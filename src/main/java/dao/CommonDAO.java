package dao;

import org.hibernate.Session;
import service.SessionFactoryService;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CommonDAO {
    private SessionFactoryService sfService;

    public CommonDAO(SessionFactoryService sfService) {
        this.sfService = sfService;
    }

    public <T> T getById(long id, Class<T> clazz) {
        Session session = null;
        T t = null;
        try {
            session = sfService.getOpenedSession();
            session.beginTransaction();
            CriteriaQuery<T> query = session.getCriteriaBuilder().createQuery(clazz);
            Root<T> root = query.from(clazz);
            query.select(root).where(root.get("id").in(id));
            List<T> results = session.createQuery(query).getResultList();
            t = !results.isEmpty() ? results.get(0) : null;
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sfService.closeSession(session);
        }
        return t;
    }
}
