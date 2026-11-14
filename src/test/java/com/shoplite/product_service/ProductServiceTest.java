package com.shoplite.product_service;

import com.shoplite.product_service.model.Product;
import com.shoplite.product_service.repository.ProductRepository;
import com.shoplite.product_service.service.ProductService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import com.shoplite.product_service.dto.CreateProductRequest;
import org.mockito.ArgumentCaptor;


import static org.mockito.Mockito.verify;

import com.shoplite.product_service.service.ProductNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnAllProducts() {

        // Step 1: Create a sample product
        Product product = new Product(
                "Mechanical Keyboard",
                "RGB Gaming Keyboard",
                new BigDecimal("89.99"),
                25
        );

        // Step 2: Tell Mockito what the repository should return
        when(productRepository.findAll())
                .thenReturn(List.of(product));

        // Step 3: Call the actual service method
        var products = productService.findAll();

        // Step 4: Verify the result
        assertEquals(1, products.size());
        assertEquals("Mechanical Keyboard", products.get(0).name());
        assertEquals(new BigDecimal("89.99"), products.get(0).price());
        assertEquals(25, products.get(0).stock());
    }

    @Test
    void shouldCreateProduct() {

        // 1. Prepare a product creation request
        CreateProductRequest request = new CreateProductRequest(
                "Wireless Mouse",
                "Bluetooth Mouse",
                new BigDecimal("29.99"),
                15
        );

        // 2. Capture the product passed to the repository
        ArgumentCaptor<Product> captor =
                ArgumentCaptor.forClass(Product.class);

        // 3. Tell the mock repository to return the saved product
        when(productRepository.save(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 4. Call the real service
        var response = productService.create(request);

        // 5. Verify that the repository's save method was called
        verify(productRepository).save(captor.capture());

        // 6. Check the saved product's values
        Product savedProduct = captor.getValue();

        assertEquals("Wireless Mouse", savedProduct.getName());
        assertEquals(new BigDecimal("29.99"), savedProduct.getPrice());
        assertEquals(15, savedProduct.getStock());

        // 7. Check the service response
        assertEquals("Wireless Mouse", response.name());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        // Step 1: Choose a product ID that doesn't exist
        Long productId = 999L;

        // Step 2: Tell Mockito to return an empty result
        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        // Step 3: Call the service and expect an exception
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.findById(productId)
        );

        // Step 4: Verify the exception message
        assertEquals("Product 999 not found", exception.getMessage());
    }

}