package entity;

import java.util.List;

public class Order {

	private long id;
	private Status status;
	private String username;
	private List<Item> items;

	public long calculateTotalCost() {
		return 0; // TODO
	}

	public long calculateTotalAmount() {
		return 0; //TODO
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
