package com.timetrack.routine;

/**
 * DTO representing a single completed routine within a batch or session runner execution.
 */
public class RoutineSessionCompletionItemDto {

    private Long routineId;
    private Integer actualDurationSeconds;
    private String notes;

    public RoutineSessionCompletionItemDto() {
    }

    public RoutineSessionCompletionItemDto(Long routineId, Integer actualDurationSeconds, String notes) {
        this.routineId = routineId;
        this.actualDurationSeconds = actualDurationSeconds;
        this.notes = notes;
    }

    public Long getRoutineId() {
        return routineId;
    }

    public void setRoutineId(Long routineId) {
        this.routineId = routineId;
    }

    public Integer getActualDurationSeconds() {
        return actualDurationSeconds;
    }

    public void setActualDurationSeconds(Integer actualDurationSeconds) {
        this.actualDurationSeconds = actualDurationSeconds;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
