package service;

import com.google.gson.JsonObject;
import com.rabbitmq.client.*;
import org.slf4j.*;
import com.google.gson.Gson;

import dto.*;
import entity.*;

public class MessagingService {
    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public static void callReserve(long itemId, long amount, long orderId) {
        callItemService(itemId, amount, orderId, "reserveItems");
    }

    public static void callRelease(long itemId, long amount) {
        callItemService(itemId, amount, null, "releaseItems");
    }

// todo add the call somewhere
    public static void callChangeAmount(long itemId, long amount) {
        callItemService(itemId, amount, null, "changeAmount");
    }

    private static void callItemService(long itemId, long amount, Long orderId, String type) {
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

    public static void setupListener(OrderService orderService) {
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

            DeliverCallback paymentCallback = (consumerTag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    PayedOrderDTO dto = new Gson().fromJson(message, PayedOrderDTO.class);
                    orderService.changeOrderStatus(
                        dto.getOrderId(),
                        dto.isPaymentSuccessful() ? Status.PAYED : Status.FAILED
                    );
                } finally {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            DeliverCallback itemCallback = (consumerTag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    ReservationFailedDTO dto = new Gson().fromJson(message, ReservationFailedDTO.class);
                    // todo remove from db failed data
                    /*logger.error(
                            "Cannot reserve " + itemAdditionParameters.getAmount() + " items with id " +
                                itemAdditionParameters.getId() + " to order " + orderId + ". ItemService rejected the operation."
                    );*/
                } finally {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            channel.basicConsume(queueName, false, paymentCallback, consumerTag -> { });
            channel.basicConsume(queueName, false, itemCallback, consumerTag -> { });
        } catch(Exception e) {
            logger.error("Failed to setupListener of orderService to listen to PaymentService events");
        }
    }
}
