package com.timetrack.calendar;

import com.timetrack.activity.ActivityRepository;
import com.timetrack.auth.SecurityUtil;
import com.timetrack.auth.User;
import com.timetrack.auth.UserRepository;
import com.timetrack.routine.RoutineService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final RoutineService routineService;

    public CalendarController(CalendarService calendarService,
                              UserRepository userRepository,
                              ActivityRepository activityRepository,
                              RoutineService routineService) {
        this.calendarService = calendarService;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.routineService = routineService;
    }

    @GetMapping
    public String viewCalendar(@RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               @RequestParam(required = false, defaultValue = "month") String view,
                               Model model) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";
        User user = userRepository.findByUsername(username);
        if (user == null) return "redirect:/login";

        LocalDate now = LocalDate.now();
        int y = (year != null && year >= 2000 && year <= 2100) ? year : now.getYear();
        int m = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();

        CalendarViewDto calendarView = calendarService.getCalendarView(y, m, user);

        model.addAttribute("calendar", calendarView);
        model.addAttribute("selectedView", view);
        model.addAttribute("todayDate", now);
        model.addAttribute("userActivities", activityRepository.findByCreatedBy_Username(username));
        model.addAttribute("planTypes", DailyPlanType.values());

        return "calendar";
    }

    @GetMapping("/api/data")
    @ResponseBody
    public ResponseEntity<CalendarViewDto> getCalendarData(@RequestParam(required = false) Integer year,
                                                          @RequestParam(required = false) Integer month) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return ResponseEntity.status(401).build();
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.status(401).build();

        LocalDate now = LocalDate.now();
        int y = (year != null && year >= 2000 && year <= 2100) ? year : now.getYear();
        int m = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();

        CalendarViewDto calendarView = calendarService.getCalendarView(y, m, user);
        return ResponseEntity.ok(calendarView);
    }

    @PostMapping("/items")
    public String createPlanItem(@ModelAttribute DailyPlanItemRequestDto request,
                                 @RequestParam(required = false) String returnView,
                                 @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return "redirect:/login";
        User user = userRepository.findByUsername(username);
        if (user == null) return "redirect:/login";

        calendarService.createPlanItem(request, user);

        LocalDate d = request.date() != null ? request.date() : LocalDate.now();
        String view = returnView != null ? returnView : "month";
        return "redirect:/calendar?year=" + d.getYear() + "&month=" + d.getMonthValue() + "&view=" + view;
    }

    @PostMapping("/items/{id}/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleItem(@PathVariable Long id) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return ResponseEntity.status(401).build();
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.status(401).build();

        DailyPlanItem updated = calendarService.togglePlanItem(id, user);
        return ResponseEntity.ok(Map.of("success", true, "completed", updated.isCompleted(), "id", updated.getId()));
    }

    @PostMapping("/items/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable Long id) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return ResponseEntity.status(401).build();
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.status(401).build();

        calendarService.deletePlanItem(id, user);
        return ResponseEntity.ok(Map.of("success", true, "id", id));
    }

    @PostMapping("/routines/{routineId}/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRoutine(@PathVariable Long routineId,
                                                               @RequestParam(required = false) Integer actualDurationSeconds) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) return ResponseEntity.status(401).build();
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.status(401).build();

        routineService.completeRoutine(routineId, actualDurationSeconds, "Completato da Calendario", user);
        return ResponseEntity.ok(Map.of("success", true, "routineId", routineId));
    }
}
