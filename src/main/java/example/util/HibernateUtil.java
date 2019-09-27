package example.util;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static SessionFactory sessionFactory;
	private  HibernateUtil(){}
	static {
		try {
			sessionFactory = new Configuration()
				.configure() // configures settings from hibernate.cfg.xml
				.buildSessionFactory();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
}

