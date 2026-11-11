package com.timetrack.calendar;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record DailyPlanItemRequestDto(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        String title,
        DailyPlanType itemType,
        Double plannedHours,
        Integer plannedMinutes,
        Long activityId,
        String notes
) {
    public int computePlannedMinutes() {
        if (plannedMinutes != null && plannedMinutes > 0) {
            return plannedMinutes;
        }
        if (plannedHours != null && plannedHours > 0) {
            return (int) Math.round(plannedHours * 60);
        }
        return 0;
    }
}
