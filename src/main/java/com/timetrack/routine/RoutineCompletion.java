package com.timetrack.routine;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Historical record of a routine completion.
 * Used to calculate consistency streaks, average execution times, and statistics.
 */
@Entity
@Table(name = "routine_completions")
public class RoutineCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @Column(nullable = false)
    private LocalDateTime completedAt = LocalDateTime.now();

    /**
     * Actual duration in seconds if the user ran the integrated stopwatch/timer.
     * Null if quick-completed without timer.
     */
    private Integer actualDurationSeconds;

    /**
     * Flag indicating whether this completion occurred on or before the due date.
     */
    @Column(nullable = false)
    private Boolean onTime = true;

    private String notes;

    public RoutineCompletion() {
    }

    public RoutineCompletion(Routine routine, LocalDateTime completedAt, Integer actualDurationSeconds, Boolean onTime) {
        this.routine = routine;
        this.completedAt = completedAt;
        this.actualDurationSeconds = actualDurationSeconds;
        this.onTime = onTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getActualDurationSeconds() {
        return actualDurationSeconds;
    }

    public void setActualDurationSeconds(Integer actualDurationSeconds) {
        this.actualDurationSeconds = actualDurationSeconds;
    }

    public Boolean getOnTime() {
        return onTime != null ? onTime : true;
    }

    public void setOnTime(Boolean onTime) {
        this.onTime = onTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFormattedDuration() {
        if (actualDurationSeconds == null || actualDurationSeconds <= 0) {
            return "-";
        }
        int minutes = actualDurationSeconds / 60;
        int seconds = actualDurationSeconds % 60;
        if (minutes > 0) {
            return String.format("%dm %02ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }
}
