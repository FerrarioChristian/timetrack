package com.timetrack.activity;

import ch.qos.logback.core.model.Model;
import com.timetrack.activity.dto.ActivityRequestDto;
import com.timetrack.category.Category;
import com.timetrack.category.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    private final ActivityService activityService;
    private final CategoryService categoryService;

    public ActivityController(ActivityService activityService, CategoryService categoryService) {
        this.activityService = activityService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Activity> getAllActivities(Model model) {
        return activityService.getAllActivities();
    }

    @PostMapping
    public ResponseEntity<Activity> newActivity(@RequestBody ActivityRequestDto activityRequest) {
        Category category = categoryService.getOrCreateCategory(activityRequest.getCategoryName());
        Activity activity = new Activity();
        activity.setName(activityRequest.getName());
        activity.setDescription(activityRequest.getDescription());
        activity.setActivityType(activityRequest.getActivityType());
        activity.setCategory(category);

        Activity savedActivity = activityService.addActivity(activity);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedActivity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        Activity newActivity = activityService.updateActivity(id, activity);
        return ResponseEntity.ok(newActivity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
