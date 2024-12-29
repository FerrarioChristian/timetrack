package com.timetrack.activitySession;

import com.timetrack.activity.Activity;
import com.timetrack.activity.ActivityMapper;
import com.timetrack.activity.ActivityRepository;
import com.timetrack.activity.dto.ActivityViewDto;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivitySessionServiceImpl implements ActivitySessionService {
    private final ActivitySessionRepository activitySessionRepository;
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    public ActivitySessionServiceImpl(ActivitySessionRepository activitySessionRepository,
                                      ActivityRepository activityRepository,
                                      ActivityMapper activityMapper) {
        this.activitySessionRepository = activitySessionRepository;
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
    }

    @Override
    public void startSession(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        if (isSessionActive(activityId)) {
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

    @Override
    public Optional<ActivitySession> getActiveSession(Long activityId) {
        return activitySessionRepository.findByActivityIdAndEndTimeIsNull(activityId);
    }

    @Override
    public List<ActivityViewDto> getAllActivitiesWithSessionStatus() {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(activity -> {
                    Optional<ActivitySession> activeSession = getActiveSession(activity.getId());
                    boolean isSessionActive = activeSession.isPresent();
                    LocalDateTime sessionStartTime = isSessionActive ? activeSession.get().getStartTime() : null;
                    return activityMapper.toActivityViewDto(activity, isSessionActive, sessionStartTime);
                })
                .collect(Collectors.toList());
    }


}
