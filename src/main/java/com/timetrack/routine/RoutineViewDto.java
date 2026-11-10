package com.timetrack.routine;

import java.time.format.DateTimeFormatter;

/**
 * View presentation model for a Routine, formatted for UI rendering.
 */
public class RoutineViewDto {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Long id;
    private final String name;
    private final String description;
    private final String sessionGroup;
    private final Integer estimatedMinutes;
    private final RecurrenceType recurrenceType;
    private final Integer frequencyDays;
    private final String targetDaysOfWeek;
    private final String recurrenceDescription;
    private final String lastCompletedFormatted;
    private final String nextDueDateFormatted;
    private final RoutineStatus status;
    private final long daysOverdue;
    private final long daysUntilDue;
    private final Long activityId;
    private final String activityName;
    private final int completionsCount;
    private final String lastDurationFormatted;
    private final java.util.List<String> checklistItems;
    private final String checklistItemsRaw;

    public RoutineViewDto(Routine routine) {
        this.id = routine.getId();
        this.name = routine.getName();
        this.description = routine.getDescription();
        this.sessionGroup = routine.getSessionGroup();
        this.estimatedMinutes = routine.getEstimatedMinutes();
        this.recurrenceType = routine.getRecurrenceType();
        this.frequencyDays = routine.getFrequencyDays();
        this.targetDaysOfWeek = routine.getTargetDaysOfWeek();
        this.recurrenceDescription = routine.getRecurrenceDescription();

        this.lastCompletedFormatted = routine.getLastCompletedAt() != null
                ? routine.getLastCompletedAt().format(TIME_FORMATTER)
                : "Mai";

        this.nextDueDateFormatted = routine.getNextDueDate() != null
                ? routine.getNextDueDate().format(DATE_FORMATTER)
                : "-";

        this.status = routine.getStatus();
        this.daysOverdue = routine.getDaysOverdue();
        this.daysUntilDue = routine.getDaysUntilDue();

        if (routine.getActivity() != null) {
            this.activityId = routine.getActivity().getId();
            this.activityName = routine.getActivity().getName();
        } else {
            this.activityId = null;
            this.activityName = null;
        }

        this.completionsCount = routine.getCompletions() != null ? routine.getCompletions().size() : 0;
        if (routine.getCompletions() != null && !routine.getCompletions().isEmpty()) {
            this.lastDurationFormatted = routine.getCompletions().get(0).getFormattedDuration();
        } else {
            this.lastDurationFormatted = "-";
        }

        this.checklistItems = routine.getChecklistItems() != null ? new java.util.ArrayList<>(routine.getChecklistItems()) : java.util.Collections.emptyList();
        this.checklistItemsRaw = String.join("\n", this.checklistItems);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSessionGroup() {
        return sessionGroup;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public Integer getFrequencyDays() {
        return frequencyDays;
    }

    public String getTargetDaysOfWeek() {
        return targetDaysOfWeek;
    }

    public String getRecurrenceDescription() {
        return recurrenceDescription;
    }

    public String getLastCompletedFormatted() {
        return lastCompletedFormatted;
    }

    public String getNextDueDateFormatted() {
        return nextDueDateFormatted;
    }

    public RoutineStatus getStatus() {
        return status;
    }

    public long getDaysOverdue() {
        return daysOverdue;
    }

    public long getDaysUntilDue() {
        return daysUntilDue;
    }

    public Long getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public int getCompletionsCount() {
        return completionsCount;
    }

    public String getLastDurationFormatted() {
        return lastDurationFormatted;
    }

    public java.util.List<String> getChecklistItems() {
        return checklistItems;
    }

    public String getChecklistItemsRaw() {
        return checklistItemsRaw;
    }

    public boolean hasChecklist() {
        return checklistItems != null && !checklistItems.isEmpty();
    }

    public boolean isDueOrOverdue() {
        return status == RoutineStatus.OVERDUE || status == RoutineStatus.DUE_TODAY;
    }

    public String getStatusLabel() {
        return switch (status) {
            case OVERDUE -> daysOverdue == 1 ? "In ritardo di 1 giorno" : "In ritardo di " + daysOverdue + " giorni";
            case DUE_TODAY -> "Da fare oggi";
            case COMPLETED_TODAY -> "Completata oggi";
            case UPCOMING -> daysUntilDue == 1 ? "Domani" : "Tra " + daysUntilDue + " giorni";
        };
    }

    public String getStatusBadgeClass() {
        return switch (status) {
            case OVERDUE -> "bg-red-500/10 text-red-400 border-red-500/20";
            case DUE_TODAY -> "bg-amber-500/10 text-amber-400 border-amber-500/20";
            case COMPLETED_TODAY -> "bg-emerald-500/10 text-emerald-400 border-emerald-500/20";
            case UPCOMING -> "bg-zinc-800 text-zinc-400 border-zinc-700";
        };
    }
}
