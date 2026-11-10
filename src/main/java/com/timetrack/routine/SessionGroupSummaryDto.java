package com.timetrack.routine;

import java.util.List;

/**
 * Summary DTO for a session group (e.g., "Pulizia Bagno") containing tasks count and total estimated duration.
 */
public record SessionGroupSummaryDto(
        String sessionGroup,
        int totalTasks,
        int dueTasks,
        int totalEstimatedMinutesDue,
        List<RoutineViewDto> routines
) {
}
