package service;

import dao.OrderDAO;
import dto.ItemAdditionParametersDTO;
import dto.OrderDTO;
import entity.Order;
import entity.Status;

public class OrderService { // TODO realize the logic
    private OrderDAO orderDAO = new OrderDAO();
    private ItemService itemService = new ItemService();

    public OrderDTO createEmptyOrder(String username) {
        Order order = new Order(username);
        orderDAO.save(order);
        System.out.println("ORDER SERVICE INFO: Created on order for " + username);
        return new OrderDTO(order);
    }

    public OrderDTO getOrderDTOById(long orderId) {
        Order order = orderDAO.getOrderById(orderId);
        return order == null ? null : new OrderDTO(order);
    }

    public OrderDTO addItemToOrder(long orderId, ItemAdditionParametersDTO itemAdditionParameters) {
        return null;
    }

    public OrderDTO changeOrderStatus(long orderId, Status newStatus) {
        Order order = orderDAO.getOrderById(orderId);
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
        order.setStatus(newStatus); //TODO check it works
        if (newStatus.equals(Status.FAILED) || newStatus.equals(Status.CANCELLED)) {
            order.getItems().stream().forEach(item -> itemService.releaseItems(item.getId(), item.getAmount()));
        }
        return new OrderDTO(order);
    }
}
