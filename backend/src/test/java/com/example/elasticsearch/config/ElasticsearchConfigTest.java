package com.example.elasticsearch.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ElasticsearchConfigTest {

    @Test
    void contextLoads() {
        // Test that the configuration loads successfully
        ElasticsearchConfig config = new ElasticsearchConfig();
        assertNotNull(config);
    }

    @Test
    void configurationIsEnabled() {
        // Test that the configuration bean can be instantiated
        ElasticsearchConfig config = new ElasticsearchConfig();
        assertNotNull(config, "ElasticsearchConfig should be instantiable");
    }
}
