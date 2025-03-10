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
}
