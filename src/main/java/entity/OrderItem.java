package entity;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private OrderItemMapping id;
    private long amount;

    public OrderItem() {}

    public OrderItem(Order order, Item item, long amount) {
        this.id = new OrderItemMapping(order, item);
        this.amount = amount;
    }

    public OrderItemMapping getId() {
        return id;
    }

    public void setId(OrderItemMapping id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
