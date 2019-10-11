package dto;

import entity.Item;

public class ItemDTO {
    private long id;
    private String name;
    private long price;
    /*
    amount of items in the order (NOT IN WAREHOUSE)
     */
    private long amount;

    ItemDTO(Item item, long amount) {
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.amount = amount;
    }

    public String toString() {
        return "{id: " + id + ", name: " + name + ", price: " + price + ", amount: " + amount + "}";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
