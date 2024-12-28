package com.timetrack.activity.dto;

public class PatchActivityRequestDto {
    private String name;
    private String description;
    private String activityType;
    private String categoryName;

    public PatchActivityRequestDto() {
    }

    public PatchActivityRequestDto(String name, String description, String activityType, String categoryName) {
        this.name = name;
        this.description = description;
        this.activityType = activityType;
        this.categoryName = categoryName;
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

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
