import static spark.Spark.*;

import com.google.gson.Gson;
import dao.OrderDAO;
import dto.ItemAdditionParametersDTO;
import entity.Status;
import service.OrderService;

public class Main {
	private static OrderDAO orderDAO = new OrderDAO();
	private static OrderService orderService = new OrderService();

    public static void main(String[] args) {
    	port(1809);

		get("/orders", (req, res) -> orderDAO.getOrders());
		post("/orders", (req, res) -> orderDAO.createOrder());

		get("/orders/:orderId", (req, res) ->
			orderDAO.getOrderById(Long.parseLong(req.params("orderId")))
		);

		put("/orders/:orderId/status/:status", (req, res) ->
			orderService.changeOrderStatus(
					Long.parseLong(req.params("orderId")),
					Status.valueOf(req.params("status"))
			)
		);

		post("/orders/:orderId/item", (req, res) ->
			orderService.addItemToOrder(
					Long.parseLong(req.params("orderId")),
					new Gson().fromJson(req.body(), ItemAdditionParametersDTO.class)
			)
		);
    }
}
