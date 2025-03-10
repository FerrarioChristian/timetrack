package com.timetrack.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the Activity entity.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByCreatedBy_Username(String sessionUsername);

    boolean existsByIdAndCreatedBy_Username(Long activityId, String username);
}
