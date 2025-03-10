package com.timetrack.activitySession;

import com.timetrack.activity.Activity;
import com.timetrack.activity.ActivityMapper;
import com.timetrack.activity.ActivityRepository;
import com.timetrack.activity.dto.ActivityViewDto;
import com.timetrack.auth.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing activity sessions.
 */
@Service
public class ActivitySessionServiceImpl implements ActivitySessionService {
    private final ActivitySessionRepository activitySessionRepository;
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    /**
     * Constructor.
     *
     * @param activitySessionRepository Activity session repository
     * @param activityRepository        Activity repository
     * @param activityMapper            Activity mapper
     */
    public ActivitySessionServiceImpl(ActivitySessionRepository activitySessionRepository,
                                      ActivityRepository activityRepository, ActivityMapper activityMapper) {
        this.activitySessionRepository = activitySessionRepository;
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
    }

    /**
     * Starts a session for the given activity.
     *
     * @param activityId Activity ID
     */
    @Override
    public void startSession(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException(
                "Activity not found"));
        if (isSessionActive(activityId)) {
            throw new IllegalArgumentException("Activity is already started");
        } else {
            ActivitySession session = new ActivitySession();
            session.setActivity(activity);
            session.setStartTime(LocalDateTime.now());
            activitySessionRepository.save(session);
        }
    }

    /**
     * Stops the session for the given activity.
     *
     * @param activityId Activity ID
     */
    @Override
    public void stopSession(Long activityId) {
        ActivitySession session =
                activitySessionRepository.findByActivityIdAndEndTimeIsNull(activityId).orElseThrow(() -> new IllegalArgumentException("No active session found"));
        session.setEndTime(LocalDateTime.now());
        activitySessionRepository.save(session);
    }

    /**
     * Checks if a session is active for the given activity.
     *
     * @param activityId Activity ID
     * @return True if a session is active, false otherwise
     */
    @Override
    public boolean isSessionActive(Long activityId) {
        return activitySessionRepository.findByActivityIdAndEndTimeIsNull(activityId).isPresent();
    }

    /**
     * Gets the active session for the given activity.
     *
     * @param activityId Activity ID
     * @return Active session
     */
    @Override
    public Optional<ActivitySession> getActiveSession(Long activityId) {
        return activitySessionRepository.findByActivityIdAndEndTimeIsNull(activityId);
    }

    /**
     * Gets all activities with session status.
     *
     * @return List of activities with session status
     */
    @Override
    public List<ActivityViewDto> getAllActivitiesWithSessionStatus() {
        List<Activity> activities = activityRepository.findByCreatedBy_Username(SecurityUtil.getSessionUsername());

        return activities.stream().map(activity -> {
            Optional<ActivitySession> activeSession = getActiveSession(activity.getId());
            boolean isSessionActive = activeSession.isPresent();
            LocalDateTime sessionStartTime = isSessionActive ? activeSession.get().getStartTime() : null;
            return activityMapper.toActivityViewDto(activity, isSessionActive, sessionStartTime);
        }).collect(Collectors.toList());
    }
}
