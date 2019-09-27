package example.dao;

import example.Person;
import example.dao.PersonDao;
import example.util.HibernateUtil;
import org.hibernate.Session;
import java.sql.SQLException;
import java.util.List;

public class PersonDaoImpl implements PersonDao {
	@Override
	public List<Person> getPersons() throws SQLException {
		List<Person> persons = null;
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			persons = session.createCriteria(Person.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return persons;
	}
}
