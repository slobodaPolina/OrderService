package example;

import static spark.Spark.get;
import example.dao.PersonDaoImpl;
import example.dao.PersonDao;
import example.Person;
import java.util.List;
import java.lang.*;
import com.cedarsoftware.util.io.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class Main {
    public static void main(String[] args) {
	get("/persons", (req, res) -> {
		PersonDao pa = new PersonDaoImpl();
		List<Person> la =  pa.getPersons();		

		return JsonWriter.objectToJson(la);
		
	});
    }
} 
