package entity;

import java.util.ArrayList;
import java.util.List;

public class Order {

	private long id;
	private Status status;
	private String username;
	private List<Item> items;

	public Order() {}

	public Order(String username) {
		this.username = username;
		this.status = Status.COLLECTING;
		this.items = new ArrayList<Item>();
	}

	public long calculateTotalCost() {
		return items.stream().map(item -> item.getAmount() * item.getPrice()).reduce((long) 0, (a, b) -> a + b);
	}

	public long calculateTotalAmount() {
		return items.stream().map(item -> item.getAmount()).reduce((long) 0, (a, b) -> a + b);
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
} 
