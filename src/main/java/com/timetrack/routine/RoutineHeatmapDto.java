package com.timetrack.routine;

import java.time.LocalDate;
import java.util.Map;

/**
 * Data Transfer Object containing daily completion metrics for GitHub-style consistency heatmaps.
 */
public record RoutineHeatmapDto(
        LocalDate startDate,
        LocalDate endDate,
        int totalWeeks,
        Map<String, Integer> globalCompletions,
        Map<Long, Map<String, Integer>> routineCompletions,
        int currentStreak,
        int longestStreak,
        int totalCompletions,
        int activeDaysCount
) {
}
