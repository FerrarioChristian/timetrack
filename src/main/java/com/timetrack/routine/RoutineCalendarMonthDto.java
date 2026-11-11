package com.timetrack.routine;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object holding calendar view information for a given month,
 * including historical completions, tasks due today, and projected future recurring dates.
 */
public record RoutineCalendarMonthDto(
        int year,
        int month,
        String monthName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate firstDayOfMonth,
        LocalDate lastDayOfMonth,
        int totalCompleted,
        int totalDue,
        int totalProjected,
        int totalEstimatedMinutes,
        Map<String, List<RoutineCalendarEventDto>> eventsByDate
) {
}
