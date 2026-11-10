package com.timetrack.routine;

import java.util.Map;

/**
 * Statistics and analytics for user routines.
 */
public record RoutineStatsDto(
        long totalRoutines,
        long dueTodayCount,
        long overdueCount,
        long completedTodayCount,
        int totalEstimatedMinutesDueToday,
        long completedThisWeekCount,
        double onTimeRatePercent,
        Double averageDurationMinutes,
        long totalActualSecondsThisWeek,
        Map<String, Long> completionsPerSessionGroup
) {
    public String getFormattedTotalActualTimeThisWeek() {
        if (totalActualSecondsThisWeek <= 0) return "0m";
        long h = totalActualSecondsThisWeek / 3600;
        long m = (totalActualSecondsThisWeek % 3600) / 60;
        if (h > 0) {
            return String.format("%dh %02dm", h, m);
        }
        return String.format("%dm", m);
    }

    public String getFormattedAverageDuration() {
        if (averageDurationMinutes == null || averageDurationMinutes <= 0) return "-";
        int mins = (int) Math.floor(averageDurationMinutes);
        int secs = (int) Math.round((averageDurationMinutes - mins) * 60);
        if (mins > 0) {
            return String.format("%dm %02ds", mins, secs);
        }
        return String.format("%ds", secs);
    }
}
