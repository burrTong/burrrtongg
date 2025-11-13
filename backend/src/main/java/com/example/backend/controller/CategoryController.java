package com.example.backend.controller;

import com.example.backend.entity.Category;
import com.example.backend.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        log.info("Fetching all categories");
        List<Category> categories = categoryService.getAllCategories();
        log.info("Retrieved {} categories", categories.size());
        return categories;
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        log.info("Fetching category id: {}", id);
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        log.info("Creating category: {}", category.getName());
        Category created = categoryService.createCategory(category);
        log.info("Category created with id: {}", created.getId());
        return created;
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        log.info("Updating category id: {}", id);
        Category updated = categoryService.updateCategory(id, categoryDetails);
        log.info("Category id: {} updated successfully", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id) {
        log.info("Deleting category id: {}", id);
        categoryService.deleteCategory(id);
        log.info("Category id: {} deleted successfully", id);
        return "Category deleted with id " + id;
    }
}