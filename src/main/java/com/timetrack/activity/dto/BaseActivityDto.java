package com.timetrack.activity.dto;

import com.timetrack.activity.ActivityType;
import com.timetrack.auth.User;
import jakarta.validation.constraints.NotEmpty;

import java.time.Duration;

public class BaseActivityDto {
    @NotEmpty(message = "Il nome della attivitá non puó essere vuoto.")
    private String name;
    private String description;
    private Duration time;
    private String categoryName;
    private ActivityType activityType;
    private User createdBy;

    public BaseActivityDto(String name, String description, Duration time, String categoryName, ActivityType activityType, User createdBy) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.categoryName = categoryName;
        this.activityType = activityType;
        this.createdBy = createdBy;
    }

    public BaseActivityDto(String name, String description, Duration time, String categoryName, ActivityType activityType) {
        this(name, description, time, categoryName, activityType, null);
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public BaseActivityDto() {
    }

    public Duration getTime() {
        return time;
    }

    public void setTime(Duration time) {
        this.time = time;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    @Override
    public String toString() {
        return "ActivityRequestDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", time=" + time +
                ", categoryName='" + categoryName + '\'' +
                ", activityType=" + activityType +
                '}';
    }
}
