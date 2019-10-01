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
        if (!itemService.reserveItems(itemAdditionParameters.getId(), itemAdditionParameters.getAmount())) {
            System.out.println("ORDER SERVICE INFO: Cannot reserve " + itemAdditionParameters.getAmount() + " items with id " + itemAdditionParameters.getId() + " to order " + orderId);
            return null;
        }
        Order order = orderId == null ?
                createEmptyOrder(itemAdditionParameters.getUsername()) :
                commonDAO.getById(orderId, Order.class);
        Item itemToAdd = commonDAO.getById(itemAdditionParameters.getId(), Item.class);
        itemToAdd.setAmount(itemAdditionParameters.getAmount());
        order.getItems().add(itemToAdd);
        orderDAO.update(order);
        System.out.println("ORDER SERVICE INFO: updated order " + orderId + " added " + itemToAdd.getAmount() + itemToAdd.getName() + " (itemId " + itemToAdd.getId() + ")");
        return new OrderDTO(order);
    }

    public OrderDTO changeOrderStatus(long orderId, Status newStatus) {
        Order order = commonDAO.getById(orderId, Order.class);
        if (order == null) {
            return null;
        }
        if ((order.getStatus().equals(Status.COLLECTING) && !(newStatus.equals(Status.FAILED) || newStatus.equals(Status.PAYED))) ||
                (order.getStatus().equals(Status.PAYED) && !(newStatus.equals(Status.SHIPPING) || newStatus.equals(Status.CANCELLED))) ||
                (order.getStatus().equals(Status.SHIPPING) && !newStatus.equals(Status.COMPLETE)) ||
                order.getStatus().equals(Status.FAILED) ||
                order.getStatus().equals(Status.COMPLETE) ||
                order.getStatus().equals(Status.CANCELLED)
        ) {
            System.out.println("ORDER SERVICE INFO: Cannot change state from " + order.getStatus() + " to " + newStatus);
            return null;
        }
        order.setStatus(newStatus);
        orderDAO.update(order);
        System.out.println("ORDER SERVICE INFO: updated state of order " + orderId + " from " + order.getStatus() + " to " + newStatus);
        if (newStatus.equals(Status.FAILED) || newStatus.equals(Status.CANCELLED)) {
            order.getItems().stream().forEach(item -> itemService.releaseItems(item.getId(), item.getAmount()));
        }
        return new OrderDTO(order);
    }
}
