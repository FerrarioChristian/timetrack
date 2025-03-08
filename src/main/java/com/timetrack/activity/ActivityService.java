package com.timetrack.activity;

import com.timetrack.activity.dto.ActivityRequestDto;

import java.util.Optional;

public interface ActivityService {

    Optional<Activity> getActivity(Long id);

    Activity addActivity(ActivityRequestDto activity);

    Activity updateActivity(Long id, ActivityRequestDto updatedActivity);

    void deleteActivity(Long id);
}
