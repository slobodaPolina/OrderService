package service;

import com.google.gson.*;
import com.rabbitmq.client.*;
import org.slf4j.*;

import dto.*;
import dao.*;
import entity.*;

public class MessagingService {
    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public static void callReserve(long itemId, long amount, long orderId) {
        callItemService(itemId, amount, orderId, "reserveItems");
    }

    public static void callRelease(long itemId, long amount) {
        callItemService(itemId, amount, null, "releaseItems");
    }

    public static void callChangeAmount(long itemId, long amount) {
        callItemService(itemId, amount, null, "changeItemAmount");
    }

    private static void callItemService(long itemId, long amount, Long orderId, String type) {
        logger.warn("Calling ItemService with type " + type);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(type, "direct");
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("itemId", itemId);
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

    public static void setupListener(OrderService orderService, CommonDAO commonDAO, OrderDAO orderDAO) {
        String queueName = "OrderService";
        String paymentExchangeName = "paymentPerformed";
        String itemExchangeName = "reservationFailed";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel()) {

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
                        orderItem.setAmount(orderItem.getAmount() - dto.getAmount());
                        commonDAO.update(orderItem);
                        logger.error(
                                "Cannot reserve " + dto.getAmount() + " items with id " +
                                    dto.getItemId() + " to order " + dto.getOrderId() + ". ItemService rejected the operation."
                        );
                    } else {
                        PayedOrderDTO dto = new Gson().fromJson(message, PayedOrderDTO.class);
                        orderService.changeOrderStatus(
                            dto.getOrderId(),
                            dto.isPaymentSuccessful() ? Status.PAYED : Status.FAILED
                        );
                        Order order = commonDAO.getById(dto.getOrderId(), Order.class);
                        logger.warn("got order with " + dto.getOrderId());
                        if (order.getOrderItems() != null) {
                            order.getOrderItems().forEach(orderItem -> {
                                long itemId = orderItem.getId().getItem().getId();
                                logger.error("trying to release itemId " + itemId);
                                callRelease(itemId, orderItem.getAmount()); //anyway these items are not booked any more
                                if (dto.isPaymentSuccessful()) {
                                    callChangeAmount(itemId, orderItem.getAmount() * (-1)); // if they were bought, they are not at warehouse
                                }
                            });
                        }
                    }
                    logger.warn("finishing delivery callback");
                } finally {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    logger.warn("sended basicAck");
                }
            };

            channel.basicConsume(queueName, false, callback, consumerTag -> { });
        } catch(Exception e) {
            logger.error("Failed to setupListener of orderService to listen to PaymentService events");
        }
    }
}
