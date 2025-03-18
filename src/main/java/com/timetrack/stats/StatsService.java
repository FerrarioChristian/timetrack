package com.timetrack.stats;

import java.time.Duration;

/**
 * Service interface for calculating statistics.
 */
public interface StatsService {
    Duration getSessionAverageLastWeek(Long activityId);

    Duration getSessionAverageLastMonth(Long activityId);

    Duration getDailyAverageLastWeek(Long activityId);

    Duration getDailyAverageLastMonth(Long activityId);

    Duration getTotalTimeLastWeek(Long activityId);

    Duration getTotalTimeLastMonth(Long activityId);

    DetailsStatsDto getDetailsStats(Long activityId);
}
