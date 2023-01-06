package vttp2022.paf.assessment.eshop.services;

import java.io.StringReader;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

@Service
public class WarehouseService {

	// You cannot change the method's signature
	// You may add one or more checked exceptions
	public OrderStatus dispatch(Order order) {
		// TODO: Task 4
		final String SERVER_URL = "http://paf.chuklee.com/dispatch";

		String dispactchUrl = UriComponentsBuilder.fromUriString(SERVER_URL).toUriString();
	
		RequestEntity<Void> req = RequestEntity.get(dispactchUrl)
								.accept(MediaType.APPLICATION_JSON)
								.build();
	
		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity(dispactchUrl, String.class);
		String payload = resp.getBody();
		
		//parse string as JsonObject 
		JsonReader reader =Json.createReader(new StringReader(payload));
		JsonObject result = reader.readObject(); 
		OrderStatus orderStatus = new OrderStatus();
		orderStatus.setDeliveryId(result.getString("deliveryId"));
		orderStatus.setOrderId(result.getString("orderId"));
		

		return orderStatus;
		

	}
}
