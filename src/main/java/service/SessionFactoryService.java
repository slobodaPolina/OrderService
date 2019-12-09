package service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryService {
	public static SessionFactory getSessionFactory() {
		return new Configuration().configure().buildSessionFactory();
	}
}
