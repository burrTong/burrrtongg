package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
    }

    @Test
    void getAllCategories_shouldReturnListOfCategories() {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Clothing");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(testCategory, category2));

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("name").contains("Electronics", "Clothing");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_shouldReturnCategory_whenCategoryExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Category result = categoryService.getCategoryById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id 999");
    }

    @Test
    void createCategory_shouldSaveAndReturnCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(testCategory);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    void updateCategory_shouldUpdateAndReturnCategory_whenCategoryExists() {
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertThat(result).isNotNull();
        assertThat(testCategory.getName()).isEqualTo("Updated Electronics");
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    void updateCategory_shouldThrowException_whenCategoryNotFound() {
        Category updatedCategory = new Category();
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(999L, updatedCategory))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id 999");
    }

    @Test
    void deleteCategory_shouldDeleteCategory_whenCategoryExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id 999");

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).deleteById(any());
    }
}
