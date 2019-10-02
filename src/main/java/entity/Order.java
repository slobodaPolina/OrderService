package entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Order implements Serializable {

	private long id;
	private Status status;
	private String username;
	private Set<OrderItem> orderItems = new HashSet<OrderItem>();

	public Order() {}

	public Order(String username) {
		this.username = username;
		this.status = Status.COLLECTING;
		this.orderItems = new HashSet<OrderItem>();
	}

	public long calculateTotalCost() {
		return sumFields(orderItem -> orderItem.getAmount() * orderItem.getId().getItem().getPrice());
	}

	public long calculateTotalAmount() {
		return sumFields(orderItem -> orderItem.getAmount());
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

	public Set<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Set<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	private long sumFields(Function<OrderItem, Long> mapper) {
		if (orderItems.isEmpty()) {
			return 0;
		}
		return orderItems.stream().map(mapper).reduce((long) 0, (a, b) -> a + b);
	}
} 
