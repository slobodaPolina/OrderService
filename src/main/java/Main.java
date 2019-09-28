import static spark.Spark.*;
import dao.OrderDAOImpl;
import java.lang.*;
import com.cedarsoftware.util.io.*;

public class Main {
    public static void main(String[] args) {
    	port(1809);
		get("/orders", (req, res) -> {
			return JsonWriter.objectToJson(new OrderDAOImpl().getOrders()); // TODO change to DTO list
		});
		// TODO add other
    }
}
