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
    public void startSession(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        boolean isAlreadyStarted = isSessionActive(activityId);
        if (isAlreadyStarted) {
            throw new IllegalArgumentException("Activity is already started");
        } else {
            ActivitySession session = new ActivitySession();
            session.setActivity(activity);
            session.setStartTime(LocalDateTime.now());
            activitySessionRepository.save(session);
        }
    }

    @Override
    public void stopSession(Long activityId) {
        ActivitySession session = activitySessionRepository.findByActivityIdAndEndTimeIsNull(activityId)
                .orElseThrow(() -> new IllegalArgumentException("No active session found"));
        session.setEndTime(LocalDateTime.now());
        activitySessionRepository.save(session);
    }

    @Override
    public Duration getTotalTimeForActivity(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        return null;
    }

    @Override
    public boolean isSessionActive(Long activityId) {
        return activitySessionRepository.findByActivityIdAndEndTimeIsNull(activityId).isPresent();
    }
}
