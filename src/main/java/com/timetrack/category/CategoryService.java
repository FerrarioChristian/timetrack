package com.timetrack.category;

import java.util.List;

/**
 * Service interface for the Category entity. 
 */
public interface CategoryService {
    List<Category> getAllCategories();
    Category addCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    Category getOrCreateCategory(String categoryName);
}
