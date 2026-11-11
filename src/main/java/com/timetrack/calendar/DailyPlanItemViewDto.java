package com.timetrack.calendar;

import java.time.LocalDate;

public record DailyPlanItemViewDto(
        Long id,
        LocalDate date,
        String title,
        DailyPlanType itemType,
        String itemTypeDisplayName,
        Integer plannedMinutes,
        String formattedPlannedTime,
        Long activityId,
        String activityName,
        String activityColor,
        boolean completed,
        String notes
) {
    public static DailyPlanItemViewDto fromEntity(DailyPlanItem item) {
        String actName = null;
        String actColor = "#3b82f6";
        Long actId = null;
        if (item.getActivity() != null) {
            actId = item.getActivity().getId();
            actName = item.getActivity().getName();
        }

        String formattedTime = null;
        if (item.getPlannedMinutes() != null && item.getPlannedMinutes() > 0) {
            int h = item.getPlannedMinutes() / 60;
            int m = item.getPlannedMinutes() % 60;
            if (h > 0 && m > 0) {
                formattedTime = h + "h " + m + "m";
            } else if (h > 0) {
                formattedTime = h + "h";
            } else {
                formattedTime = m + "m";
            }
        }

        return new DailyPlanItemViewDto(
                item.getId(),
                item.getDate(),
                item.getTitle(),
                item.getItemType(),
                item.getItemType() != null ? item.getItemType().getDisplayName() : "Altro",
                item.getPlannedMinutes(),
                formattedTime,
                actId,
                actName,
                actColor,
                item.isCompleted(),
                item.getNotes()
        );
    }
}
