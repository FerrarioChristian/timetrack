package com.timetrack.goal;

import com.timetrack.activity.ActivityRepository;
import com.timetrack.activitySession.ActivitySessionRepository;
import com.timetrack.activitySession.ActivitySession;
import com.timetrack.auth.User;
import com.timetrack.category.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final ActivitySessionRepository sessionRepository;
    private final CategoryRepository categoryRepository;
    private final ActivityRepository activityRepository;

    public GoalServiceImpl(GoalRepository goalRepository, ActivitySessionRepository sessionRepository, CategoryRepository categoryRepository, ActivityRepository activityRepository) {
        this.goalRepository = goalRepository;
        this.sessionRepository = sessionRepository;
        this.categoryRepository = categoryRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public List<GoalDto> getGoalsForUser(User user) {
        List<Goal> goals = goalRepository.findByUser(user);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        List<ActivitySession> weekSessions = sessionRepository.findByActivityCreatedByAndStartTimeAfter(user, startOfWeek);
        List<ActivitySession> monthSessions = sessionRepository.findByActivityCreatedByAndStartTimeAfter(user, startOfMonth);

        return goals.stream().map(goal -> {
            String name = goal.getCategory() != null ? goal.getCategory().getName() : (goal.getActivity() != null ? goal.getActivity().getName() : "General");
            
            List<ActivitySession> relevantSessions = goal.getPeriod() == Goal.GoalPeriod.WEEKLY ? weekSessions : monthSessions;
            
            Duration current = relevantSessions.stream()
                .filter(s -> goal.getCategory() != null ? (s.getActivity().getCategory() != null && s.getActivity().getCategory().getId().equals(goal.getCategory().getId())) : true)
                .filter(s -> goal.getActivity() != null ? s.getActivity().getId().equals(goal.getActivity().getId()) : true)
                .map(s -> {
                    LocalDateTime end = s.getEndTime() != null ? s.getEndTime() : now;
                    return Duration.between(s.getStartTime(), end);
                })
                .reduce(Duration.ZERO, Duration::plus);
                
            double percentage = Math.min(100.0, ((double) current.getSeconds() / goal.getTargetDuration().getSeconds()) * 100);
            
            return new GoalDto(goal.getId(), name, goal.getTargetDuration(), current, percentage, goal.getPeriod());
        }).collect(Collectors.toList());
    }

    @Override
    public void createGoal(User user, Long categoryId, Long activityId, Duration target, Goal.GoalPeriod period) {
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTargetDuration(target);
        goal.setPeriod(period);
        if (categoryId != null) goal.setCategory(categoryRepository.findById(categoryId).orElse(null));
        if (activityId != null) goal.setActivity(activityRepository.findById(activityId).orElse(null));
        goalRepository.save(goal);
    }

    @Override
    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }
}
