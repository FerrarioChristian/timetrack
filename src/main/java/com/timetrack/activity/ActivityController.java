package com.timetrack.activity;

import com.timetrack.activity.dto.ActivityRequestDto;
import com.timetrack.activity.dto.ActivityViewDto;
import com.timetrack.activitySession.ActivitySession;
import com.timetrack.activitySession.ActivitySessionService;
import com.timetrack.category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<ActivityViewDto> activities = activitySessionService.getAllActivitiesWithSessionStatus();
        model.addAttribute("activities", activities);
        return "activity-list";
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
    public String newActivity(@Valid @ModelAttribute("activity") ActivityRequestDto activityRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "activity-new";
        }
        activityService.addActivity(activityRequest);
        return "redirect:/";
    }

    @GetMapping("/activities/{id}")
    public String activityDetails(@PathVariable Long id, Model model) {
        Activity activity = activityService.getActivity(id).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        List<ActivitySession> sessions = activitySessionService.getActivitySessionsByActivityId(id).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        model.addAttribute("activity", activity);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sessions", sessions);
        return "activity-details";
    }

    @PutMapping("/activities/{id}")
    public String updateActivity(@ModelAttribute ActivityRequestDto updatedActivity, @PathVariable Long id) {
        Activity newActivity = activityService.updateActivity(id, updatedActivity);
        return "redirect:/";
    }

    @DeleteMapping("/activities/{id}")
    public String deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return "redirect:/";
    }
}
