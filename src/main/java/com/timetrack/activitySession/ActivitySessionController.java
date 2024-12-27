package com.timetrack.activitySession;

import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/sessions")
public class ActivitySessionController {
    private final ActivitySessionService activitySessionService;

    public ActivitySessionController(ActivitySessionService activitySessionService) {
        this.activitySessionService = activitySessionService;
    }

    @PostMapping("/{activityId}/start")
    public ActivitySession startSession(@PathVariable Long activityId) {
        return activitySessionService.startSession(activityId);
    }

    @PostMapping("/{sessionId}/stop")
    public ActivitySession stopSession(@PathVariable Long sessionId) {
        return activitySessionService.stopSession(sessionId);
    }

    @GetMapping("/{activityId}/total-time")
    public Duration getTotalTime(@PathVariable Long activityId) {
        return activitySessionService.getTotalTimeForActivity(activityId);
    }

}
