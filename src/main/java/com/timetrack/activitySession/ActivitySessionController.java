package com.timetrack.activitySession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sessions")
public class ActivitySessionController {
    private final ActivitySessionService activitySessionService;

    public ActivitySessionController(ActivitySessionService activitySessionService) {
        this.activitySessionService = activitySessionService;
    }

    @PostMapping("/{activityId}/start")
    public String startSession(@PathVariable Long activityId) {
        activitySessionService.startSession(activityId);
        return "redirect:/";
    }

    @PostMapping("/{activityId}/stop")
    public String stopSession(@PathVariable Long activityId) {
        activitySessionService.stopSession(activityId);
        return "redirect:/";
    }
}
