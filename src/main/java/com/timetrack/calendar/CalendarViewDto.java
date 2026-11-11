package com.timetrack.calendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record CalendarViewDto(
        int year,
        int month,
        String monthName,
        LocalDate startDate,
        LocalDate endDate,
        List<CalendarDayDto> days,
        Map<String, CalendarDayDto> daysByDate,
        int totalTrackedMinutesMonth,
        String formattedTrackedTimeMonth,
        int totalTargetMinutesMonth,
        String formattedTargetTimeMonth,
        int totalRoutinesCompletedMonth,
        int totalEventsCompletedMonth
) {}
