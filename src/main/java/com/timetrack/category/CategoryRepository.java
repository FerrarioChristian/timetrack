package com.timetrack.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCreatedBy_Username(String sessionUsername);

    Optional<Category> findByNameAndCreatedBy_Username(String categoryName, String username);
}
