package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import com.project.code.DTO.PlaceOrderRequestDTO;
import com.project.code.DTO.PurchaseProductDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

        Customer customer = customerRepository.findByEmail(placeOrderRequest.getEmail());
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getName());
            customer.setEmail(placeOrderRequest.getEmail());
            customer.setPhone(placeOrderRequest.getPhone());
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

        for (PurchaseProductDTO productDTO : placeOrderRequest.getPurchaseProducts()) {
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(
                    productDTO.getProductId(), placeOrderRequest.getStoreId());

            if (inventory == null || inventory.getStockLevel() < productDTO.getQuantity()) {
                throw new RuntimeException("Product out of stock or not available in store");
            }

            inventory.setStockLevel(inventory.getStockLevel() - productDTO.getQuantity());
            inventoryRepository.save(inventory);

            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem orderItem = new OrderItem(orderDetails, product,
                    productDTO.getQuantity(), productDTO.getPrice());

            orderItemRepository.save(orderItem);
        }
    }
}
