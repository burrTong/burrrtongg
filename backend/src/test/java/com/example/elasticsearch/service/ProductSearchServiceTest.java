package com.example.elasticsearch.service;

import com.example.elasticsearch.ProductDocument;
import com.example.elasticsearch.ProductSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {

    @Mock
    private ProductSearchRepository productSearchRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    private ProductDocument testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new ProductDocument();
        testProduct.setId("1");
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
    }

    @Test
    void saveProduct_shouldSaveProductDocument() {
        when(productSearchRepository.save(any(ProductDocument.class))).thenReturn(testProduct);

        productSearchService.saveProduct(testProduct);

        verify(productSearchRepository, times(1)).save(testProduct);
    }

    @Test
    void deleteProduct_shouldDeleteProductById() {
        doNothing().when(productSearchRepository).deleteById("1");

        productSearchService.deleteProduct("1");

        verify(productSearchRepository, times(1)).deleteById("1");
    }

    @Test
    void searchProducts_shouldReturnMatchingProducts() {
        ProductDocument product2 = new ProductDocument();
        product2.setId("2");
        product2.setName("Test Product 2");
        product2.setDescription("Another description");

        List<ProductDocument> products = Arrays.asList(testProduct, product2);
        when(productSearchRepository.findByNameFuzzy("Test")).thenReturn(products);

        Iterable<ProductDocument> result = productSearchService.searchProducts("Test");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(productSearchRepository, times(1)).findByNameFuzzy("Test");
    }

    @Test
    void searchProducts_shouldReturnEmptyWhenNoMatch() {
        when(productSearchRepository.findByNameFuzzy("NonExistent")).thenReturn(Arrays.asList());

        Iterable<ProductDocument> result = productSearchService.searchProducts("NonExistent");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(0);
        verify(productSearchRepository, times(1)).findByNameFuzzy("NonExistent");
    }

    @Test
    void findAll_shouldReturnAllProducts() {
        ProductDocument product2 = new ProductDocument();
        product2.setId("2");
        product2.setName("Product 2");

        List<ProductDocument> products = Arrays.asList(testProduct, product2);
        when(productSearchRepository.findAll()).thenReturn(products);

        Iterable<ProductDocument> result = productSearchService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(productSearchRepository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoProducts() {
        when(productSearchRepository.findAll()).thenReturn(Arrays.asList());

        Iterable<ProductDocument> result = productSearchService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(0);
    }

    @Test
    void saveProduct_shouldHandleNullFields() {
        ProductDocument productWithNulls = new ProductDocument();
        productWithNulls.setId("3");
        productWithNulls.setName(null);
        productWithNulls.setDescription(null);

        when(productSearchRepository.save(any(ProductDocument.class))).thenReturn(productWithNulls);

        productSearchService.saveProduct(productWithNulls);

        verify(productSearchRepository, times(1)).save(productWithNulls);
    }

    @Test
    void deleteProduct_shouldHandleMultipleDeletes() {
        doNothing().when(productSearchRepository).deleteById(anyString());

        productSearchService.deleteProduct("1");
        productSearchService.deleteProduct("2");
        productSearchService.deleteProduct("3");

        verify(productSearchRepository, times(1)).deleteById("1");
        verify(productSearchRepository, times(1)).deleteById("2");
        verify(productSearchRepository, times(1)).deleteById("3");
    }
}
