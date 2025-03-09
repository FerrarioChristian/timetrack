package com.timetrack.stats;

import java.time.Duration;
import java.time.LocalDateTime;

public interface StatsService {
    Duration calculateDailyAverage(Long activityId, LocalDateTime fromDate);

    Duration calculateSessionAverage(Long activityId, LocalDateTime fromDate);

    Duration calculateTotalTime(Long activityID, LocalDateTime fromDate);

    Duration getSessionAverageLastWeek(Long activityId);

    Duration getSessionAverageLastMonth(Long activityId);

    Duration getDailyAverageLastWeek(Long activityId);

    Duration getDailyAverageLastMonth(Long activityId);

    Duration getTotalTimeLastWeek(Long activityId);

    Duration getTotalTimeLastMonth(Long activityId);

    DetailsStatsDto getDetailsStats(Long activityId);
}
