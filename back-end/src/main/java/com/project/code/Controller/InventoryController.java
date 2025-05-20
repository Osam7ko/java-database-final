package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private ServiceClass validationService;

	@PutMapping
	public Map<String, String> updateInventory(@RequestBody CombinedRequest request) {
		Map<String, String> response = new HashMap<>();
		try {
			Product product = request.getProduct();
			Inventory inventory = request.getInventory();

			boolean isValid = validationService.validateProductId(product.getId());
			if (!isValid) {
				response.put("message", "Product not found");
				return response;
			}

			Inventory existing = inventoryRepository.findByProductIdandStoreId(product.getId(),
					inventory.getStore().getId());
			if (existing != null) {
				existing.setStockLevel(inventory.getStockLevel());
				inventoryRepository.save(existing);
				response.put("message", "Successfully updated product");
			} else {
				response.put("message", "No data available");
			}
		} catch (DataIntegrityViolationException e) {
			response.put("error", "Data integrity violation: " + e.getMessage());
		} catch (Exception e) {
			response.put("error", "Exception occurred: " + e.getMessage());
		}
		return response;
	}

	@PostMapping
	public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
		Map<String, String> response = new HashMap<>();
		try {
			if (!validationService.validateInventory(inventory)) {
				response.put("message", "Inventory already exists");
			} else {
				inventoryRepository.save(inventory);
				response.put("message", "Inventory saved successfully");
			}
		} catch (DataIntegrityViolationException e) {
			response.put("error", "Data integrity violation: " + e.getMessage());
		} catch (Exception e) {
			response.put("error", "Exception occurred: " + e.getMessage());
		}
		return response;
	}

	@GetMapping("/{storeid}")
	public Map<String, Object> getAllProducts(@PathVariable Long storeid) {
		Map<String, Object> response = new HashMap<>();
		List<Product> products = productRepository.findProductsByStoreId(storeid);
		response.put("products", products);
		return response;
	}

	@GetMapping("filter/{category}/{name}/{storeid}")
	public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name,
			@PathVariable Long storeid) {
		Map<String, Object> response = new HashMap<>();
		List<Product> result;

		if ("null".equals(category)) {
			result = productRepository.findByNameLike(storeid, name);
		} else if ("null".equals(name)) {
			result = productRepository.findByCategoryAndStoreId(storeid, category);
		} else {
			result = productRepository.findByNameAndCategory(storeid, name, category);
		}
		response.put("product", result);
		return response;
	}

	@GetMapping("search/{name}/{storeId}")
	public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
		Map<String, Object> response = new HashMap<>();
		List<Product> products = productRepository.findByNameLike(storeId, name);
		response.put("product", products);
		return response;
	}

	@DeleteMapping("/{id}")
	public Map<String, String> removeProduct(@PathVariable Long id) {
		Map<String, String> response = new HashMap<>();
		if (!validationService.validateProductId(id)) {
			response.put("message", "Product not present in database");
		} else {
			inventoryRepository.deleteByProductId(id);
			productRepository.deleteById(id);
			response.put("message", "Product deleted successfully");
		}
		return response;
	}

	@GetMapping("validate/{quantity}/{storeId}/{productId}")
	public boolean validateQuantity(@PathVariable int quantity, @PathVariable Long storeId,
			@PathVariable Long productId) {
		Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
		return inventory != null && inventory.getStockLevel() >= quantity;
	}
}
