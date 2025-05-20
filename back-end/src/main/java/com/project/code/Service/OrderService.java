package com.project.code.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Product;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private OrderDetailsRepository orderDetailsRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Transactional
	public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

		Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
		if (customer == null) {
			customer = new Customer();
			customer.setName(placeOrderRequest.getCustomerName());
			customer.setEmail(placeOrderRequest.getCustomerEmail());
			customer.setPhone(placeOrderRequest.getCustomerPhone());
			customer = customerRepository.save(customer);
		}

		Store store = storeRepository.findById(placeOrderRequest.getStoreId())
				.orElseThrow(() -> new RuntimeException("Store not found"));

		OrderDetails orderDetails = new OrderDetails();
		orderDetails.setCustomer(customer);
		orderDetails.setStore(store);
		orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
		orderDetails.setDate(LocalDateTime.now());
		orderDetails = orderDetailsRepository.save(orderDetails);

		for (PurchaseProductDTO productDTO : placeOrderRequest.getPurchaseProduct()) {
			Inventory inventory = inventoryRepository.findByProductIdandStoreId(productDTO.getId(),
					placeOrderRequest.getStoreId());

			if (inventory == null || inventory.getStockLevel() < productDTO.getQuantity()) {
				throw new RuntimeException("Product out of stock or not available in store");
			}

			inventory.setStockLevel(inventory.getStockLevel() - productDTO.getQuantity());
			inventoryRepository.save(inventory);

			Product product = productRepository.findById(productDTO.getId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			OrderItem orderItem = new OrderItem(orderDetails, product, productDTO.getQuantity(), productDTO.getPrice());

			orderItemRepository.save(orderItem);
		}
	}
}
