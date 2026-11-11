package com.timetrack.calendar;

import com.timetrack.auth.User;

import java.time.LocalDate;

public interface CalendarService {

    CalendarViewDto getCalendarView(int year, int month, User user);

    CalendarDayDto getDayAgenda(LocalDate date, User user);

    DailyPlanItem createPlanItem(DailyPlanItemRequestDto request, User user);

    DailyPlanItem togglePlanItem(Long id, User user);

    void deletePlanItem(Long id, User user);
}
