package com.timetrack.activitySession;

import java.time.Duration;

public interface ActivitySessionService {
    ActivitySession startSession(Long activityId);
    ActivitySession stopSession(Long sessionId);
    Duration getTotalTimeForActivity (Long activityId);
}
