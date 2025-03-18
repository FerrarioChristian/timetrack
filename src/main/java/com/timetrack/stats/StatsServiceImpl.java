package com.timetrack.stats;

import com.timetrack.activity.Activity;
import com.timetrack.activity.ActivityRepository;
import com.timetrack.activitySession.ActivitySession;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the StatsService interface.
 * This class is responsible for calculating statistics for a given activity.
 */
@Service
public class StatsServiceImpl implements StatsService {
    private final ActivityRepository activityRepository;

    public StatsServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Calculates the daily average duration of an activity.
     *
     * @param activityId the id of the activity
     * @param fromDate   the date from which to calculate the average
     * @return the daily average duration of the activity
     */
    private Duration calculateDailyAverage(Long activityId, LocalDateTime fromDate) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException(
                "Activity not found"));
        List<ActivitySession> sessions = activity.getSessions();

        if (sessions.isEmpty()) {
            return Duration.ZERO;
        }

        Map<LocalDate, Duration> dailyDurations = new HashMap<>();

        for (ActivitySession session : sessions) {
            if (session.getEndTime() != null) {
                LocalDate date = session.getStartTime().toLocalDate();
                Duration sessionDuration = Duration.between(session.getStartTime(), session.getEndTime());
                dailyDurations.put(date, dailyDurations.getOrDefault(date, Duration.ZERO).plus(sessionDuration));
            }
        }

        if (dailyDurations.isEmpty()) {
            return Duration.ZERO;
        }

        Duration total = dailyDurations.values().stream().reduce(Duration.ZERO, Duration::plus);
        return total.dividedBy(dailyDurations.size());
    }

    /**
     * Calculates the average duration of an activity session.
     *
     * @param activityId the id of the activity
     * @param fromDate   the date from which to calculate the average
     * @return the average duration of an activity session
     */
    private Duration calculateSessionAverage(Long activityId, LocalDateTime fromDate) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException(
                "Activity not found"));
        List<ActivitySession> sessions = activity.getSessions();

        if (sessions.isEmpty()) {
            return Duration.ZERO;
        }

        Duration totalTime =
                sessions.stream().filter(session -> session.getEndTime() != null).map(session -> Duration.between(session.getStartTime(), session.getEndTime())).reduce(Duration.ZERO, Duration::plus);

        return totalTime.dividedBy(sessions.size());
    }

    /**
     * Calculates the total time spent on an activity.
     *
     * @param activityId the id of the activity
     * @param fromDate   the date from which to calculate the total time
     * @return the total time spent on the activity
     */
    private Duration calculateTotalTime(Long activityId, LocalDateTime fromDate) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException(
                "Activity not found"));
        List<ActivitySession> sessions = activity.getSessions();

        if (sessions.isEmpty()) {
            return Duration.ZERO;
        }

        Duration totalTime = Duration.ZERO;
        for (ActivitySession session : sessions) {
            if (session.getEndTime() != null) {
                Duration sessionDuration = Duration.between(session.getStartTime(), session.getEndTime());
                totalTime = totalTime.plus(sessionDuration);
            }
        }
        return totalTime;
    }

    /**
     * Calculates the daily average duration of an activity.
     *
     * @param activityId the id of the activity
     * @return the daily average duration of the activity
     */
    @Override
    public Duration getSessionAverageLastWeek(Long activityId) {
        return calculateSessionAverage(activityId, LocalDateTime.now().minusWeeks(1));
    }

    /**
     * Calculates the daily average duration of an activity.
     *
     * @param activityId the id of the activity
     * @return the daily average duration of the activity
     */
    @Override
    public Duration getSessionAverageLastMonth(Long activityId) {
        return calculateSessionAverage(activityId, LocalDateTime.now().minusMonths(1));
    }

    /**
     * Calculates the daily average duration of an activity.
     *
     * @param activityId the id of the activity
     * @return the daily average duration of the activity
     */
    @Override
    public Duration getDailyAverageLastWeek(Long activityId) {
        return calculateDailyAverage(activityId, LocalDateTime.now().minusWeeks(1));
    }

    /**
     * Calculates the daily average duration of an activity.
     *
     * @param activityId the id of the activity
     * @return the daily average duration of the activity
     */
    @Override
    public Duration getDailyAverageLastMonth(Long activityId) {
        return calculateDailyAverage(activityId, LocalDateTime.now().minusMonths(1));
    }

    /**
     * Calculates the daily average duration of an activity.
     *
     * @param activityId the id of the activity
     * @return the daily average duration of the activity
     */
    @Override
    public Duration getTotalTimeLastWeek(Long activityId) {
        return calculateTotalTime(activityId, LocalDateTime.now().minusWeeks(1));
    }

    /**
     * Calculates the daily average duration of an activity.
     *
     * @param activityId the id of the activity
     * @return the daily average duration of the activity
     */
    @Override
    public Duration getTotalTimeLastMonth(Long activityId) {
        return calculateTotalTime(activityId, LocalDateTime.now().minusMonths(1));
    }

    /**
     * Returns a DetailsStatsDto object containing the statistics for a given activity.
     *
     * @param activityId the id of the activity
     * @return a DetailsStatsDto object containing the statistics for the activity
     */
    @Override
    public DetailsStatsDto getDetailsStats(Long activityId) {
        return new DetailsStatsDto(
                getSessionAverageLastWeek(activityId),
                getSessionAverageLastMonth(activityId),
                getDailyAverageLastWeek(activityId),
                getDailyAverageLastMonth(activityId),
                getTotalTimeLastWeek(activityId),
                getTotalTimeLastMonth(activityId)
        );
    }

}
