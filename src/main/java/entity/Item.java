package entity;

import dto.ItemAdditionParametersDTO;
import dto.ItemDTO;

public class Item {
    private long id;
    private String name;
    private long price;
    /*
    here it is amount of elements IN ORDER (NOT IN WAREHOUSE)
    it is empty until set explicitly
     */
    private long amount;

    public Item() {}

    public Item(long id, String name, long price, long amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
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
