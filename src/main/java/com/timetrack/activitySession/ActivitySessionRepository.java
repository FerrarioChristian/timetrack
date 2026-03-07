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
    
    java.util.List<ActivitySession> findByActivityCreatedByAndStartTimeAfter(com.timetrack.auth.User user, java.time.LocalDateTime startTime);
    
    java.util.List<ActivitySession> findByActivityCreatedByAndEndTimeIsNull(com.timetrack.auth.User user);
}
