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
    public String startSession(@PathVariable Long activityId, jakarta.servlet.http.HttpServletRequest request) {
        activitySessionService.startSession(activityId);
        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/activities";
    }

    /**
     * Stops the session for the given activity.
     *
     * @param activityId the ID of the activity to stop the session for
     * @return the URL to redirect to
     */
    @PostMapping("/{activityId}/stop")
    public String stopSession(@PathVariable Long activityId, jakarta.servlet.http.HttpServletRequest request) {
        activitySessionService.stopSession(activityId);
        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/activities";
    }
    @PostMapping("/{activityId}/manual")
    public String addManualSession(@PathVariable Long activityId, 
                                 @RequestParam String startTime,
                                 @RequestParam String endTime,
                                 jakarta.servlet.http.HttpServletRequest request) {
        activitySessionService.addManualSession(activityId, parseDateTime(startTime), parseDateTime(endTime));
        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/activities/" + activityId;
    }
    
    @PutMapping("/edit/{sessionId}")
    public String updateSession(@PathVariable Long sessionId, 
                                @RequestParam String startTime,
                                @RequestParam String endTime,
                                jakarta.servlet.http.HttpServletRequest request) {
        activitySessionService.updateSession(sessionId, parseDateTime(startTime), parseDateTime(endTime));
        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/activities";
    }
    
    @DeleteMapping("/delete/{sessionId}")
    public String deleteSession(@PathVariable Long sessionId, jakarta.servlet.http.HttpServletRequest request) {
        activitySessionService.deleteSession(sessionId);
        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/activities";
    }

    private java.time.LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        if (dateTimeStr.length() == 16) {
            return java.time.LocalDateTime.parse(dateTimeStr + ":00");
        }
        // If it has a Z or offset, parse to instant then local
        if (dateTimeStr.endsWith("Z")) {
            return java.time.LocalDateTime.ofInstant(java.time.Instant.parse(dateTimeStr), java.time.ZoneId.systemDefault());
        }
        return java.time.LocalDateTime.parse(dateTimeStr);
    }

}
