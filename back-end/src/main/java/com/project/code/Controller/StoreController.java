package com.project.code.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;

@RestController
@RequestMapping("/store")
public class StoreController {

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private OrderService orderService;

	@PostMapping
	public Map<String, String> addStore(@RequestBody Store store) {
		Store savedStore = storeRepository.save(store);
		Map<String, String> response = new HashMap<>();
		response.put("message", "Store created successfully with ID: " + savedStore.getId());
		return response;
	}

	@GetMapping("/validate/{storeId}")
	public boolean validateStore(@PathVariable Long storeId) {
		return storeRepository.findById(storeId).isPresent();
	}

	@PostMapping("/placeOrder")
	public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequestDTO) {
		Map<String, String> response = new HashMap<>();
		try {
			orderService.saveOrder(placeOrderRequestDTO);
			response.put("message", "Order placed successfully");
		} catch (Exception e) {
			response.put("Error", e.getMessage());
		}
		return response;
	}
}
