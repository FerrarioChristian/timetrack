package com.timetrack.calendar;

import java.time.LocalDateTime;

public record TrackedSessionDto(
        Long sessionId,
        Long activityId,
        String activityName,
        String categoryColor,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long durationSeconds,
        String formattedDuration
) {}
