package com.timetrack.activity.dto;

import com.timetrack.activity.ActivityType;

public class ActivityRequestDto extends BaseActivityDto{
    public ActivityRequestDto(String name, String description, String categoryName, ActivityType activityType) {
        super(name, description, categoryName, activityType);
    }

    public ActivityRequestDto() {
        super();
    }
}
