package com.timetrack.goal;

import java.time.Duration;

public record GoalDto(
    Long id,
    String targetName, // Category name or Activity name
    Duration targetDuration,
    Duration currentDuration,
    double progressPercentage,
    Goal.GoalPeriod period
) {}
