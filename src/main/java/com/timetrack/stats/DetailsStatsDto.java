package com.timetrack.stats;

import java.time.Duration;

public class DetailsStatsDto {

    private Duration sessionAverageLastWeek;
    private Duration sessionAverageLastMonth;
    private Duration dailyAverageLastWeek;
    private Duration dailyAverageLastMonth;
    private Duration totalTimeLastWeek;
    private Duration totalTimeLastMonth;

    public DetailsStatsDto(Duration sessionAverageLastWeek, Duration sessionAverageLastMonth,
                    Duration dailyAverageLastWeek, Duration dailyAverageLastMonth,
                    Duration totalTimeLastWeek, Duration totalTimeLastMonth) {
        this.sessionAverageLastWeek = sessionAverageLastWeek;
        this.sessionAverageLastMonth = sessionAverageLastMonth;
        this.dailyAverageLastWeek = dailyAverageLastWeek;
        this.dailyAverageLastMonth = dailyAverageLastMonth;
        this.totalTimeLastWeek = totalTimeLastWeek;
        this.totalTimeLastMonth = totalTimeLastMonth;
    }

    public Duration getSessionAverageLastWeek() {
        return sessionAverageLastWeek;
    }

    public void setSessionAverageLastWeek(Duration sessionAverageLastWeek) {
        this.sessionAverageLastWeek = sessionAverageLastWeek;
    }

    public Duration getSessionAverageLastMonth() {
        return sessionAverageLastMonth;
    }

    public void setSessionAverageLastMonth(Duration sessionAverageLastMonth) {
        this.sessionAverageLastMonth = sessionAverageLastMonth;
    }

    public Duration getDailyAverageLastWeek() {
        return dailyAverageLastWeek;
    }

    public void setDailyAverageLastWeek(Duration dailyAverageLastWeek) {
        this.dailyAverageLastWeek = dailyAverageLastWeek;
    }

    public Duration getDailyAverageLastMonth() {
        return dailyAverageLastMonth;
    }

    public void setDailyAverageLastMonth(Duration dailyAverageLastMonth) {
        this.dailyAverageLastMonth = dailyAverageLastMonth;
    }

    public Duration getTotalTimeLastWeek() {
        return totalTimeLastWeek;
    }

    public void setTotalTimeLastWeek(Duration totalTimeLastWeek) {
        this.totalTimeLastWeek = totalTimeLastWeek;
    }

    public Duration getTotalTimeLastMonth() {
        return totalTimeLastMonth;
    }

    public void setTotalTimeLastMonth(Duration totalTimeLastMonth) {
        this.totalTimeLastMonth = totalTimeLastMonth;
    }
}
