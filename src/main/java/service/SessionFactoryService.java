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

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}

