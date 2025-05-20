package com.project.code.Repo;

import com.project.code.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Built-in method
    List<Product> findAll();

    // 2. Find by category
    List<Product> findByCategory(String category);

    // 3. Find by price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // 4. Find by SKU
    List<Product> findBySku(String sku);

    // 5. Find by name
    Product findByName(String name);

    // 6. Find by ID
    Product findById(Long id);

    // 7. Find by name pattern for a specific store
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findByNameLike(Long storeId, String pname);

    // 8. Find by name and category for a specific store
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.product.category = :category")
    List<Product> findByNameAndCategory(Long storeId, String pname, String category);

    // 9. Find by category and storeId
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")
    List<Product> findByCategoryAndStoreId(Long storeId, String category);

    // 10. Find by sub-name (case-insensitive)
    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findProductBySubName(String pname);

    // 11. Find all products for a specific store
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    List<Product> findProductsByStoreId(Long storeId);

    // 12. Find product by category and storeId
    @Query("SELECT i.product FROM Inventory i WHERE i.product.category = :category AND i.store.id = :storeId")
    List<Product> findProductByCategory(String category, Long storeId);

    // 13. Find product by sub-name and category
    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.category = :category")
    List<Product> findProductBySubNameAndCategory(String pname, String category);
}
