package com.timetrack.routine;

import com.timetrack.activity.ActivityRepository;
import com.timetrack.auth.SecurityUtil;
import com.timetrack.auth.User;
import com.timetrack.auth.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Web Controller handling requests for cyclical routines, checklists, stopwatch timers, and routine analytics.
 */
@Controller
@RequestMapping("/routines")
public class RoutineController {

    private final RoutineService routineService;
    private final UserService userService;
    private final ActivityRepository activityRepository;

    public RoutineController(RoutineService routineService, UserService userService, ActivityRepository activityRepository) {
        this.routineService = routineService;
        this.userService = userService;
        this.activityRepository = activityRepository;
    }

    @GetMapping
    public String routinesPage(Model model) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";

        User user = userService.findById(username);
        if (user == null) return "redirect:/login";

        List<SessionGroupSummaryDto> summaries = routineService.getSessionSummaries(user);
        RoutineStatsDto stats = routineService.getStats(user);
        List<RoutineViewDto> allRoutines = routineService.getAllRoutines(user);
        List<RoutineViewDto> dueRoutines = routineService.getDueOrOverdueRoutines(user);
        List<String> existingGroups = routineService.getExistingSessionGroups(user);

        model.addAttribute("summaries", summaries);
        model.addAttribute("stats", stats);
        model.addAttribute("allRoutines", allRoutines);
        model.addAttribute("dueRoutines", dueRoutines);
        model.addAttribute("existingGroups", existingGroups);
        model.addAttribute("activities", activityRepository.findByCreatedBy_Username(username));

        // Heatmap consistency data (last 26 weeks)
        RoutineHeatmapDto heatmapData = routineService.getHeatmapData(user, 26);
        model.addAttribute("heatmapData", heatmapData);

        // Chart data for completions per session
        model.addAttribute("chartLabels", stats.completionsPerSessionGroup().keySet());
        model.addAttribute("chartData", stats.completionsPerSessionGroup().values());

        return "routines";
    }

    @PostMapping
    public String createRoutine(@ModelAttribute RoutineRequestDto request) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";

        User user = userService.findById(username);
        routineService.createRoutine(request, user);
        return "redirect:/routines";
    }

    @PutMapping("/{id}")
    public String updateRoutine(@PathVariable Long id, @ModelAttribute RoutineRequestDto request) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";

        User user = userService.findById(username);
        routineService.updateRoutine(id, request, user);
        return "redirect:/routines";
    }

    @DeleteMapping("/{id}")
    public String deleteRoutine(@PathVariable Long id) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";

        User user = userService.findById(username);
        routineService.deleteRoutine(id, user);
        return "redirect:/routines";
    }

    @PostMapping("/{id}/complete")
    public String completeRoutine(@PathVariable Long id,
                                  @RequestParam(required = false) Integer actualDurationSeconds,
                                  @RequestParam(required = false) String notes,
                                  HttpServletRequest request) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";

        User user = userService.findById(username);
        routineService.completeRoutine(id, actualDurationSeconds, notes, user);

        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/routines";
    }

    @PostMapping("/{id}/uncomplete")
    public String uncompleteRoutine(@PathVariable Long id, HttpServletRequest request) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";

        User user = userService.findById(username);
        routineService.undoLastCompletion(id, user);

        String referer = request.getHeader("Referer");
        return referer != null ? "redirect:" + referer : "redirect:/routines";
    }

    @PostMapping("/complete-session")
    @ResponseBody
    public ResponseEntity<?> completeSession(@RequestBody List<RoutineSessionCompletionItemDto> items) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userService.findById(username);
        List<RoutineCompletion> completions = routineService.completeBatchSession(items, user);
        return ResponseEntity.ok(Map.of("success", true, "count", completions.size()));
    }

    @GetMapping("/api/calendar")
    @ResponseBody
    public ResponseEntity<RoutineCalendarMonthDto> getCalendarApi(@RequestParam(required = false) Integer year,
                                                                  @RequestParam(required = false) Integer month) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userService.findById(username);
        java.time.LocalDate now = java.time.LocalDate.now();
        int y = (year != null && year >= 2000 && year <= 2100) ? year : now.getYear();
        int m = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();

        RoutineCalendarMonthDto calendarMonth = routineService.getCalendarMonth(y, m, user);
        return ResponseEntity.ok(calendarMonth);
    }
}
