package com.example.backend;

import com.example.backend.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration"
})
@ActiveProfiles("test")
@Import(TestConfig.class)
class BackendApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "Application context should not be null");
	}

	@Test
	void mainMethodShouldExistWithCorrectSignature() throws NoSuchMethodException {
		// Verify main method exists with correct signature
		var mainMethod = BackendApplication.class.getDeclaredMethod("main", String[].class);
		assertNotNull(mainMethod, "Main method should exist");
		assertEquals(void.class, mainMethod.getReturnType(), "Main method should return void");
		assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()), 
			"Main method should be static");
		assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()), 
			"Main method should be public");
	}

	@Test
	void applicationContextShouldContainRequiredBeans() {
		assertNotNull(applicationContext, "Application context should be loaded");
		assertTrue(applicationContext.containsBean("dataSource"), "DataSource bean should exist");
	}

	@Test
	void componentScanShouldIncludeBackendPackage() {
		SpringBootApplication springBootAnnotation = BackendApplication.class.getAnnotation(SpringBootApplication.class);
		assertNotNull(springBootAnnotation, "Should have SpringBootApplication annotation");
		
		ComponentScan componentScanAnnotation = BackendApplication.class.getAnnotation(ComponentScan.class);
		assertNotNull(componentScanAnnotation, "Should have ComponentScan annotation");
		
		String[] basePackages = componentScanAnnotation.basePackages();
		assertTrue(basePackages.length > 0, "Should have base packages configured");
		assertTrue(java.util.Arrays.asList(basePackages).contains("com.example.backend"), 
			"Should scan com.example.backend package");
	}

	@Test
	void applicationShouldHaveMainMethod() throws NoSuchMethodException {
		assertNotNull(BackendApplication.class.getMethod("main", String[].class),
			"Should have main method with String[] args");
	}

	@Test
	void backendApplicationClassShouldNotBeNull() {
		BackendApplication app = new BackendApplication();
		assertNotNull(app, "BackendApplication instance should be created");
	}

	@Test
	void applicationContextShouldHaveExpectedBeanCount() {
		int beanCount = applicationContext.getBeanDefinitionCount();
		assertTrue(beanCount > 0, "Should have beans loaded in context");
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