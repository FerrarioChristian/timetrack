package com.timetrack.activitySession;

import com.timetrack.activity.Activity;
import com.timetrack.activity.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ActivitySessionServiceImpl implements ActivitySessionService {
    private final ActivitySessionRepository activitySessionRepository;
    private final ActivityRepository activityRepository;

    public ActivitySessionServiceImpl(ActivitySessionRepository activitySessionRepository,
                                      ActivityRepository activityRepository) {
        this.activitySessionRepository = activitySessionRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public ActivitySession startSession(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        ActivitySession session = new ActivitySession();
        session.setActivity(activity);
        session.setStartTime(LocalDateTime.now());
        return activitySessionRepository.save(session);
    }

    @Override
    public ActivitySession stopSession(Long sessionId) {
        ActivitySession session = activitySessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        if(session.getEndTime() != null) {
            throw new IllegalStateException("Session is already ended");
        }
        session.setEndTime(LocalDateTime.now());
        return activitySessionRepository.save(session);
    }

    @Override
    public Duration getTotalTimeForActivity(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        return activity.getSessions().stream()
                .map(ActivitySession::getSessionDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }
}
