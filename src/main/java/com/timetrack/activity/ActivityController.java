package com.timetrack.activity;

import com.timetrack.activity.dto.ActivityRequestDto;
import com.timetrack.activitySession.ActivitySessionService;
import com.timetrack.category.Category;
import com.timetrack.category.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class ActivityController {
    private final ActivityService activityService;
    private final CategoryService categoryService;
    private final ActivitySessionService activitySessionService;

    public ActivityController(ActivityService activityService,
                              CategoryService categoryService,
                              ActivitySessionService activitySessionService) {
        this.activityService = activityService;
        this.categoryService = categoryService;
        this.activitySessionService = activitySessionService;
    }

    @GetMapping("/")
    public String listActivities(Model model) {
        List<Activity> activities = activityService.getAllActivities();
        Map<Long, Boolean> activeSessions = new HashMap<>();

        for (Activity activity : activities) {
            boolean isSessionActive = activitySessionService.isSessionActive(activity.getId());
            activeSessions.put(activity.getId(), isSessionActive);
        }
        model.addAttribute("activities", activityService.getAllActivities());
        model.addAttribute("activeSessions", activeSessions);
        return "activity-list";
    }

    @GetMapping("/activities")
    public List<Activity> getAllActivities(Model model) {
        return activityService.getAllActivities();
    }

    @GetMapping("/activities/new")
    public String newActivityPage(Model model) {
        model.addAttribute("activity", new ActivityRequestDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activity-new";
    }

    @GetMapping("/activities/{id}/edit")
    public String editActivityPage(Model model, @PathVariable Long id) {
        Activity activity = activityService.getActivity(id).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        model.addAttribute("activity", activity);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activity-edit";
    }

    @PostMapping("/activities")
    public String newActivity(@ModelAttribute("activity") ActivityRequestDto activityRequest) {
        Category category = categoryService.getOrCreateCategory(activityRequest.getCategoryName());
        Activity activity = new Activity();
        activity.setName(activityRequest.getName());
        activity.setDescription(activityRequest.getDescription());
        activity.setActivityType(activityRequest.getActivityType());
        activity.setCategory(category);

        activityService.addActivity(activity);

        return "redirect:/";
    }

    @PutMapping("/activities/{id}")
    public String updateActivity(@ModelAttribute ActivityRequestDto updatedActivity, @PathVariable Long id) {
        Activity newActivity = activityService.updateActivity(id, updatedActivity);
        //return "redirect:/activities/" + newActivity.getId();
        return "redirect:/";
    }

    @DeleteMapping("/activities/{id}")
    public String deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return "redirect:/";
    }
}
