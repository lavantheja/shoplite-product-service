
package com.shoplite.product_service.service;

import com.shoplite.product_service.dto.CreateProductRequest;
import com.shoplite.product_service.dto.ProductResponse;
import com.shoplite.product_service.model.Product;
import com.shoplite.product_service.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return ProductResponse.from(getOrThrow(id));
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {

        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock()
        );

        Product saved = repository.save(product);

        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long id, CreateProductRequest request) {

        Product product = getOrThrow(id);

        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.stock()
        );

        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        repository.deleteById(id);
    }

    private Product getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
