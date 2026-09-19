package com.library.management.service;

import com.library.management.entity.Category;
import com.library.management.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseThrow(() ->
                        new RuntimeException("Category not found with id: " + id));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {

        Category category = getCategoryById(id);

        category.setName(categoryDetails.getName());

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {

        Category category = getCategoryById(id);

        categoryRepository.delete(category);
    }
}