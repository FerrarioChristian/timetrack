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
    private final com.timetrack.activitySession.ActivitySessionRepository sessionRepository;

    public StatsServiceImpl(ActivityRepository activityRepository, com.timetrack.activitySession.ActivitySessionRepository sessionRepository) {
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Calculates the daily average duration of an activity.
     *
     * @return the daily average duration of the activity
     */
    private Duration calculateDailyAverage(List<ActivitySession> sessions) {
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
     * @return the average duration of an activity session
     */
    private Duration calculateSessionAverage(List<ActivitySession> sessions) {
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
     * @return the total time spent on the activity
     */
    private Duration calculateTotalTime(List<ActivitySession> sessions) {
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
     * Returns a list of ActivitySessions of the Activity before the fromDate
     *
     * @param activityId the id of the Activity
     * @param fromDate Date
     * @return List<ActivitySessions>
     */
    private List<ActivitySession> getFilteredSessions(Long activityId, LocalDateTime fromDate) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        return activity.getSessions().stream()
                .filter(session -> session.getStartTime().isAfter(fromDate) || session.getStartTime().isEqual(fromDate))
                .toList();
    }

    /**
     * Returns a DetailsStatsDto object containing the statistics for a given activity.
     *
     * @param activityId the id of the activity
     * @return a DetailsStatsDto object containing the statistics for the activity
     */
    @Override
    public DetailsStatsDto getDetailsStats(Long activityId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        List<ActivitySession> sessionsLastWeek = getFilteredSessions(activityId, oneWeekAgo);
        List<ActivitySession> sessionsLastMonth = getFilteredSessions(activityId, oneMonthAgo);

        return new DetailsStatsDto(
                calculateSessionAverage(sessionsLastWeek),
                calculateSessionAverage(sessionsLastMonth),
                calculateDailyAverage(sessionsLastWeek),
                calculateDailyAverage(sessionsLastMonth),
                calculateTotalTime(sessionsLastWeek),
                calculateTotalTime(sessionsLastMonth)
        );
    }

    @Override
    public DashboardStatsDto getDashboardStats(com.timetrack.auth.User user) {
        LocalDateTime startOfWeek = LocalDateTime.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        List<ActivitySession> weekSessions = sessionRepository.findByActivityCreatedByAndStartTimeAfter(user, startOfWeek);
        
        Duration totalWeek = Duration.ZERO;
        Duration totalToday = Duration.ZERO;
        Map<String, Duration> categoryTimes = new HashMap<>();
        
        for (ActivitySession session : weekSessions) {
            LocalDateTime end = session.getEndTime() != null ? session.getEndTime() : LocalDateTime.now();
            Duration duration = Duration.between(session.getStartTime(), end);
            
            totalWeek = totalWeek.plus(duration);
            
            // Calculate time spent TODAY
            LocalDateTime startForToday = session.getStartTime().isBefore(startOfDay) ? startOfDay : session.getStartTime();
            if (!end.isBefore(startOfDay)) {
                Duration durationToday = Duration.between(startForToday, end);
                totalToday = totalToday.plus(durationToday);
            }
            
            String catName = session.getActivity().getCategory() != null ? session.getActivity().getCategory().getName() : "Uncategorized";
            categoryTimes.put(catName, categoryTimes.getOrDefault(catName, Duration.ZERO).plus(duration));
        }
        
        return new DashboardStatsDto(totalToday, totalWeek, categoryTimes);
    }
}
