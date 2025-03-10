package com.timetrack.activity;

import com.timetrack.activity.dto.ActivityRequestDto;
import com.timetrack.auth.SecurityUtil;
import com.timetrack.auth.User;
import com.timetrack.auth.UserRepository;
import com.timetrack.category.Category;
import com.timetrack.category.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing activities.
 * Implements the ActivityService interface.
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, CategoryService categoryService, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    /**
     * Get all activities.
     * @return Iterable<Activity> - all activities
     */
    @Override
    public Optional<Activity> getActivity(Long id) {
       return activityRepository.findById(id);
    }

    /**
     * Add a new activity.
     * @param activityRequest - ActivityRequestDto
     * @return Activity - the new activity
     */
    @Override
    public Activity addActivity(ActivityRequestDto activityRequest) {
        Activity newActivity = new Activity();
        return saveActivity(activityRequest, newActivity);
    }

    /**
     * Update an existing activity.
     * @param id - Long
     * @param updatedActivity - ActivityRequestDto
     * @return Activity - the updated activity
     */
    @Override
    public Activity updateActivity(Long id, ActivityRequestDto updatedActivity) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        return saveActivity(updatedActivity, activity);
    }

    /**
     * Save an activity.
     * @param updatedActivity - ActivityRequestDto
     * @param activity - Activity
     * @return Activity - the saved activity
     */
    private Activity saveActivity(ActivityRequestDto updatedActivity, Activity activity) {
        activity.setName(updatedActivity.getName());
        activity.setDescription(updatedActivity.getDescription());
        activity.setTime(updatedActivity.getTime());
        activity.setActivityType(updatedActivity.getActivityType());

        Category category = categoryService.getOrCreateCategory(updatedActivity.getCategoryName());
        activity.setCategory(category);

        String username = SecurityUtil.getSessionUsername();
        User user = userRepository.findByUsername(username);
        activity.setCreatedBy(user);

        return activityRepository.save(activity);
    }

    /**
     * Delete an activity.
     * @param id - Long
     */
    @Override
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    /**
     * Check if the current user is the owner of an activity.
     * @param activityId - Long
     * @return boolean - true if the current user is the owner, false otherwise
     */
    public boolean isOwner(Long activityId) {
        String username = SecurityUtil.getSessionUsername();
        return activityRepository.existsByIdAndCreatedBy_Username(activityId, username);
    }

}
