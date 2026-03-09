package com.timetrack.activitySession;

import com.timetrack.activity.dto.ActivityViewDto;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing activity sessions.
 */
public interface ActivitySessionService {
    void startSession(Long activityId);
    void stopSession(Long activityId);
    boolean isSessionActive(Long activityId);
    Optional<ActivitySession> getActiveSession(Long activityId);
    List<ActivityViewDto> getAllActivitiesWithSessionStatus();
    void addManualSession(Long activityId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    void updateSession(Long sessionId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    void deleteSession(Long sessionId);
}
