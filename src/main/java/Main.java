import static spark.Spark.*;

import com.google.gson.*;
import dao.*;
import dto.*;
import entity.*;
import service.*;

import java.util.stream.Collectors;

public class Main {
	private static MessagingService messagingService = new MessagingService();
	private static OrderService orderService = new OrderService(new OrderDAO(), new CommonDAO(), messagingService);

	private static GsonBuilder builder = new GsonBuilder();
	private static Gson gson = builder.create();

    public static void main(String[] args) {
		messagingService.setupListener(orderService, new CommonDAO(), new OrderDAO());

    	port(1809);

		get(
			"/api/orders",
			(req, res) ->
				orderService.getOrders().stream()
					.map(orderDTO -> gson.toJson(orderDTO))
					.collect(Collectors.toList())
		);

		post("/api/orders/:username", (req, res) -> {
			try {
				return gson.toJson(
					new OrderDTO(
						orderService.createEmptyOrder(req.params("username"))
					)
				);
			} catch (IllegalArgumentException e) {
				return gson.toJson(e.getMessage());
			}
		});

		get("/api/orders/:orderId", (req, res) -> {
			try {
				return gson.toJson(
					orderService.getOrderDTOById(
						Long.parseLong(req.params("orderId"))
					)
				);
			} catch (IllegalArgumentException e) {
				return gson.toJson(e.getMessage());
			}
		});

		put("/api/orders/:orderId/status/:status", (req, res) -> {
			try {
				return gson.toJson(
					orderService.changeOrderStatus(
						Long.parseLong(req.params("orderId")),
						Status.valueOf(req.params("status"))
					)
				);
			} catch (IllegalArgumentException e) {
				return gson.toJson(e.getMessage());
			}
		});

		post("/api/orders/:orderId/item", (req, res) -> {
			try {
				return gson.toJson(
					orderService.addItemToOrder(
						parseLong(req.params("orderId")),
						new Gson().fromJson(req.body(), ItemAdditionParametersDTO.class)
					)
				);
			} catch (IllegalArgumentException e) {
				return gson.toJson(e.getMessage());
			}
		});
    }

	private static Long parseLong(String s) {
		if (s == null || s.equals("") || s.toLowerCase().equals("null")) {
			return null;
		}
		return Long.parseLong(s);
	}
}
