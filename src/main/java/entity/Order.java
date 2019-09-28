package entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="orders")
public class Order {
	@Id
	@Column(name="id")
	private long id;

	@Column(name="status")
	private Status status;

	@Column(name="username")
	private String username;

	// TODO annotate to get items of ids and amounts
	private List<Item> items;

	public long calculateTotalCost() {
		return 0; // TODO
	}

	public long calculateTotalAmount() {
		return 0; //TODO
	};

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
