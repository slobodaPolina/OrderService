package dao;

import org.hibernate.SessionFactory;

import org.hibernate.Session;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CommonDAO {
    private SessionFactory sessionFactory;
    public CommonDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T getById(long id, Class<T> clazz) {
        T t = null;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CriteriaQuery<T> query = session.getCriteriaBuilder().createQuery(clazz);
            Root<T> root = query.from(clazz);
            query.select(root).where(root.get("id").in(id));
            List<T> results = session.createQuery(query).getResultList();
            t = !results.isEmpty() ? results.get(0) : null;
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public <T> void save(T obj) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void update(T obj) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(obj);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
