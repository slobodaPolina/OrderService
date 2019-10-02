package service;

import dao.*;
import dto.*;
import entity.*;

public class OrderService {
    private OrderDAO orderDAO = new OrderDAO();
    private CommonDAO commonDAO = new CommonDAO();
    private ItemService itemService = new ItemService();

    public Order createEmptyOrder(String username) {
        Order order = new Order(username);
        orderDAO.save(order);
        System.out.println("ORDER SERVICE INFO: Created on order for " + username);
        return order;
    }

    public OrderDTO createEmptyOrderDTO(String username) {
        return new OrderDTO(createEmptyOrder(username));
    }

    public OrderDTO getOrderDTOById(long orderId) {
        Order order = commonDAO.getById(orderId, Order.class);
        return order == null ? null : new OrderDTO(order);
    }

    public OrderDTO addItemToOrder(Long orderId, ItemAdditionParametersDTO itemAdditionParameters) {
        Item itemToAdd = commonDAO.getById(itemAdditionParameters.getId(), Item.class);
        if (itemToAdd == null) {
            System.out.println("ORDER SERVICE INFO: Cannot add items with id " + itemAdditionParameters.getId() + " - nonexistent!");
            return null;
        }
        Order order = null;
        if (orderId != null) {
            order = commonDAO.getById(orderId, Order.class);
            if(order == null) {
                System.out.println(
                        "ORDER SERVICE INFO: Cannot reserve " + itemAdditionParameters.getAmount() + " items with id " +
                                itemAdditionParameters.getId() + " to order " + orderId + ". Such order does not exist!"
                );
                return null;
            }
        }
        if (!itemService.reserveItems(itemAdditionParameters.getId(), itemAdditionParameters.getAmount())) {
            System.out.println(
                    "ORDER SERVICE INFO: Cannot reserve " + itemAdditionParameters.getAmount() + " items with id " +
                        itemAdditionParameters.getId() + " to order " + orderId
            );
            return null;
        }
        if (orderId == null) {
            order = createEmptyOrder(itemAdditionParameters.getUsername());
        }
        order.getOrderItems().add(new OrderItem(order, itemToAdd, itemAdditionParameters.getAmount()));
        orderDAO.update(order);
        System.out.println(
                "ORDER SERVICE INFO: updated order " + order.getId() + " added " + itemAdditionParameters.getAmount() + " " +
                        itemToAdd.getName() + " (itemId " + itemToAdd.getId() + ")"
        );
        return new OrderDTO(order);
    }

    public OrderDTO changeOrderStatus(long orderId, Status newStatus) {
        Order order = commonDAO.getById(orderId, Order.class);
        if (order == null) {
            return null;
        }
        Status oldStatus = order.getStatus();
        if ((oldStatus.equals(Status.COLLECTING) && !(newStatus.equals(Status.FAILED) || newStatus.equals(Status.PAYED))) ||
                (oldStatus.equals(Status.PAYED) && !(newStatus.equals(Status.SHIPPING) || newStatus.equals(Status.CANCELLED))) ||
                (oldStatus.equals(Status.SHIPPING) && !newStatus.equals(Status.COMPLETE)) ||
                oldStatus.equals(Status.FAILED) ||
                oldStatus.equals(Status.COMPLETE) ||
                oldStatus.equals(Status.CANCELLED)
        ) {
            System.out.println("ORDER SERVICE INFO: Cannot change state from " + oldStatus + " to " + newStatus);
            return null;
        }
        order.setStatus(newStatus);
        orderDAO.update(order);
        System.out.println("ORDER SERVICE INFO: updated state of order " + orderId + " from " + oldStatus + " to " + newStatus);
        if (newStatus.equals(Status.FAILED) || newStatus.equals(Status.CANCELLED)) {
            order.getOrderItems().stream().forEach(
                    orderItem -> itemService.releaseItems(orderItem.getId().getItem().getId(), orderItem.getAmount())
            );
        }
        return new OrderDTO(order);
    }
}
