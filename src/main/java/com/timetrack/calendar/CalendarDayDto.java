package com.timetrack.calendar;

import com.timetrack.routine.RoutineCalendarEventDto;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayDto(
        LocalDate date,
        String dateString,
        int dayOfMonth,
        boolean isToday,
        boolean isCurrentMonth,
        String dayOfWeekName,
        List<DailyPlanItemViewDto> workTargets,
        int totalTargetMinutes,
        int totalTrackedMinutes,
        String formattedTargetTime,
        String formattedTrackedTime,
        int progressPercent,
        List<TrackedSessionDto> trackedSessions,
        List<RoutineCalendarEventDto> routines,
        int completedRoutinesCount,
        int dueRoutinesCount,
        int projectedRoutinesCount,
        List<DailyPlanItemViewDto> events,
        int completedEventsCount,
        int totalEventsCount
) {}
