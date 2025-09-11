package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(categoryDetails.getName());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    public void deleteCategory(Long id) {
        // We might want to add logic here to check if products are using this category
        categoryRepository.deleteById(id);
    }
}
