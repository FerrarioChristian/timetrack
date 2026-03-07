package com.timetrack.activity;

import com.timetrack.auth.SecurityUtil;
import com.timetrack.auth.User;
import com.timetrack.auth.UserService;
import com.timetrack.stats.DashboardStatsDto;
import com.timetrack.stats.StatsService;
import com.timetrack.activitySession.ActivitySession;
import com.timetrack.activitySession.ActivitySessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class DashboardController {

    private final StatsService statsService;
    private final UserService userService;
    private final ActivitySessionRepository sessionRepository;

    public DashboardController(StatsService statsService, UserService userService, ActivitySessionRepository sessionRepository) {
        this.statsService = statsService;
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        String username = SecurityUtil.getSessionUsername();
        if (username == null) {
            return "redirect:/login";
        }
        
        User user = userService.findById(username);
        if (user == null) {
            return "redirect:/login";
        }

        DashboardStatsDto stats = statsService.getDashboardStats(user);
        model.addAttribute("stats", stats);
        
        List<ActivitySession> activeSessions = sessionRepository.findByActivityCreatedByAndEndTimeIsNull(user);
        model.addAttribute("activeSessions", activeSessions);
        
        model.addAttribute("categoryNames", stats.categoryTimesThisWeek().keySet());
        model.addAttribute("categoryTimes", stats.categoryTimesThisWeek().values().stream().map(d -> d.getSeconds()).toList());

        return "dashboard";
    }
}
