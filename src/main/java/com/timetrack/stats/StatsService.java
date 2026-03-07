package com.timetrack.stats;

import java.time.Duration;

/**
 * Service interface for calculating statistics.
 */
public interface StatsService {
    DetailsStatsDto getDetailsStats(Long activityId);
    
    /**
     * Calculates statistics for the dashboard for a specific user.
     * 
     * @param user the logged in user
     * @return DashboardStatsDto containing total times and category breakdown
     */
    DashboardStatsDto getDashboardStats(com.timetrack.auth.User user);
}
