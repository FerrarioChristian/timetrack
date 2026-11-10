package com.timetrack.routine;

/**
 * Calculated status of a routine relative to the current date.
 */
public enum RoutineStatus {
    /**
     * The routine should have been completed before today and is overdue.
     */
    OVERDUE,

    /**
     * The routine is scheduled to be completed today.
     */
    DUE_TODAY,

    /**
     * The routine has already been completed today.
     */
    COMPLETED_TODAY,

    /**
     * The routine is completed and scheduled for a future date.
     */
    UPCOMING
}
