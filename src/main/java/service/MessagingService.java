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
        sendEvent(itemId, amount, orderId, "itemAddedToOrder");
    }

    public void callRelease(long itemId, long amount) {
        sendEvent(itemId, amount, null, "itemRemovedFromOrder");
    }

    public void callRemoveFromWarehouse(long itemId, long amount) {
        sendEvent(itemId,amount,null, "itemBought");
    }

    private void sendEvent(long itemId, long amount, Long orderId, String type) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection;
        Channel channel;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(type, "fanout");

            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("id", itemId);
            json.addProperty("amount", amount);
            if (orderId != null) {
                json.addProperty("orderId", orderId);
            }
            String message = json.toString();

            channel.basicPublish(type, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            logger.info("OrderService have sent event '" + message + "'");
        } catch (Exception e) {
            logger.error("OrderService failed to send event with itemId(id) " + itemId + " amount " + amount + " orderId " + orderId + " of type " + type);
        }
    }

        public void setupListener(OrderService orderService, CommonDAO commonDAO, OrderDAO orderDAO) {
        String queueName = "OrderService";
        String paymentExchangeName = "paymentPerformed";
        String reservationFailedExchangeName = "reservationFailed";
        String itemAddedExchangeName = "itemAdded";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection;
        Channel channel;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(paymentExchangeName, "fanout");
            channel.exchangeDeclare(reservationFailedExchangeName, "fanout");
            channel.exchangeDeclare(itemAddedExchangeName, "fanout");

            channel.queueDeclare(queueName, true, false, false, null);

            channel.queueBind(queueName, paymentExchangeName, "");
            channel.queueBind(queueName, reservationFailedExchangeName, "");
            channel.queueBind(queueName, itemAddedExchangeName, "");

            DeliverCallback callback = (consumerTag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(message).getAsJsonObject();
                    if (obj.get("type").getAsString().equals(reservationFailedExchangeName)) {
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
                    } else if (obj.get("type").getAsString().equals(itemAddedExchangeName)) {
                        ItemDTO dto = new Gson().fromJson(message, ItemDTO.class);
                        Item item = new Item(dto);
                        commonDAO.save(item);
                    } else {
                        PayedOrderDTO dto = new Gson().fromJson(message, PayedOrderDTO.class);
                        orderService.changeOrderStatus(
                            dto.getOrderId(),
                            dto.isPaymentSuccessful() ? Status.PAYED : Status.FAILED
                        );
                    }
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (Exception e) {
                    logger.error("Problems during delivery callback. Got the message but failed to deal with");
                    e.printStackTrace();
                }
            };

            channel.basicConsume(queueName, false, callback, consumerTag -> { });
        } catch(Exception e) {
            logger.error("Failed to setupListener of orderService to listen to PaymentService events");
        }
    }
}
