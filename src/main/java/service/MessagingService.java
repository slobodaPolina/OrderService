package service;

import com.google.gson.JsonObject;
import com.rabbitmq.client.*;
import org.slf4j.*;
import com.google.gson.Gson;

import dto.*;
import entity.*;

public class MessagingService {
    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public static void callItemService(boolean isReserve, long itemId, long amount) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("reserveItems", "direct");
            JsonObject json = new JsonObject();
            json.addProperty("type", "reserveItems");
            json.addProperty("isReserve", isReserve);
            json.addProperty("itemId", itemId);
            json.addProperty("amount", amount);
            String message = json.toString();

            channel.basicPublish(
                "reserveItems",
                "",
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes("UTF-8")
            );
            logger.info("Called itemService " + message);
        } catch(Exception e) {
            logger.error("Failed to call item service with params isReserve = " + isReserve +
                            ", itemId = " + itemId + ", amount = " + amount);
        }
    }

    public static void setupListener(OrderService orderService) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("performPayment", "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "performPayment", "");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                PayedOrderDTO dto = new Gson().fromJson(message, PayedOrderDTO.class);
                orderService.changeOrderStatus(
                    dto.getOrderId(),
                    dto.isPayed() ? Status.PAYED : Status.FAILED
                );
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch(Exception e) {
            logger.error("Failed to setupListener of orderService to listen to PaymentService events");
        }
    }
}
