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

@Service
public class StatsServiceImpl implements StatsService {
    private final ActivityRepository activityRepository;

    public StatsServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Duration calculateDailyAverage(Long activityId, LocalDateTime fromDate) {
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

    @Override
    public Duration calculateSessionAverage(Long activityId, LocalDateTime fromDate) {
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

    @Override
    public Duration calculateTotalTime(Long activityId, LocalDateTime fromDate){
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

    @Override
    public Duration getSessionAverageLastWeek(Long activityId) {
        return calculateSessionAverage(activityId, LocalDateTime.now().minusWeeks(1));
    }

    @Override
    public Duration getSessionAverageLastMonth(Long activityId) {
        return calculateSessionAverage(activityId, LocalDateTime.now().minusMonths(1));
    }

    @Override
    public Duration getDailyAverageLastWeek(Long activityId) {
        return calculateDailyAverage(activityId, LocalDateTime.now().minusWeeks(1));
    }

    @Override
    public Duration getDailyAverageLastMonth(Long activityId) {
        return calculateDailyAverage(activityId, LocalDateTime.now().minusMonths(1));
    }

    @Override
    public Duration getTotalTimeLastWeek(Long activityId) {
        return calculateTotalTime(activityId, LocalDateTime.now().minusWeeks(1));
    }

    @Override
    public Duration getTotalTimeLastMonth(Long activityId) {
        return calculateTotalTime(activityId, LocalDateTime.now().minusMonths(1));
    }

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
