package dao;

import dto.OrderDTO;
import entity.Order;
import service.SessionFactoryService;
import org.hibernate.Session;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDAO {
	private SessionFactoryService sfService = new SessionFactoryService();

	public List<OrderDTO> getOrders() {
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

	public OrderDTO getOrderById(long orderId) {
		Session session = null;
		Order order = null;
		try {
			session = sfService.getOpenedSession();
			CriteriaQuery<Order> query = session.getCriteriaBuilder().createQuery(Order.class);
			Root<Order> root = query.from(Order.class);
			query.select(root).where(root.get("id").in(orderId));
			order = getSingleResult(session, query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sfService.closeSession(session);
		}
		return order == null ? null : new OrderDTO(order);
	}

	public OrderDTO createOrder() {
		return null;
	}

	private <T> T getSingleResult(Session session, CriteriaQuery<T> query) {
		List<T> results = session.createQuery(query).getResultList();
		return !results.isEmpty() ? results.get(0) : null;
	}
}
