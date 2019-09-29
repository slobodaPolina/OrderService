import static spark.Spark.*;

import dao.OrderDAO;

public class Main {
	private static OrderDAO orderDAO = new OrderDAO();

    public static void main(String[] args) {
    	port(1809);
		get("/orders", (req, res) -> { return orderDAO.getOrders(); });
		// TODO add other
    }
}
