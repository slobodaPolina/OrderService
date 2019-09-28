package dao;

import entity.Order;
import java.sql.SQLException;
import java.util.List;

public interface OrderDAO {
	public List<Order> getOrders() throws SQLException;
}
