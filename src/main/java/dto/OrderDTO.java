package dto;

import entity.Order;
import entity.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {
    private long id;
    private Status status;
    private long totalCost;
    private long totalAmount;
    private String username;
    private List<ItemDTO> items;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.status = order.getStatus();
        this.totalCost = order.calculateTotalCost();
        this.totalAmount = order.calculateTotalAmount();
        this.username = order.getUsername();
        this.items =  order.getOrderItems().isEmpty() ? new ArrayList<>() :
                order.getOrderItems().stream()
                .map(orderItem -> new ItemDTO(orderItem.getId().getItem(), orderItem.getAmount()))
                .collect(Collectors.toList());
    }

    public String toString() {
        String itemsStr = items.stream().map(ItemDTO::toString).collect(Collectors.joining(", "));
        return "{id: " + id + ", status: " + status + ", username: " + username + ", items: [" + itemsStr + "]}";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
}
