package com.timetrack.activity;

import com.timetrack.activity.dto.ActivityRequestDto;
import com.timetrack.activity.dto.ActivityViewDto;
import com.timetrack.activitySession.ActivitySessionService;
import com.timetrack.category.CategoryService;
import com.timetrack.stats.StatsService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling activity related requests.
 */
@Controller
@RequestMapping
public class ActivityController {
    private final ActivityService activityService;
    private final CategoryService categoryService;
    private final ActivitySessionService activitySessionService;
    private final StatsService statsService;

    public ActivityController(ActivityService activityService, CategoryService categoryService,
                              ActivitySessionService activitySessionService, StatsService statsService) {
        this.activityService = activityService;
        this.categoryService = categoryService;
        this.activitySessionService = activitySessionService;
        this.statsService = statsService;
    }

    /**
     * Returns a list of all activities.
     *
     * @param model model to add attributes to
     * @return view name
     */
    @GetMapping("/")
    public String listActivities(Model model) {
        List<ActivityViewDto> activities = activitySessionService.getAllActivitiesWithSessionStatus();
        model.addAttribute("activities", activities);
        return "activity-list";
    }

    /**
     * Returns a page for creating a new activity.
     *
     * @param model model to add attributes to
     * @return view name
     */
    @GetMapping("/activities/new")
    public String newActivityPage(Model model) {
        model.addAttribute("activity", new ActivityRequestDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activity-new";
    }

    /**
     * Returns a page for editing an activity.
     * @param model model to add attributes to
     * @param id id of the activity to edit
     * @return view name
     */
    @GetMapping("/activities/{id}/edit")
    public String editActivityPage(Model model, @PathVariable Long id) {
        Activity activity = activityService.getActivity(id).orElseThrow(() -> new IllegalArgumentException("Activity "
                + "not found"));
        model.addAttribute("activity", activity);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "activity-edit";
    }

    /**
     * Creates a new activity.
     *
     * @param activityRequest activity request
     * @param bindingResult binding result
     * @return redirect to home page
     */
    @PostMapping("/activities")
    public String newActivity(@Valid @ModelAttribute("activity") ActivityRequestDto activityRequest,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "activity-new";
        }
        activityService.addActivity(activityRequest);
        return "redirect:/";
    }

    /**
     * Returns a page with details of an activity.
     * @param model model to add attributes to
     * @param id id of the activity
     * @return view name
     * @throws IllegalArgumentException if activity is not found
     */
    @PreAuthorize("@activityServiceImpl.isOwner(#id)")
    @GetMapping("/activities/{id}")
    public String activityDetails(Model model, @PathVariable Long id) {
        Activity activity = activityService.getActivity(id).orElseThrow(() -> new IllegalArgumentException("Activity "
                + "not found"));

        model.addAttribute("activity", activity);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("stats", statsService.getDetailsStats(id));

        return "activity-details";
    }

    /**
     * Updates an activity.
     *
     * @param updatedActivity updated activity
     * @param id id of the activity to update
     * @return redirect to home page
     */
    @PreAuthorize("@activityServiceImpl.isOwner(#id)")
    @PutMapping("/activities/{id}")
    public String updateActivity(@ModelAttribute ActivityRequestDto updatedActivity, @PathVariable Long id) {
        activityService.updateActivity(id, updatedActivity);
        return "redirect:/";
    }

    /**
     * Deletes an activity.
     *
     * @param id id of the activity to delete
     * @return redirect to home page
     */
    @PreAuthorize("@activityServiceImpl.isOwner(#id)")
    @DeleteMapping("/activities/{id}")
    public String deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return "redirect:/";
    }
}
