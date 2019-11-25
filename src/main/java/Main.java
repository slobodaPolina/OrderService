import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.*;
import dto.*;
import entity.Status;
import service.*;

import java.util.stream.Collectors;

public class Main {
	private static OrderService orderService = new OrderService(new OrderDAO(), new CommonDAO());

	private static GsonBuilder builder = new GsonBuilder();
	private static Gson gson = builder.create();

    public static void main(String[] args) {
		MessagingService.setupListener(orderService, new CommonDAO(), new OrderDAO());

    	port(1809);

		get(
			"/api/orders",
			(req, res) ->
				orderService.getOrders().stream()
					.map(orderDTO -> gson.toJson(orderDTO))
					.collect(Collectors.toList())
		);

		post("/api/orders/:username", (req, res) ->
			gson.toJson(
				new OrderDTO(
					orderService.createEmptyOrder(req.params("username"))
				)
			)
		);

		get("/api/orders/:orderId", (req, res) ->
			gson.toJson(
				orderService.getOrderDTOById(
					Long.parseLong(req.params("orderId"))
				)
			)
		);

		put("/api/orders/:orderId/status/:status", (req, res) ->
			gson.toJson(
				orderService.changeOrderStatus(
					Long.parseLong(req.params("orderId")),
					Status.valueOf(req.params("status"))
				)
			)
		);

		post("/api/orders/:orderId/item", (req, res) ->
			gson.toJson(
				orderService.addItemToOrder(
					parseLong(req.params("orderId")),
					new Gson().fromJson(req.body(), ItemAdditionParametersDTO.class)
				)
			)
		);
    }

	private static Long parseLong(String s) {
		if (s == null || s.equals("") || s.toLowerCase().equals("null")) {
			return null;
		}
		return Long.parseLong(s);
	}
}
