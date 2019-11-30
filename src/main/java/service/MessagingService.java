package service;

import com.google.gson.*;
import com.rabbitmq.client.*;
import org.slf4j.*;

import dto.*;
import dao.*;
import entity.*;

public class MessagingService {
    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public void callReserve(long itemId, long amount, long orderId) {
        callItemService(itemId, amount, orderId, "reserveItems");
    }

    public void callRelease(long itemId, long amount) {
        callItemService(itemId, amount, null, "releaseItems");
    }

    public void callChangeAmount(long itemId, long amount) {
        callItemService(itemId, amount, null, "changeItemAmount");
    }

    private void callItemService(long itemId, long amount, Long orderId, String type) {
        logger.warn("Calling ItemService with type " + type);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection;
        Channel channel;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            
            channel.exchangeDeclare(type, "direct");
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("id", itemId);
            json.addProperty("amount", amount);
            if (orderId != null) {
                json.addProperty("orderId", orderId);
            }
            String message = json.toString();

            channel.basicPublish(
                type, "ItemService", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8")
            );
            logger.info("Called itemService " + message);
        } catch(Exception e) {
            logger.error("Failed to call item service with params type = " + type +
                            ", itemId = " + itemId + ", amount = " + amount);
        }
    }

    public void setupListener(OrderService orderService, CommonDAO commonDAO, OrderDAO orderDAO) {
        String queueName = "OrderService";
        String paymentExchangeName = "paymentPerformed";
        String itemExchangeName = "reservationFailed";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection;
        Channel channel;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(paymentExchangeName, "fanout");
            channel.exchangeDeclare(itemExchangeName, "fanout");

            channel.queueDeclare(queueName, true, false, false, null);

            channel.queueBind(queueName, paymentExchangeName, "");
            channel.queueBind(queueName, itemExchangeName, "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(message).getAsJsonObject();
                    if (obj.get("type").getAsString().equals("reservationFailed")) {
                        ReservationFailedDTO dto = new Gson().fromJson(message, ReservationFailedDTO.class);
                        OrderItem orderItem = orderDAO.getOrderItem(dto.getOrderId(), dto.getItemId());
                        if (orderItem == null) {
                            logger.error("Got reservation failed message of nonexistent combination of orderId " +
                                    dto.getOrderId() + " and itemId " + dto.getItemId() + "! Seems like the reservation has not been performed before");
                        } else {
                            orderItem.setAmount(orderItem.getAmount() - dto.getAmount());
                            commonDAO.update(orderItem);
                            logger.error(
                                    "Cannot reserve " + dto.getAmount() + " items with id " +
                                            dto.getItemId() + " to order " + dto.getOrderId() + ". ItemService rejected the operation."
                            );
                        }
                    } else {
                        PayedOrderDTO dto = new Gson().fromJson(message, PayedOrderDTO.class);
                        orderService.changeOrderStatus(
                            dto.getOrderId(),
                            dto.isPaymentSuccessful() ? Status.PAYED : Status.FAILED
                        );
                    }
                    logger.warn("finishing delivery callback");
                } finally {
                    logger.warn("trying to send basicAck");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    logger.warn("sent basicAck");
                }
            };

            channel.basicConsume(queueName, false, callback, consumerTag -> { });
        } catch(Exception e) {
            logger.error("Failed to setupListener of orderService to listen to PaymentService events");
        }
    }
}
