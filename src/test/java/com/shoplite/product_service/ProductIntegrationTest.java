package com.shoplite.product_service;

import com.shoplite.product_service.model.Product;
import com.shoplite.product_service.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

@SpringBootTest
class ProductIntegrationTest {

    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveProductToPostgres() {

        // Step 1: Create a sample product
        Product product = new Product(
                "Wireless Mouse",
                "Bluetooth Mouse",
                new BigDecimal("29.99"),
                15
        );

        // Step 2: Save the product to PostgreSQL
        Product saved = productRepository.save(product);

        // Step 3: Verify the product was saved
        assertNotNull(saved.getId());
        assertEquals("Wireless Mouse", saved.getName());
        assertEquals(new BigDecimal("29.99"), saved.getPrice());
        assertEquals(15, saved.getStock());

        // Step 4: Clear Hibernate's persistence context
        entityManager.clear();

        // Step 5: Retrieve the product from PostgreSQL
        Product retrieved = productRepository.findById(saved.getId())
                .orElseThrow();

        // Step 6: Verify the retrieved product
        assertEquals(saved.getId(), retrieved.getId());
        assertEquals("Wireless Mouse", retrieved.getName());
        assertEquals("Bluetooth Mouse", retrieved.getDescription());
        assertEquals(new BigDecimal("29.99"), retrieved.getPrice());
        assertEquals(15, retrieved.getStock());
    }

    @Test
    void shouldUpdateProductInPostgres() {

        // Step 1: Create a product
        Product product = new Product(
                "Mechanical Keyboard",
                "RGB Keyboard",
                new BigDecimal("79.99"),
                10
        );

        // Step 2: Save it to PostgreSQL
        Product saved = productRepository.save(product);

        // Step 3: Update its details
        saved.update(
                "Mechanical Keyboard",
                "Updated RGB Keyboard",
                new BigDecimal("89.99"),
                25
        );

        // Step 4: Save the updated product
        productRepository.saveAndFlush(saved);

        // Step 5: Clear Hibernate's cache
        entityManager.clear();

        // Step 6: Retrieve the product again
        Product updated = productRepository.findById(saved.getId())
                .orElseThrow();

        // Step 7: Verify the updated values
        assertEquals("Mechanical Keyboard", updated.getName());
        assertEquals("Updated RGB Keyboard", updated.getDescription());
        assertEquals(new BigDecimal("89.99"), updated.getPrice());
        assertEquals(25, updated.getStock());
    }


    @Test
    void shouldSearchProductsIgnoringCase() {

        // Create a product in the test database
        Product mouse = new Product(
                "Wireless Mouse",
                "Bluetooth Mouse",
                new BigDecimal("29.99"),
                15
        );

        productRepository.saveAndFlush(mouse);

        // Search using uppercase letters
        List<Product> results =
                productRepository.findByNameContainingIgnoreCase("MOUSE");

        // Verify PostgreSQL returned the matching product
        assertTrue(
                results.stream()
                        .anyMatch(product ->
                                product.getName().equals("Wireless Mouse"))
        );
    }

    @Test
    void shouldDeleteProductFromPostgres() {

        // Step 1: Create a sample product
        Product product = new Product(
                "Wireless Mouse",
                "Bluetooth Mouse",
                new BigDecimal("29.99"),
                15
        );

        // Step 2: Save the product to PostgreSQL
        Product saved = productRepository.save(product);

        Long productId = saved.getId();

        // Step 3: Confirm the product exists
        assertTrue(productRepository.existsById(productId));

        // Step 4: Delete the product
        productRepository.deleteById(productId);

        // Step 5: Send the DELETE to PostgreSQL
        productRepository.flush();

        // Step 6: Clear Hibernate's persistence context
        entityManager.clear();

        // Step 7: Verify the product no longer exists
        assertFalse(productRepository.existsById(productId));
    }

}