package com.timetrack.goal;

import com.timetrack.auth.User;
import java.util.List;

public interface GoalService {
    List<GoalDto> getGoalsForUser(User user);
    void createGoal(User user, Long categoryId, Long activityId, java.time.Duration target, Goal.GoalPeriod period);
    void deleteGoal(Long goalId);
}
