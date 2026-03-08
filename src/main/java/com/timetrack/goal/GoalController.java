package com.timetrack.goal;

import com.timetrack.auth.SecurityUtil;
import com.timetrack.auth.User;
import com.timetrack.auth.UserService;
import com.timetrack.category.CategoryService;
import com.timetrack.activity.ActivityRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;

@Controller
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ActivityRepository activityRepository;

    public GoalController(GoalService goalService, UserService userService, CategoryService categoryService, ActivityRepository activityRepository) {
        this.goalService = goalService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.activityRepository = activityRepository;
    }

    @GetMapping
    public String goalsPage(Model model) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";
        User user = userService.findById(username);

        model.addAttribute("goals", goalService.getGoalsForUser(user));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("activities", activityRepository.findByCreatedBy_Username(username));
        return "goals";
    }

    @PostMapping
    public String createGoal(@RequestParam(required = false) Long categoryId,
                             @RequestParam(required = false) Long activityId,
                             @RequestParam int hours,
                             @RequestParam Goal.GoalPeriod period) {
        String username = SecurityUtil.getSessionUsername();
        User user = userService.findById(username);
        goalService.createGoal(user, categoryId, activityId, Duration.ofHours(hours), period);
        return "redirect:/goals";
    }

    @DeleteMapping("/{id}")
    public String deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return "redirect:/goals";
    }
}
