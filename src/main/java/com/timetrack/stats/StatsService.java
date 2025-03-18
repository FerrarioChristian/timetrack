package com.timetrack.stats;

import java.time.Duration;

/**
 * Service interface for calculating statistics.
 */
public interface StatsService {
    DetailsStatsDto getDetailsStats(Long activityId);
}
