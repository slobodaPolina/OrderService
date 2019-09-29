package dto;

import entity.Order;
import entity.Status;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {
    private long id;
    private Status status;
    private long totalCost;
    private long totalAmount;
    private String username;
    List<ItemDTO> items;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.status = order.getStatus();
        this.totalCost = order.calculateTotalCost();
        this.totalAmount = order.calculateTotalAmount();
        this.username = order.getUsername();
        this.items = order.getItems().stream()
                .map(item -> new ItemDTO(item))
                .collect(Collectors.toList());
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
