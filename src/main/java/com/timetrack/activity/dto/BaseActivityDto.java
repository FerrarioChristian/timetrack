package com.timetrack.activity.dto;

import com.timetrack.activity.ActivityType;

public class BaseActivityDto {
    private String name;
    private String description;
    private String categoryName;
    private ActivityType activityType;

    public BaseActivityDto(String name, String description, String categoryName, ActivityType activityType) {
        this.name = name;
        this.description = description;
        this.categoryName = categoryName;
        this.activityType = activityType;
    }

    public BaseActivityDto() {}

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
                ", categoryName='" + categoryName + '\'' +
                ", activityType=" + activityType +
                '}';
    }
}
