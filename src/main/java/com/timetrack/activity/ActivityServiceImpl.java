package com.timetrack.activity;

import com.timetrack.activity.dto.ActivityRequestDto;
import com.timetrack.activity.dto.ActivityViewDto;
import com.timetrack.category.Category;
import com.timetrack.category.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final CategoryService categoryService;

    public ActivityServiceImpl(ActivityRepository activityRepository, CategoryService categoryService) {
        this.activityRepository = activityRepository;
        this.categoryService = categoryService;
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Optional<Activity> getActivity(Long id) {
       return activityRepository.findById(id);
    }

    @Override
    public Activity addActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Long id, ActivityRequestDto updatedActivity) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        activity.setName(updatedActivity.getName());
        activity.setDescription(updatedActivity.getDescription());
        activity.setActivityType(updatedActivity.getActivityType());

        System.out.println(updatedActivity);

        Category category = categoryService.getOrCreateCategory(updatedActivity.getCategoryName());
        activity.setCategory(category);

        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

}
