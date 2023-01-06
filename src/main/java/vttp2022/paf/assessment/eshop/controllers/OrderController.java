package vttp2022.paf.assessment.eshop.controllers;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;
import vttp2022.paf.assessment.eshop.services.OrderException;
import vttp2022.paf.assessment.eshop.services.WarehouseService;

@RestController
@RequestMapping(path="/", produces=MediaType.APPLICATION_JSON_VALUE)
public class OrderController {
	
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private WarehouseService warehouseService;

	// @GetMapping
	// public ResponseEntity<String> verifyUser(@RequestBody MultiValueMap<String, String> form, HttpSession session) {

	// }

	//TODO: Task 3
	@PostMapping(path="api/order", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<String> createOrder(@RequestBody MultiValueMap<String, String> form, HttpSession session) throws OrderException {
		
		//verify the user exists
		String name = form.getFirst("name");
		System.out.println(name);
		if (customerRepository.findCustomerByName(name).isEmpty()) {
			JsonObject error = Json.createObjectBuilder()
			.add("error", "Customer %s does not exist".formatted(name))
			.build(); 
			return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(error.toString());
		} else {

			Customer customer = customerRepository.findCustomerByName(name).get();
			//Get the details from the form
			System.out.printf("customer %s name successful!".formatted(name));
			//create the order
			Order order = new Order();
			order.setName(customer.getName());
			order.setAddress(customer.getAddress());
			order.setEmail(customer.getEmail());

			//create/check for the lineItems
			List<LineItem> lineItems = (List<LineItem>) session.getAttribute("items");

			if (lineItems == null) {
				System.out.println("This is a new session");
				System.out.printf("session id= %s\n", session.getId());
				lineItems = new LinkedList<>();
				session.setAttribute("items", lineItems);
			}

			String item = form.getFirst("item");
			Integer quantity = Integer.parseInt(form.getFirst("quantity"));
			lineItems.add(new LineItem(item, quantity));
			order.setLineItems(lineItems);

			for (LineItem li: lineItems) 
				System.out.printf("description: %s, quantity: %d\n", li.getItem(), li.getQuantity());

			session.setAttribute("order", order);
			session.setAttribute("items", lineItems);

			//make the queries 
			int insertCount = orderRepository.saveOrder(order);
			
			if (insertCount == 0) {
				JsonObject error = Json.createObjectBuilder()
									.add("error", "Save was unsuccessful!")
									.build();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error.toString());
			}
			System.out.println(order.toString());

			//dispatch and get back order_status
			OrderStatus orderStatus = warehouseService.dispatch(order);

			//insert order status
			orderRepository.saveOrderStatus(orderStatus);


			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(order.toJson().toString());

		
		}
	}

	@GetMapping(path="api/order/{name}/status")
	public ResponseEntity<String> getTotalStatusByName(@PathVariable String name) {
		//make the query
		JsonObject result = orderRepository.getOrdersByName(name);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(result.toString());
	}


}
