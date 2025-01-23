package com.timetrack.activity.dto;

import com.timetrack.activity.ActivityType;

import java.time.Duration;

public class ActivityRequestDto extends BaseActivityDto{
    private String timeString;
    public ActivityRequestDto(String name, String description, String time, String categoryName, ActivityType activityType) {
        super(name, description, Duration.parse(time), categoryName, activityType);
    }

    public ActivityRequestDto() {
        super();
    }

    public void setTime(String time) {
        super.setTime(Duration.parse(time));
    }
    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;

        if (timeString != null && !timeString.isEmpty()) {
            setTime(Duration.parse(timeString));
        }
    }
}
