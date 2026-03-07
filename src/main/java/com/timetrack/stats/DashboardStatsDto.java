package com.timetrack.stats;

import java.time.Duration;
import java.util.Map;

/**
 * Data transfer object for Dashboard statistics.
 */
public record DashboardStatsDto(
    Duration totalTimeToday,
    Duration totalTimeThisWeek,
    Map<String, Duration> categoryTimesThisWeek
) {}
