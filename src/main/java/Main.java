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

		exception(Exception.class, (exception, request, response) -> {
			exception.printStackTrace();
		});

		get("/api/orders", (req, res) -> orderDAO.getOrders());

		post("/api/orders/:username", (req, res) ->
			orderService.createEmptyOrder(req.params("username"))
		);

		get("/api/orders/:orderId", (req, res) ->
			orderDAO.getOrderById(Long.parseLong(req.params("orderId")))
		);

		put("/api/orders/:orderId/status/:status", (req, res) ->
			orderService.changeOrderStatus(
					Long.parseLong(req.params("orderId")),
					Status.valueOf(req.params("status"))
			)
		);

		post("/api/orders/:orderId/item", (req, res) ->
			orderService.addItemToOrder(
					Long.parseLong(req.params("orderId")),
					new Gson().fromJson(req.body(), ItemAdditionParametersDTO.class)
			)
		);
    }
}
