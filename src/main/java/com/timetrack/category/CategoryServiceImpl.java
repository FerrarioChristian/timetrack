package com.timetrack.category;

import com.timetrack.auth.SecurityUtil;
import com.timetrack.auth.User;
import com.timetrack.auth.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing categories.
 * This class is responsible for handling the business logic of the application.
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all categories for the currently logged in user.
     * @return List of categories.
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findByCreatedBy_Username(SecurityUtil.getSessionUsername());
    }

    /**
     * Add a new category.
     * @param category Category to add.
     * @return The added category.
     */
    public Category addCategory(Category category) {
        String username = SecurityUtil.getSessionUsername();
        User user = userRepository.findByUsername(username);
        category.setCreatedBy(user);

        return categoryRepository.save(category);
    }

    /**
     * Update a category by its ID.
     * @param id ID of the category.
     * @return The category.
     */
    @Override
    public Category updateCategory(Long id, Category newCategory) {
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setName(newCategory.getName());
        String username = SecurityUtil.getSessionUsername();

        User user = userRepository.findByUsername(username);
        category.setCreatedBy(user);

        return categoryRepository.save(category);
    }

    /**
     * Delete a category by its ID.
     * @param id ID of the category.
     */
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Get a category by its ID, or create a new category if it doesn't exist.
     * @param categoryName Name of the category.
     * @return The category.
     */
    @Override
    public Category getOrCreateCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return null;
        }

        String username = SecurityUtil.getSessionUsername();
        return categoryRepository.findByNameAndCreatedBy_Username(categoryName, username)
                .orElseGet(() -> addCategory(new Category(categoryName)));
    }
}
