package dao;

import dto.*;
import entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDAO {
    private SessionFactory sessionFactory;
    public OrderDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	public List<OrderDTO> getOrders() {
		List<Order> orders = null;
		try (Session session = sessionFactory.openSession()) {
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

	public OrderItem getOrderItem(long orderId, long itemId) {
		OrderItem orderItem = null;
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();
			CriteriaQuery<OrderItem> query = session.getCriteriaBuilder().createQuery(OrderItem.class);
			Root<OrderItem> root = query.from(OrderItem.class);
            query.select(root).where(
				root.get("id").get("order").get("id").in(orderId),
				root.get("id").get("item").get("id").in(itemId)
			);
			List<OrderItem> results = session.createQuery(query).getResultList();
            orderItem = !results.isEmpty() ? results.get(0) : null;
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderItem;
	}

    public void updateOrderItemAmount(OrderItem orderItem, long newAmount) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CriteriaUpdate<OrderItem> query = session.getCriteriaBuilder().createCriteriaUpdate(OrderItem.class);
            Root e = query.from(OrderItem.class);
            query.set("amount", newAmount);
            query.where(e.get("id").in(orderItem.getId()));
            session.createQuery(query).executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
