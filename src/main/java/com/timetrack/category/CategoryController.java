package com.timetrack.category;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CategoryController class is responsible for handling HTTP requests related to categories.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Constructor for CategoryController.
     * @param categoryService CategoryService object.
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get all categories.
     * @return List of categories.
     */
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * Create a new category
     * @param category Category object.
     * @return Category object.
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    /**
     * Update category by id.
     * @param id Category id.
     * @return Category object.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category savedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(savedCategory);
    }

    /**
     * Delete category by id.
     * @param id Category id.
     * @return Category object.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
