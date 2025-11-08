package com.example.elasticsearch.service;

import com.example.elasticsearch.ProductDocument;
import com.example.elasticsearch.ProductSearchRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;

    public ProductSearchService(ProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }

    public void saveProduct(ProductDocument product) {
        productSearchRepository.save(product);
    }

    public void deleteProduct(String id) {
        productSearchRepository.deleteById(id);
    }

    public Iterable<ProductDocument> searchProducts(String name) {
        return productSearchRepository.findByName(name);
    }

    public Iterable<ProductDocument> findAll() {
        return productSearchRepository.findAll();
    }
}
