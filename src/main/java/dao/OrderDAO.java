package dao;

import dto.OrderDTO;
import entity.Order;
import service.SessionFactoryService;
import org.hibernate.Session;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDAO {
	public void save(Order obj) {
		try (Session session = SessionFactoryService.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.save(obj);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(Order obj) {
		try (Session session = SessionFactoryService.getSessionFactory().openSession()) {
			session.beginTransaction();
			session.update(obj);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<OrderDTO> getOrders() {
		List<Order> orders = null;
		try (Session session = SessionFactoryService.getSessionFactory().openSession()) {
			session.beginTransaction();
			CriteriaQuery<Order> query = session.getCriteriaBuilder().createQuery(Order.class);
			query.from(Order.class);
			orders = session.createQuery(query).getResultList();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
	}
}
