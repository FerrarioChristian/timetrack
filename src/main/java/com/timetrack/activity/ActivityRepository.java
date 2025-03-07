package com.timetrack.activity;

import com.timetrack.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByCreatedBy_Username(String sessionUsername);

    boolean existsByIdAndCreatedBy_Username(Long activityId, String username);
}
