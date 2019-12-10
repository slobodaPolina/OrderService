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

		post("/api/orders/:username", (req, res) ->
			getJson(
				new OrderDTO(
					orderService.createEmptyOrder(req.params("username"))
				)
			)
		);

		get("/api/orders/:orderId", (req, res) ->
			getJson(
				orderService.getOrderDTOById(
					Long.parseLong(req.params("orderId"))
				)
			)
		);

		put("/api/orders/:orderId/status/:status", (req, res) ->
			getJson(
				orderService.changeOrderStatus(
					Long.parseLong(req.params("orderId")),
					Status.valueOf(req.params("status"))
				)
			)
		);

		post("/api/orders/:orderId/item", (req, res) ->
			getJson(
				orderService.addItemToOrder(
					parseLong(req.params("orderId")),
					new Gson().fromJson(req.body(), ItemAdditionParametersDTO.class)
				)
			)
		);
    }

	private static String getJson(OrderDTO dto) {
    	try {
    		return gson.toJson(dto);
		} catch(IllegalArgumentException e) {
    		return gson.toJson(e.getMessage());
		}
	}

	private static Long parseLong(String s) {
		if (s == null || s.equals("") || s.toLowerCase().equals("null")) {
			return null;
		}
		return Long.parseLong(s);
	}
}
