package com.example.elasticsearch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductDocumentTest {

    @Test
    void productDocument_gettersAndSetters() {
        ProductDocument product = new ProductDocument();
        
        product.setId("123");
        product.setName("Laptop");
        product.setDescription("High performance laptop");

        assertEquals("123", product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("High performance laptop", product.getDescription());
    }

    @Test
    void productDocument_defaultValues() {
        ProductDocument product = new ProductDocument();

        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
    }

    @Test
    void productDocument_withNullValues() {
        ProductDocument product = new ProductDocument();
        
        product.setId(null);
        product.setName(null);
        product.setDescription(null);

        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
    }

    @Test
    void productDocument_withEmptyStrings() {
        ProductDocument product = new ProductDocument();
        
        product.setId("");
        product.setName("");
        product.setDescription("");

        assertEquals("", product.getId());
        assertEquals("", product.getName());
        assertEquals("", product.getDescription());
    }

    @Test
    void productDocument_withLongStrings() {
        ProductDocument product = new ProductDocument();
        
        String longDescription = "A".repeat(1000);
        product.setId("456");
        product.setName("Product with long description");
        product.setDescription(longDescription);

        assertEquals("456", product.getId());
        assertEquals("Product with long description", product.getName());
        assertEquals(longDescription, product.getDescription());
        assertEquals(1000, product.getDescription().length());
    }

    @Test
    void productDocument_withSpecialCharacters() {
        ProductDocument product = new ProductDocument();
        
        product.setId("789");
        product.setName("Product!@#$%^&*()");
        product.setDescription("Description with special chars: <>&\"'");

        assertEquals("789", product.getId());
        assertEquals("Product!@#$%^&*()", product.getName());
        assertEquals("Description with special chars: <>&\"'", product.getDescription());
    }

    @Test
    void productDocument_idOverwrite() {
        ProductDocument product = new ProductDocument();
        
        product.setId("original-id");
        assertEquals("original-id", product.getId());
        
        product.setId("new-id");
        assertEquals("new-id", product.getId());
    }

    @Test
    void productDocument_multipleUpdates() {
        ProductDocument product = new ProductDocument();
        
        product.setName("Name 1");
        assertEquals("Name 1", product.getName());
        
        product.setName("Name 2");
        assertEquals("Name 2", product.getName());
        
        product.setDescription("Desc 1");
        assertEquals("Desc 1", product.getDescription());
        
        product.setDescription("Desc 2");
        assertEquals("Desc 2", product.getDescription());
    }
}
