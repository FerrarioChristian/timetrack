package com.timetrack.activity.dto;

import com.timetrack.activity.ActivityType;

import java.time.LocalDateTime;

public class ActivityViewDto extends BaseActivityDto {
    private Long id;
    private boolean isSessionActive;
    private LocalDateTime sessionStartTime;

    public ActivityViewDto(Long id, String name, String description, String categoryName, ActivityType activityType, boolean isSessionActive, LocalDateTime sessionStartTime) {
        super(name, description, categoryName, activityType);
        this.id = id;
        this.isSessionActive = isSessionActive;
        this.sessionStartTime = sessionStartTime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSessionActive() {
        return isSessionActive;
    }

    public void setSessionActive(boolean sessionActive) {
        isSessionActive = sessionActive;
    }

    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }
}
