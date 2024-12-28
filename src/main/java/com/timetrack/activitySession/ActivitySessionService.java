package com.timetrack.activitySession;

import java.time.Duration;

public interface ActivitySessionService {
    void startSession(Long activityId);
    void stopSession(Long activityId);
    Duration getTotalTimeForActivity (Long activityId);
    boolean isSessionActive(Long activityId);
}
