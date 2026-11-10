package com.timetrack.routine;

import com.timetrack.activity.Activity;
import com.timetrack.activity.ActivityRepository;
import com.timetrack.activitySession.ActivitySessionService;
import com.timetrack.auth.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of RoutineService containing the business logic for cyclical routines,
 * recurring schedule calculations, completion tracking, and productivity analytics.
 */
@Service
@Transactional
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineCompletionRepository completionRepository;
    private final ActivityRepository activityRepository;
    private final ActivitySessionService activitySessionService;

    public RoutineServiceImpl(RoutineRepository routineRepository,
                              RoutineCompletionRepository completionRepository,
                              ActivityRepository activityRepository,
                              ActivitySessionService activitySessionService) {
        this.routineRepository = routineRepository;
        this.completionRepository = completionRepository;
        this.activityRepository = activityRepository;
        this.activitySessionService = activitySessionService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoutineViewDto> getAllRoutines(User user) {
        return routineRepository.findByUserOrderBySessionGroupAscNameAsc(user)
                .stream()
                .map(RoutineViewDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoutineViewDto> getDueOrOverdueRoutines(User user) {
        return routineRepository.findByUserOrderBySessionGroupAscNameAsc(user)
                .stream()
                .filter(Routine::isDueOrOverdue)
                .map(RoutineViewDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionGroupSummaryDto> getSessionSummaries(User user) {
        List<Routine> routines = routineRepository.findByUserOrderBySessionGroupAscNameAsc(user);
        Map<String, List<Routine>> grouped = routines.stream()
                .collect(Collectors.groupingBy(Routine::getSessionGroup, LinkedHashMap::new, Collectors.toList()));

        List<SessionGroupSummaryDto> summaries = new ArrayList<>();
        for (Map.Entry<String, List<Routine>> entry : grouped.entrySet()) {
            String sessionName = entry.getKey();
            List<Routine> sessionRoutines = entry.getValue();

            int totalTasks = sessionRoutines.size();
            List<Routine> dueRoutines = sessionRoutines.stream().filter(Routine::isDueOrOverdue).toList();
            int dueTasks = dueRoutines.size();
            int totalEstimatedMinutesDue = dueRoutines.stream().mapToInt(Routine::getEstimatedMinutes).sum();

            List<RoutineViewDto> dtos = sessionRoutines.stream().map(RoutineViewDto::new).toList();
            summaries.add(new SessionGroupSummaryDto(sessionName, totalTasks, dueTasks, totalEstimatedMinutesDue, dtos));
        }

        return summaries;
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineStatsDto getStats(User user) {
        List<Routine> allRoutines = routineRepository.findByUserOrderBySessionGroupAscNameAsc(user);
        long totalRoutines = allRoutines.size();

        long dueTodayCount = allRoutines.stream().filter(r -> r.getStatus() == RoutineStatus.DUE_TODAY).count();
        long overdueCount = allRoutines.stream().filter(r -> r.getStatus() == RoutineStatus.OVERDUE).count();
        long completedTodayCount = allRoutines.stream().filter(r -> r.getStatus() == RoutineStatus.COMPLETED_TODAY).count();

        int totalEstimatedMinutesDueToday = allRoutines.stream()
                .filter(Routine::isDueOrOverdue)
                .mapToInt(Routine::getEstimatedMinutes)
                .sum();

        LocalDateTime startOfWeek = LocalDateTime.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<RoutineCompletion> weekCompletions = completionRepository
                .findByRoutine_UserAndCompletedAtAfterOrderByCompletedAtDesc(user, startOfWeek);

        long completedThisWeekCount = weekCompletions.size();

        long onTimeCount = completionRepository.countOnTimeByUser(user);
        long totalCompletions = completionRepository.countByRoutine_User(user);
        double onTimeRatePercent = totalCompletions > 0 ? (onTimeCount * 100.0 / totalCompletions) : 100.0;

        Double avgSeconds = completionRepository.findAverageDurationSecondsByUser(user);
        Double avgMinutes = avgSeconds != null ? (avgSeconds / 60.0) : null;

        long totalActualSecondsThisWeek = weekCompletions.stream()
                .filter(c -> c.getActualDurationSeconds() != null)
                .mapToLong(RoutineCompletion::getActualDurationSeconds)
                .sum();

        Map<String, Long> completionsPerSessionGroup = weekCompletions.stream()
                .collect(Collectors.groupingBy(c -> c.getRoutine().getSessionGroup(), Collectors.counting()));

        return new RoutineStatsDto(
                totalRoutines,
                dueTodayCount,
                overdueCount,
                completedTodayCount,
                totalEstimatedMinutesDueToday,
                completedThisWeekCount,
                Math.round(onTimeRatePercent * 10.0) / 10.0,
                avgMinutes != null ? Math.round(avgMinutes * 10.0) / 10.0 : null,
                totalActualSecondsThisWeek,
                completionsPerSessionGroup
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Routine getRoutineById(Long id, User user) {
        return routineRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Routine non trovata: " + id));
    }

    @Override
    public Routine createRoutine(RoutineRequestDto request, User user) {
        Routine routine = new Routine();
        applyRequestToRoutine(routine, request);
        routine.setUser(user);
        return routineRepository.save(routine);
    }

    @Override
    public Routine updateRoutine(Long id, RoutineRequestDto request, User user) {
        Routine routine = getRoutineById(id, user);
        applyRequestToRoutine(routine, request);
        return routineRepository.save(routine);
    }

    @Override
    public void deleteRoutine(Long id, User user) {
        Routine routine = getRoutineById(id, user);
        routineRepository.delete(routine);
    }

    @Override
    public RoutineCompletion completeRoutine(Long id, Integer actualDurationSeconds, String notes, User user) {
        Routine routine = getRoutineById(id, user);

        LocalDateTime now = LocalDateTime.now();
        boolean onTime = !routine.getNextDueDate().isBefore(LocalDate.now());

        routine.setLastCompletedAt(now);
        routineRepository.save(routine);

        RoutineCompletion completion = new RoutineCompletion(routine, now, actualDurationSeconds, onTime);
        if (notes != null && !notes.isBlank()) {
            completion.setNotes(notes.trim());
        }
        RoutineCompletion savedCompletion = completionRepository.save(completion);

        // If this routine is linked to an Activity and user provided actual time, log to TimeTrack ActivitySession
        if (routine.getActivity() != null && actualDurationSeconds != null && actualDurationSeconds > 0) {
            LocalDateTime sessionStart = now.minusSeconds(actualDurationSeconds);
            activitySessionService.addManualSession(routine.getActivity().getId(), sessionStart, now);
        }

        return savedCompletion;
    }

    @Override
    public void undoLastCompletion(Long id, User user) {
        Routine routine = getRoutineById(id, user);
        List<RoutineCompletion> completions = completionRepository.findByRoutineOrderByCompletedAtDesc(routine);

        if (!completions.isEmpty()) {
            RoutineCompletion last = completions.get(0);
            completionRepository.delete(last);

            if (completions.size() > 1) {
                routine.setLastCompletedAt(completions.get(1).getCompletedAt());
            } else {
                routine.setLastCompletedAt(null);
            }
            routineRepository.save(routine);
        }
    }

    @Override
    public List<RoutineCompletion> completeBatchSession(List<RoutineSessionCompletionItemDto> items, User user) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        List<RoutineCompletion> results = new ArrayList<>();
        for (RoutineSessionCompletionItemDto item : items) {
            if (item.getRoutineId() != null) {
                RoutineCompletion completion = completeRoutine(
                        item.getRoutineId(),
                        item.getActualDurationSeconds(),
                        item.getNotes(),
                        user
                );
                results.add(completion);
            }
        }
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineHeatmapDto getHeatmapData(User user, int weeks) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusWeeks(weeks).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        List<RoutineCompletion> completions = completionRepository
                .findByRoutine_UserAndCompletedAtAfterOrderByCompletedAtDesc(user, startDateTime);

        Map<String, Integer> globalCompletions = new HashMap<>();
        Map<Long, Map<String, Integer>> routineCompletions = new HashMap<>();

        for (RoutineCompletion c : completions) {
            String dateKey = c.getCompletedAt().toLocalDate().toString();
            globalCompletions.merge(dateKey, 1, Integer::sum);

            Long routineId = c.getRoutine().getId();
            routineCompletions.computeIfAbsent(routineId, k -> new HashMap<>()).merge(dateKey, 1, Integer::sum);
        }

        // Calculate streaks
        int currentStreak = 0;
        LocalDate cursor = today;
        if (!globalCompletions.containsKey(cursor.toString())) {
            cursor = cursor.minusDays(1);
        }
        while (!cursor.isBefore(startDate) && globalCompletions.getOrDefault(cursor.toString(), 0) > 0) {
            currentStreak++;
            cursor = cursor.minusDays(1);
        }

        int longestStreak = 0;
        int runningStreak = 0;
        LocalDate scan = startDate;
        while (!scan.isAfter(today)) {
            if (globalCompletions.getOrDefault(scan.toString(), 0) > 0) {
                runningStreak++;
                if (runningStreak > longestStreak) {
                    longestStreak = runningStreak;
                }
            } else {
                runningStreak = 0;
            }
            scan = scan.plusDays(1);
        }

        return new RoutineHeatmapDto(
                startDate,
                endDate,
                weeks,
                globalCompletions,
                routineCompletions,
                currentStreak,
                longestStreak,
                completions.size(),
                globalCompletions.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getExistingSessionGroups(User user) {
        return routineRepository.findDistinctSessionGroupsByUser(user);
    }

    private void applyRequestToRoutine(Routine routine, RoutineRequestDto request) {
        routine.setName(request.getName().trim());
        routine.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        routine.setSessionGroup(request.getSessionGroup().trim());
        routine.setEstimatedMinutes(request.getEstimatedMinutes() != null && request.getEstimatedMinutes() > 0 ? request.getEstimatedMinutes() : 5);
        routine.setRecurrenceType(request.getRecurrenceType() != null ? request.getRecurrenceType() : RecurrenceType.DAYS_INTERVAL);
        routine.setFrequencyDays(request.getFrequencyDays() != null && request.getFrequencyDays() > 0 ? request.getFrequencyDays() : 7);
        routine.setTargetDaysOfWeek(request.getTargetDaysOfWeek() != null ? request.getTargetDaysOfWeek().trim() : null);
        routine.setChecklistItems(request.getParsedChecklistItems());

        if (request.getActivityId() != null) {
            Activity activity = activityRepository.findById(request.getActivityId()).orElse(null);
            routine.setActivity(activity);
        } else {
            routine.setActivity(null);
        }
    }
}
