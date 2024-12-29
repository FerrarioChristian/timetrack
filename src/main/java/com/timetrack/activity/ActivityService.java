package com.timetrack.activity;

import com.timetrack.activity.dto.ActivityRequestDto;
import com.timetrack.activity.dto.ActivityViewDto;

import java.util.List;
import java.util.Optional;

public interface ActivityService {
    List<Activity> getAllActivities();
    Optional<Activity> getActivity(Long id);
    Activity addActivity(Activity activity);
    Activity updateActivity(Long id, ActivityRequestDto updatedActivity);
    void deleteActivity(Long id);
}
