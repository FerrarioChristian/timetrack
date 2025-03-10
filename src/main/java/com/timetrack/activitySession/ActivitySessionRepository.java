package com.timetrack.activitySession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for the ActivitySession entity.
 */
@Repository
public interface ActivitySessionRepository extends JpaRepository<ActivitySession, Long> {
    Optional<ActivitySession> findByActivityIdAndEndTimeIsNull(Long activityId);
}
