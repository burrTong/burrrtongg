package com.example.backend.controller;

import com.example.backend.config.TestConfig;
import com.example.backend.entity.Category;
import com.example.backend.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
    }

    @Test
    void testGetCategoryByIdWithDebug() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);

        MvcResult result = mockMvc.perform(get("/api/categories/1"))
                .andDo(print())
                .andReturn();

        System.out.println("===== RESPONSE STATUS: " + result.getResponse().getStatus());
        System.out.println("===== RESPONSE BODY: " + result.getResponse().getContentAsString());
        System.out.println("===== RESPONSE ERROR: " + result.getResponse().getErrorMessage());
        
        if (result.getResolvedException() != null) {
            System.out.println("===== EXCEPTION: " + result.getResolvedException().getClass().getName());
            System.out.println("===== EXCEPTION MESSAGE: " + result.getResolvedException().getMessage());
            result.getResolvedException().printStackTrace();
        }
    }

    @Test
    void testUpdateCategoryWithDebug() throws Exception {
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Electronics");

        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(testCategory);

        MvcResult result = mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andDo(print())
                .andReturn();

        System.out.println("===== RESPONSE STATUS: " + result.getResponse().getStatus());
        System.out.println("===== RESPONSE BODY: " + result.getResponse().getContentAsString());
        System.out.println("===== RESPONSE ERROR: " + result.getResponse().getErrorMessage());
        
        if (result.getResolvedException() != null) {
            System.out.println("===== EXCEPTION: " + result.getResolvedException().getClass().getName());
            System.out.println("===== EXCEPTION MESSAGE: " + result.getResolvedException().getMessage());
            result.getResolvedException().printStackTrace();
        }
    }

    @Test
    void testDeleteCategoryWithDebug() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        MvcResult result = mockMvc.perform(delete("/api/categories/1"))
                .andDo(print())
                .andReturn();

        System.out.println("===== RESPONSE STATUS: " + result.getResponse().getStatus());
        System.out.println("===== RESPONSE BODY: " + result.getResponse().getContentAsString());
        System.out.println("===== RESPONSE ERROR: " + result.getResponse().getErrorMessage());
        
        if (result.getResolvedException() != null) {
            System.out.println("===== EXCEPTION: " + result.getResolvedException().getClass().getName());
            System.out.println("===== EXCEPTION MESSAGE: " + result.getResolvedException().getMessage());
            result.getResolvedException().printStackTrace();
        }
    }
}
