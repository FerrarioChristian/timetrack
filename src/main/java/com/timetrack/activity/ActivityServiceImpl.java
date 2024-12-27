package com.timetrack.activity;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Activity addActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Long id, Activity newActivity) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        activity.setName(newActivity.getName());
        activity.setDescription(newActivity.getDescription());
        activity.setActivityType(newActivity.getActivityType());
        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }


}
