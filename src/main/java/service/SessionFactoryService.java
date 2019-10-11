package service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryService {
	private static SessionFactory sessionFactory;
	static {
		try {
			// configures settings from hibernate.cfg.xml
			sessionFactory = new Configuration().configure().buildSessionFactory();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Session getOpenedSession() {
		return getSessionFactory().openSession();
	}

	public void closeSession(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}
}

