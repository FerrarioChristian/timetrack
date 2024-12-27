package com.timetrack.activity;

import java.util.List;

public interface ActivityService {
    List<Activity> getAllActivities();
    Activity addActivity(Activity activity);
    Activity updateActivity(Long id, Activity activity);
    void deleteActivity(Long id);

}
