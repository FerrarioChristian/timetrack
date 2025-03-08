package com.timetrack.activity;

import com.timetrack.activity.dto.ActivityViewDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ActivityMapper {
    public ActivityViewDto toActivityViewDto(Activity activity, boolean isSessionActive,
                                             LocalDateTime sessionStartTime) {
        return new ActivityViewDto(activity.getId(), activity.getName(), activity.getDescription(),
                activity.getTime(), activity.getCategory() != null ? activity.getCategory().getName() : null,
                activity.getActivityType(), isSessionActive, sessionStartTime);
    }
}
