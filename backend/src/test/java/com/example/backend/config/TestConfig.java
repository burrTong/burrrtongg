package com.example.backend.config;

import com.example.elasticsearch.service.ProductSearchService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    
    @Bean
    public ProductSearchService productSearchService() {
        return org.mockito.Mockito.mock(ProductSearchService.class);
    }
}
