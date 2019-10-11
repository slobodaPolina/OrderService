package dao;

import dto.OrderDTO;
import entity.Order;
import service.SessionFactoryService;
import org.hibernate.Session;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDAO {
	private SessionFactoryService sfService;

	public OrderDAO(SessionFactoryService sfService) {
		this.sfService = sfService;
	}

	public void save(Order obj) {
		Session session = null;
		try {
			session = sfService.getOpenedSession();
			session.beginTransaction();
			session.save(obj);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sfService.closeSession(session);
		}
	}

	public void update(Order obj) {
		Session session = null;
		try {
			session = sfService.getOpenedSession();
			session.beginTransaction();
			session.update(obj);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sfService.closeSession(session);
		}
	}

	public List<OrderDTO> getOrders() {
		Session session = null;
		List<Order> orders = null;
		try {
			session = sfService.getOpenedSession();
			session.beginTransaction();
			CriteriaQuery<Order> query = session.getCriteriaBuilder().createQuery(Order.class);
			query.from(Order.class);
			orders = session.createQuery(query).getResultList();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sfService.closeSession(session);
		}
		return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
	}
}
