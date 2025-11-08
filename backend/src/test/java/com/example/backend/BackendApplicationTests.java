package com.example.backend;

import com.example.elasticsearch.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration"
})
@ActiveProfiles("test")
class BackendApplicationTests {

	@MockBean
	private ProductSearchService productSearchService;

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class TestDataSourceConfig {
		@Bean
		@Primary
		public DataSource dataSource() {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("org.h2.Driver");
			dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
			dataSource.setUsername("sa");
			dataSource.setPassword("");
			return dataSource;
		}
	}

}