package com.timetrack.routine;

import com.timetrack.activity.Activity;
import com.timetrack.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Represents a cyclical recurring task or habit (Routine).
 * Routines can be grouped into sessions (e.g., "Pulizia Bagno", "Cura Personale"),
 * have estimated durations in minutes, and recur either at fixed day intervals or on specific weekdays.
 */
@Entity
@Table(name = "routines")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Il nome della routine non può essere vuoto.")
    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * The macro session or category group for this routine (e.g. "Pulizia Bagno", "Cura Personale", "Casa").
     */
    @NotEmpty(message = "La sessione di appartenenza non può essere vuota.")
    @Column(nullable = false)
    private String sessionGroup;

    /**
     * Estimated time in minutes required to complete the routine.
     */
    @Column(nullable = false)
    private Integer estimatedMinutes = 5;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceType recurrenceType = RecurrenceType.DAYS_INTERVAL;

    /**
     * Number of days between occurrences when recurrenceType is DAYS_INTERVAL (e.g. 3, 7, 28).
     */
    private Integer frequencyDays = 7;

    /**
     * Comma-separated list of DayOfWeek names when recurrenceType is WEEKLY_DAYS (e.g. "MONDAY,THURSDAY" or "SUNDAY").
     */
    private String targetDaysOfWeek;

    /**
     * Timestamp when the routine was last completed.
     */
    private LocalDateTime lastCompletedAt;

    /**
     * Optional link to an Activity in TimeTrack for tracking hours or category statistics.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    /**
     * The user who owns this routine.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "routine_checklist_items", joinColumns = @JoinColumn(name = "routine_id"))
    @Column(name = "item_text", nullable = false)
    @OrderColumn(name = "sort_order")
    private List<String> checklistItems = new ArrayList<>();

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("completedAt desc")
    private List<RoutineCompletion> completions = new ArrayList<>();

    public Routine() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSessionGroup() {
        return sessionGroup;
    }

    public void setSessionGroup(String sessionGroup) {
        this.sessionGroup = sessionGroup;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes != null ? estimatedMinutes : 5;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType != null ? recurrenceType : RecurrenceType.DAYS_INTERVAL;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public Integer getFrequencyDays() {
        return frequencyDays != null ? frequencyDays : 7;
    }

    public void setFrequencyDays(Integer frequencyDays) {
        this.frequencyDays = frequencyDays;
    }

    public String getTargetDaysOfWeek() {
        return targetDaysOfWeek;
    }

    public void setTargetDaysOfWeek(String targetDaysOfWeek) {
        this.targetDaysOfWeek = targetDaysOfWeek;
    }

    public LocalDateTime getLastCompletedAt() {
        return lastCompletedAt;
    }

    public void setLastCompletedAt(LocalDateTime lastCompletedAt) {
        this.lastCompletedAt = lastCompletedAt;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<RoutineCompletion> getCompletions() {
        return completions;
    }

    public void setCompletions(List<RoutineCompletion> completions) {
        this.completions = completions;
    }

    public List<String> getChecklistItems() {
        return checklistItems != null ? checklistItems : Collections.emptyList();
    }

    public void setChecklistItems(List<String> checklistItems) {
        this.checklistItems = checklistItems != null ? new ArrayList<>(checklistItems) : new ArrayList<>();
    }

    /**
     * Parses targetDaysOfWeek string into a set of DayOfWeek enums.
     */
    public Set<DayOfWeek> parseDaysOfWeek() {
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        if (targetDaysOfWeek != null && !targetDaysOfWeek.isBlank()) {
            for (String part : targetDaysOfWeek.split(",")) {
                try {
                    days.add(DayOfWeek.valueOf(part.trim().toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return days;
    }

    /**
     * Calculates the next due date for this routine based on last completion and recurrence rules.
     */
    public LocalDate getNextDueDate() {
        LocalDate today = LocalDate.now();

        if (recurrenceType == RecurrenceType.WEEKLY_DAYS) {
            Set<DayOfWeek> days = parseDaysOfWeek();
            if (days.isEmpty()) {
                days = EnumSet.of(DayOfWeek.MONDAY);
            }

            if (lastCompletedAt == null) {
                if (days.contains(today.getDayOfWeek())) {
                    return today;
                }
                return findNextMatchingDay(today, days);
            }

            LocalDate lastDate = lastCompletedAt.toLocalDate();
            // If completed today, look for the next scheduled day starting tomorrow
            if (lastDate.isEqual(today)) {
                return findNextMatchingDay(today.plusDays(1), days);
            }

            // If last completed in the past, check if today is a scheduled day
            if (days.contains(today.getDayOfWeek())) {
                return today;
            }

            // Check if there was an earlier missed day this week
            LocalDate cursor = lastDate.plusDays(1);
            while (cursor.isBefore(today)) {
                if (days.contains(cursor.getDayOfWeek())) {
                    return cursor;
                }
                cursor = cursor.plusDays(1);
            }

            return findNextMatchingDay(today, days);
        }

        // DAYS_INTERVAL
        int interval = (frequencyDays != null && frequencyDays > 0) ? frequencyDays : 1;
        if (lastCompletedAt == null) {
            return today;
        }

        return lastCompletedAt.toLocalDate().plusDays(interval);
    }

    private LocalDate findNextMatchingDay(LocalDate start, Set<DayOfWeek> days) {
        LocalDate cursor = start;
        for (int i = 0; i < 7; i++) {
            if (days.contains(cursor.getDayOfWeek())) {
                return cursor;
            }
            cursor = cursor.plusDays(1);
        }
        return start;
    }

    /**
     * Determines the current status of the routine.
     */
    public RoutineStatus getStatus() {
        LocalDate today = LocalDate.now();
        if (lastCompletedAt != null && lastCompletedAt.toLocalDate().isEqual(today)) {
            return RoutineStatus.COMPLETED_TODAY;
        }

        LocalDate nextDue = getNextDueDate();
        if (nextDue.isBefore(today)) {
            return RoutineStatus.OVERDUE;
        } else if (nextDue.isEqual(today)) {
            return RoutineStatus.DUE_TODAY;
        } else {
            return RoutineStatus.UPCOMING;
        }
    }

    public boolean isDueOrOverdue() {
        RoutineStatus status = getStatus();
        return status == RoutineStatus.OVERDUE || status == RoutineStatus.DUE_TODAY;
    }

    public long getDaysOverdue() {
        LocalDate today = LocalDate.now();
        LocalDate nextDue = getNextDueDate();
        if (nextDue.isBefore(today)) {
            return ChronoUnit.DAYS.between(nextDue, today);
        }
        return 0;
    }

    public long getDaysUntilDue() {
        LocalDate today = LocalDate.now();
        LocalDate nextDue = getNextDueDate();
        if (nextDue.isAfter(today)) {
            return ChronoUnit.DAYS.between(today, nextDue);
        }
        return 0;
    }

    /**
     * Human-friendly recurrence description (e.g. "Ogni 3 giorni" or "Ogni Lun, Gio").
     */
    public String getRecurrenceDescription() {
        if (recurrenceType == RecurrenceType.WEEKLY_DAYS) {
            Set<DayOfWeek> days = parseDaysOfWeek();
            if (days.isEmpty()) return "Ogni settimana";
            List<String> shortNames = new ArrayList<>();
            for (DayOfWeek day : days) {
                switch (day) {
                    case MONDAY -> shortNames.add("Lun");
                    case TUESDAY -> shortNames.add("Mar");
                    case WEDNESDAY -> shortNames.add("Mer");
                    case THURSDAY -> shortNames.add("Gio");
                    case FRIDAY -> shortNames.add("Ven");
                    case SATURDAY -> shortNames.add("Sab");
                    case SUNDAY -> shortNames.add("Dom");
                }
            }
            return "Ogni " + String.join(", ", shortNames);
        }

        if (frequencyDays == null || frequencyDays == 1) return "Ogni giorno";
        if (frequencyDays % 7 == 0) {
            int weeks = frequencyDays / 7;
            return weeks == 1 ? "Ogni settimana" : "Ogni " + weeks + " settimane";
        }
        return "Ogni " + frequencyDays + " giorni";
    }
}
