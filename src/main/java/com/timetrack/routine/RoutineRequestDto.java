package com.timetrack.routine;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating or updating a Routine.
 */
public class RoutineRequestDto {

    private Long id;

    @NotEmpty(message = "Il nome della routine è obbligatorio.")
    private String name;

    private String description;

    @NotEmpty(message = "La sessione o gruppo è obbligatoria.")
    private String sessionGroup;

    @NotNull(message = "I minuti stimati sono obbligatori.")
    private Integer estimatedMinutes = 5;

    @NotNull(message = "Il tipo di ricorrenza è obbligatorio.")
    private RecurrenceType recurrenceType = RecurrenceType.DAYS_INTERVAL;

    private Integer frequencyDays = 7;

    private String targetDaysOfWeek;

    private Long activityId;

    /**
     * Optional sub-items / checklist items, entered as newline-separated strings in the UI.
     */
    private String checklistItemsRaw;

    public RoutineRequestDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSessionGroup() {
        return sessionGroup;
    }

    public void setSessionGroup(String sessionGroup) {
        this.sessionGroup = sessionGroup;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public Integer getFrequencyDays() {
        return frequencyDays;
    }

    public void setFrequencyDays(Integer frequencyDays) {
        this.frequencyDays = frequencyDays;
    }

    public String getTargetDaysOfWeek() {
        return targetDaysOfWeek;
    }

    public void setTargetDaysOfWeek(String targetDaysOfWeek) {
        this.targetDaysOfWeek = targetDaysOfWeek;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getChecklistItemsRaw() {
        return checklistItemsRaw;
    }

    public void setChecklistItemsRaw(String checklistItemsRaw) {
        this.checklistItemsRaw = checklistItemsRaw;
    }

    public java.util.List<String> getParsedChecklistItems() {
        if (checklistItemsRaw == null || checklistItemsRaw.isBlank()) {
            return java.util.Collections.emptyList();
        }
        return java.util.Arrays.stream(checklistItemsRaw.split("[\r\n]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
