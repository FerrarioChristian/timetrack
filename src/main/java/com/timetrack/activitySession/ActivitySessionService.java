package com.timetrack.activitySession;

import com.timetrack.activity.dto.ActivityViewDto;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface ActivitySessionService {
    void startSession(Long activityId);
    void stopSession(Long activityId);
    Duration getTotalTimeForActivity (Long activityId);
    boolean isSessionActive(Long activityId);
    Optional<ActivitySession> getActiveSession(Long activityId);
    List<ActivityViewDto> getAllActivitiesWithSessionStatus();
}
