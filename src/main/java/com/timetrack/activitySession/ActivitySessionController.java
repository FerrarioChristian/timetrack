package com.timetrack.activitySession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing activity sessions.
 * This controller is responsible for starting and stopping activity sessions.
 */
@Controller
@RequestMapping("/sessions")
public class ActivitySessionController {
    /**
     * The service for managing activity sessions.
     */
    private final ActivitySessionService activitySessionService;

    /**
     * Creates a new activity session controller with the given activity session service.
     *
     * @param activitySessionService the activity session service
     */
    public ActivitySessionController(ActivitySessionService activitySessionService) {
        this.activitySessionService = activitySessionService;
    }

    /**
     * Starts a new session for the given activity.
     *
     * @param activityId the ID of the activity to start a session for
     * @return the URL to redirect to
     */
    @PostMapping("/{activityId}/start")
    public String startSession(@PathVariable Long activityId) {
        activitySessionService.startSession(activityId);
        return "redirect:/";
    }

    /**
     * Stops the session for the given activity.
     *
     * @param activityId the ID of the activity to stop the session for
     * @return the URL to redirect to
     */
    @PostMapping("/{activityId}/stop")
    public String stopSession(@PathVariable Long activityId) {
        activitySessionService.stopSession(activityId);
        return "redirect:/";
    }
}
