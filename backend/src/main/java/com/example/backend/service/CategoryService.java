package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        log.info("Fetching all categories from database");
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        log.info("Fetching category by id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found with id " + id);
                });
    }

    public Category createCategory(Category category) {
        log.info("Creating category: {}", category.getName());
        Category saved = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", saved.getId());
        return saved;
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        log.info("Updating category id: {}", id);
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(categoryDetails.getName());
                    Category saved = categoryRepository.save(category);
                    log.info("Category id: {} updated successfully", id);
                    return saved;
                })
                .orElseThrow(() -> {
                    log.warn("Category not found for update with id: {}", id);
                    return new ResourceNotFoundException("Category not found with id " + id);
                });
    }

    public void deleteCategory(Long id) {
        log.info("Deleting category id: {}", id);
        categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found for deletion with id: {}", id);
                    return new ResourceNotFoundException("Category not found with id " + id);
                });
        categoryRepository.deleteById(id);
        log.info("Category id: {} deleted successfully", id);
    }
}
