package service;

import dao.*;
import dto.*;
import entity.*;
import org.slf4j.*;
import java.util.*;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private OrderDAO orderDAO;
    private CommonDAO commonDAO;
    private MessagingService messagingService;

    public OrderService(OrderDAO orderDAO, CommonDAO commonDAO, MessagingService messagingService) {
        this.orderDAO = orderDAO;
        this.commonDAO = commonDAO;
        this.messagingService = messagingService;
    }

    public List<OrderDTO> getOrders() {
        return orderDAO.getOrders();
    }

    public Order createEmptyOrder(String username) {
        Order order = new Order(username);
        commonDAO.save(order);
        logger.info("Created on order for " + username);
        return order;
    }

    public OrderDTO getOrderDTOById(long orderId) throws IllegalArgumentException {
        Order order = commonDAO.getById(orderId, Order.class);
        if (order != null) {
            return new OrderDTO(order);
        }
        logger.error("No order with orderId {} found!", orderId);
        throw new IllegalArgumentException("No order with orderId " + orderId + " found!");
    }

    public OrderDTO addItemToOrder(Long orderId, ItemAdditionParametersDTO itemAdditionParameters) throws IllegalArgumentException {
        Item itemToAdd = commonDAO.getById(itemAdditionParameters.getId(), Item.class);
        if (itemToAdd == null) {
            logger.error("Cannot add items with id " + itemAdditionParameters.getId() + " - there is no item with such id!");
            throw new IllegalArgumentException("Cannot add items with id " + itemAdditionParameters.getId() + " - there is no item with such id!");
        }

        Order order;
        if (orderId != null) {
            order = commonDAO.getById(orderId, Order.class);
            if(order == null) {
                logger.error("Cannot reserve " + itemAdditionParameters.getAmount() + " items with id " +
                        itemAdditionParameters.getId() + " to order " + orderId + ". Such order does not exist!");
                throw new IllegalArgumentException("Cannot reserve " + itemAdditionParameters.getAmount() + " items with id " +
                        itemAdditionParameters.getId() + " to order " + orderId + ". Such order does not exist!");
            }
        } else {
            order = createEmptyOrder(itemAdditionParameters.getUsername());
        }

        messagingService.callReserve(itemAdditionParameters.getId(), itemAdditionParameters.getAmount(), order.getId());
        OrderItem orderItem = orderDAO.getOrderItem(order.getId(), itemAdditionParameters.getId());
        if (orderItem != null) {
            orderItem.setAmount(orderItem.getAmount() + itemAdditionParameters.getAmount());
            // Здесь ты поменяла свой orderItem, но в order его не добавила. Нужно удалить из order старый orderItem и добавить новый
        } else {
            order.getOrderItems().add(new OrderItem(order, itemToAdd, itemAdditionParameters.getAmount()));
        }
        commonDAO.update(order);
        logger.info(
                "Updated order " + order.getId() + " added " + itemAdditionParameters.getAmount() + " " +
                        itemToAdd.getName() + " (itemId " + itemToAdd.getId() + ")"
        );
        return new OrderDTO(order);
    }

    public OrderDTO changeOrderStatus(long orderId, Status newStatus) throws IllegalArgumentException {
        Order order = commonDAO.getById(orderId, Order.class);
        if (order == null) {
            logger.error("No order with orderId " + orderId + " found!");
            throw new IllegalArgumentException("No order with orderId " + orderId + " found!");
        }
        Status oldStatus = order.getStatus();
        if (!oldStatus.nextStatus().contains(newStatus)) {
            logger.error("Cannot change state from " + oldStatus + " to " + newStatus + " for the order " + orderId);
            throw new IllegalArgumentException("Cannot change state from " + oldStatus + " to " + newStatus + " for the order " + orderId);
        }
        order.setStatus(newStatus);
        commonDAO.update(order);
        logger.info("Updated state of order " + orderId + " from " + oldStatus + " to " + newStatus);
        if (newStatus.equals(Status.FAILED) || newStatus.equals(Status.CANCELLED) || newStatus.equals(Status.PAYED)) {
            order.getOrderItems().forEach(
                orderItem -> {
                    long itemId = orderItem.getId().getItem().getId();
                    messagingService.callRelease(itemId, orderItem.getAmount());
                    if (newStatus.equals(Status.PAYED)) {
                        messagingService.callRemoveFromWarehouse(itemId, orderItem.getAmount());
                    }
                }
            );
        }
        return new OrderDTO(order);
    }
}
