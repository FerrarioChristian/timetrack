package com.timetrack.routine;

import java.time.LocalDate;
import java.util.List;

/**
 * Event DTO representing a scheduled, projected, or completed routine occurrence on a specific calendar date.
 */
public record RoutineCalendarEventDto(
        Long routineId,
        String name,
        String sessionGroup,
        Integer estimatedMinutes,
        Integer actualDurationSeconds,
        LocalDate date,
        String status, // "COMPLETED", "DUE_TODAY", "OVERDUE", "PROJECTED_FUTURE"
        Boolean onTime,
        String recurrenceDescription,
        List<String> checklistItems
) {
    public String getFormattedDuration() {
        if (actualDurationSeconds == null || actualDurationSeconds <= 0) {
            return (estimatedMinutes != null ? estimatedMinutes : 5) + " min (stimati)";
        }
        int mins = actualDurationSeconds / 60;
        int secs = actualDurationSeconds % 60;
        if (mins > 0) {
            return String.format("%dm %02ds", mins, secs);
        }
        return String.format("%ds", secs);
    }
}
