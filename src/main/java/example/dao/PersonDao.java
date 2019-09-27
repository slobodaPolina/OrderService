package example.dao;

import example.Person;
import java.sql.SQLException;
import java.util.List;

public interface PersonDao {
	public List<Person> getPersons() throws SQLException;
}
