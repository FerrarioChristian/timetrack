package com.timetrack.routine;

/**
 * Defines the recurrence strategy for a routine.
 */
public enum RecurrenceType {
    /**
     * Recurring every N days from the last completion date (e.g., every 3 days, 14 days, 28 days).
     */
    DAYS_INTERVAL,

    /**
     * Recurring on specific days of the week (e.g., every Monday and Thursday, or every Sunday).
     */
    WEEKLY_DAYS
}
