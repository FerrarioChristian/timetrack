package com.timetrack.calendar;

public enum DailyPlanType {
    WORK_TARGET("Obiettivo Lavoro / Studio"),
    EVENT("Evento / Impegno"),
    PERSONAL("Personale / Altro");

    private final String displayName;

    DailyPlanType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
