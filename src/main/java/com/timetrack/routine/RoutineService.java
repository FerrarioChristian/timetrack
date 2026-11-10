package com.timetrack.routine;

import com.timetrack.auth.User;

import java.util.List;

/**
 * Service interface for managing cyclical routines and their completions.
 */
public interface RoutineService {

    List<RoutineViewDto> getAllRoutines(User user);

    List<RoutineViewDto> getDueOrOverdueRoutines(User user);

    List<SessionGroupSummaryDto> getSessionSummaries(User user);

    RoutineStatsDto getStats(User user);

    Routine getRoutineById(Long id, User user);

    Routine createRoutine(RoutineRequestDto request, User user);

    Routine updateRoutine(Long id, RoutineRequestDto request, User user);

    void deleteRoutine(Long id, User user);

    RoutineCompletion completeRoutine(Long id, Integer actualDurationSeconds, String notes, User user);

    void undoLastCompletion(Long id, User user);

    List<RoutineCompletion> completeBatchSession(List<RoutineSessionCompletionItemDto> items, User user);

    RoutineHeatmapDto getHeatmapData(User user, int weeks);

    List<String> getExistingSessionGroups(User user);
}
