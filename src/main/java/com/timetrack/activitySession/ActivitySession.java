package com.timetrack.activitySession;

import com.timetrack.activity.Activity;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a session of an activity.
 */
@Entity
public class ActivitySession {
    /**
     * The unique identifier of the activity session.
     * This is the primary key of the table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The activity that was performed during this session.
     * This is a foreign key to the activity table.
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    private Activity activity;

    /**
     * The start time of the session.
     */
    private LocalDateTime startTime;
    /**
     * The end time of the session.
     */
    private LocalDateTime endTime;

    /**
     * Returns the duration of the session.
     * If the start time or end time is not set, the duration is zero.
     *
     * @return the duration of the session
     */
    public Duration getDuration() {
        if (startTime != null && endTime != null) {
            return Duration.between(startTime, endTime);
        }
        return Duration.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
