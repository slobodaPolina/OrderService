package service;

import dao.OrderDAO;
import dto.ItemAdditionParametersDTO;
import dto.OrderDTO;
import entity.Order;
import entity.Status;

public class OrderService { // TODO realize the logic
    private OrderDAO orderDAO = new OrderDAO();

    public OrderDTO createEmptyOrder(String username) {
        Order order = new Order(username);
        orderDAO.save(order);
        return new OrderDTO(order);
    }

    public OrderDTO addItemToOrder(long orderId, ItemAdditionParametersDTO itemAdditionParameters) {
        return null;
    }

    public OrderDTO changeOrderStatus(long orderId, Status status) {
        return null;
    }
}
