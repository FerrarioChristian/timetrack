package com.timetrack.calendar;

import com.timetrack.activity.Activity;
import com.timetrack.activity.ActivityRepository;
import com.timetrack.activitySession.ActivitySession;
import com.timetrack.activitySession.ActivitySessionRepository;
import com.timetrack.auth.User;
import com.timetrack.routine.RoutineCalendarEventDto;
import com.timetrack.routine.RoutineCalendarMonthDto;
import com.timetrack.routine.RoutineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CalendarServiceImpl implements CalendarService {

    private final DailyPlanItemRepository planItemRepository;
    private final ActivitySessionRepository sessionRepository;
    private final RoutineService routineService;
    private final ActivityRepository activityRepository;

    public CalendarServiceImpl(DailyPlanItemRepository planItemRepository,
                               ActivitySessionRepository sessionRepository,
                               RoutineService routineService,
                               ActivityRepository activityRepository) {
        this.planItemRepository = planItemRepository;
        this.sessionRepository = sessionRepository;
        this.routineService = routineService;
        this.activityRepository = activityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarViewDto getCalendarView(int year, int month, User user) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1);
        LocalDate gridStart = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate gridEnd = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String rawMonth = firstDayOfMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        String monthName = Character.toUpperCase(rawMonth.charAt(0)) + rawMonth.substring(1) + " " + year;

        // 1. Fetch routines calendar data
        RoutineCalendarMonthDto routineMonth = routineService.getCalendarMonth(year, month, user);
        Map<String, List<RoutineCalendarEventDto>> routineEventsByDate = routineMonth.eventsByDate();

        // 2. Fetch tracked sessions in grid range
        LocalDateTime startDateTime = gridStart.atStartOfDay();
        LocalDateTime endDateTime = gridEnd.atTime(LocalTime.MAX);
        List<ActivitySession> sessions = sessionRepository
                .findByActivityCreatedByAndStartTimeBetween(user, startDateTime, endDateTime);

        Map<String, List<TrackedSessionDto>> sessionsByDate = new HashMap<>();
        for (ActivitySession s : sessions) {
            if (s.getStartTime() == null) continue;
            LocalDate sDate = s.getStartTime().toLocalDate();
            String key = sDate.toString();

            long durSec = s.getDuration().getSeconds();
            long mins = durSec / 60;
            long hours = mins / 60;
            long remMins = mins % 60;
            String fmt = (hours > 0) ? (hours + "h " + remMins + "m") : (remMins + "m");

            String color = "#3b82f6";

            TrackedSessionDto dto = new TrackedSessionDto(
                    s.getId(),
                    s.getActivity() != null ? s.getActivity().getId() : null,
                    s.getActivity() != null ? s.getActivity().getName() : "Attività",
                    color,
                    s.getStartTime(),
                    s.getEndTime(),
                    durSec,
                    fmt
            );
            sessionsByDate.computeIfAbsent(key, k -> new ArrayList<>()).add(dto);
        }

        // 3. Fetch daily plan items in grid range
        List<DailyPlanItem> planItems = planItemRepository
                .findByUserAndDateBetweenOrderByDateAscIdAsc(user, gridStart, gridEnd);

        Map<String, List<DailyPlanItemViewDto>> workTargetsByDate = new HashMap<>();
        Map<String, List<DailyPlanItemViewDto>> eventsByDate = new HashMap<>();

        for (DailyPlanItem item : planItems) {
            String key = item.getDate().toString();
            DailyPlanItemViewDto dto = DailyPlanItemViewDto.fromEntity(item);
            if (item.getItemType() == DailyPlanType.WORK_TARGET) {
                workTargetsByDate.computeIfAbsent(key, k -> new ArrayList<>()).add(dto);
            } else {
                eventsByDate.computeIfAbsent(key, k -> new ArrayList<>()).add(dto);
            }
        }

        // 4. Construct CalendarDayDto for each day
        LocalDate today = LocalDate.now();
        List<CalendarDayDto> dayList = new ArrayList<>();
        Map<String, CalendarDayDto> dayMap = new LinkedHashMap<>();

        int totalTrackedMinutesMonth = 0;
        int totalTargetMinutesMonth = 0;
        int totalRoutinesCompletedMonth = 0;
        int totalEventsCompletedMonth = 0;

        LocalDate cursor = gridStart;
        while (!cursor.isAfter(gridEnd)) {
            String key = cursor.toString();
            boolean isCurrentMonth = (cursor.getMonthValue() == month);
            boolean isToday = cursor.isEqual(today);

            List<DailyPlanItemViewDto> dayWorkTargets = workTargetsByDate.getOrDefault(key, Collections.emptyList());
            List<DailyPlanItemViewDto> dayEvents = eventsByDate.getOrDefault(key, Collections.emptyList());
            List<TrackedSessionDto> daySessions = sessionsByDate.getOrDefault(key, Collections.emptyList());
            List<RoutineCalendarEventDto> dayRoutines = routineEventsByDate.getOrDefault(key, Collections.emptyList());

            int dayTargetMins = dayWorkTargets.stream()
                    .mapToInt(wt -> wt.plannedMinutes() != null ? wt.plannedMinutes() : 0)
                    .sum();

            int dayTrackedMins = (int) daySessions.stream()
                    .mapToLong(s -> s.durationSeconds() / 60)
                    .sum();

            int progressPercent = 0;
            if (dayTargetMins > 0) {
                progressPercent = Math.min(100, Math.round(((float) dayTrackedMins / dayTargetMins) * 100));
            }

            int completedRoutines = (int) dayRoutines.stream()
                    .filter(r -> "COMPLETED".equals(r.status()))
                    .count();
            int dueRoutines = (int) dayRoutines.stream()
                    .filter(r -> "DUE_TODAY".equals(r.status()) || "OVERDUE".equals(r.status()))
                    .count();
            int projectedRoutines = (int) dayRoutines.stream()
                    .filter(r -> "PROJECTED_FUTURE".equals(r.status()))
                    .count();

            int completedEvents = (int) dayEvents.stream()
                    .filter(DailyPlanItemViewDto::completed)
                    .count();

            if (isCurrentMonth) {
                totalTrackedMinutesMonth += dayTrackedMins;
                totalTargetMinutesMonth += dayTargetMins;
                totalRoutinesCompletedMonth += completedRoutines;
                totalEventsCompletedMonth += completedEvents;
            }

            String dayOfWeek = cursor.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
            dayOfWeek = Character.toUpperCase(dayOfWeek.charAt(0)) + dayOfWeek.substring(1);

            CalendarDayDto dayDto = new CalendarDayDto(
                    cursor,
                    key,
                    cursor.getDayOfMonth(),
                    isToday,
                    isCurrentMonth,
                    dayOfWeek,
                    dayWorkTargets,
                    dayTargetMins,
                    dayTrackedMins,
                    formatMinutes(dayTargetMins),
                    formatMinutes(dayTrackedMins),
                    progressPercent,
                    daySessions,
                    dayRoutines,
                    completedRoutines,
                    dueRoutines,
                    projectedRoutines,
                    dayEvents,
                    completedEvents,
                    dayEvents.size()
            );

            dayList.add(dayDto);
            dayMap.put(key, dayDto);

            cursor = cursor.plusDays(1);
        }

        return new CalendarViewDto(
                year,
                month,
                monthName,
                gridStart,
                gridEnd,
                dayList,
                dayMap,
                totalTrackedMinutesMonth,
                formatMinutes(totalTrackedMinutesMonth),
                totalTargetMinutesMonth,
                formatMinutes(totalTargetMinutesMonth),
                totalRoutinesCompletedMonth,
                totalEventsCompletedMonth
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarDayDto getDayAgenda(LocalDate date, User user) {
        CalendarViewDto view = getCalendarView(date.getYear(), date.getMonthValue(), user);
        return view.daysByDate().get(date.toString());
    }

    @Override
    public DailyPlanItem createPlanItem(DailyPlanItemRequestDto request, User user) {
        if (request.date() == null) {
            throw new IllegalArgumentException("La data è obbligatoria");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("Il titolo è obbligatorio");
        }

        Activity activity = null;
        if (request.activityId() != null) {
            activity = activityRepository.findById(request.activityId()).orElse(null);
        }

        int plannedMins = request.computePlannedMinutes();

        DailyPlanItem item = new DailyPlanItem(
                user,
                request.date(),
                request.title().trim(),
                request.itemType() != null ? request.itemType() : DailyPlanType.EVENT,
                plannedMins > 0 ? plannedMins : null,
                activity,
                request.notes()
        );

        return planItemRepository.save(item);
    }

    @Override
    public DailyPlanItem togglePlanItem(Long id, User user) {
        DailyPlanItem item = planItemRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Elemento non trovato"));
        item.setCompleted(!item.isCompleted());
        return planItemRepository.save(item);
    }

    @Override
    public void deletePlanItem(Long id, User user) {
        DailyPlanItem item = planItemRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Elemento non trovato"));
        planItemRepository.delete(item);
    }

    private String formatMinutes(int totalMins) {
        if (totalMins <= 0) return "0m";
        int h = totalMins / 60;
        int m = totalMins % 60;
        if (h > 0 && m > 0) {
            return h + "h " + m + "m";
        } else if (h > 0) {
            return h + "h";
        } else {
            return m + "m";
        }
    }
}
