package com.timetrack.category;

import com.timetrack.auth.SecurityUtil;
import com.timetrack.auth.User;
import com.timetrack.auth.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findByCreatedBy_Username(SecurityUtil.getSessionUsername());
    }

    public Category addCategory(Category category) {
        String username = SecurityUtil.getSessionUsername();
        User user = userRepository.findByUsername(username);
        category.setCreatedBy(user);

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category newCategory) {
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setName(newCategory.getName());
        String username = SecurityUtil.getSessionUsername();

        User user = userRepository.findByUsername(username);
        category.setCreatedBy(user);

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

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
