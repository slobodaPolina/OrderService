package dao;

import dto.OrderDTO;
import entity.Order;
import service.SessionFactoryService;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDAO {
	public List<OrderDTO> getOrders() throws SQLException {
		SessionFactoryService sfService = new SessionFactoryService();
		Session session = null;
		List<Order> orders = null;
		try {
			session = sfService.getOpenedSession();
			CriteriaQuery<Order> query = session.getCriteriaBuilder().createQuery(Order.class);
			query.from(Order.class);
			orders = session.createQuery(query).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sfService.closeSession(session);
		}
		return orders.stream().map(order -> new OrderDTO(order)).collect(Collectors.toList());
	}
}
