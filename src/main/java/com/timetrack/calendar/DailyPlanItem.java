package com.timetrack.calendar;

import com.timetrack.activity.Activity;
import com.timetrack.auth.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_plan_items")
public class DailyPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DailyPlanType itemType = DailyPlanType.EVENT;

    private Integer plannedMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DailyPlanItem() {}

    public DailyPlanItem(User user, LocalDate date, String title, DailyPlanType itemType, Integer plannedMinutes, Activity activity, String notes) {
        this.user = user;
        this.date = date;
        this.title = title;
        this.itemType = itemType != null ? itemType : DailyPlanType.EVENT;
        this.plannedMinutes = plannedMinutes;
        this.activity = activity;
        this.notes = notes;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DailyPlanType getItemType() {
        return itemType;
    }

    public void setItemType(DailyPlanType itemType) {
        this.itemType = itemType;
    }

    public Integer getPlannedMinutes() {
        return plannedMinutes;
    }

    public void setPlannedMinutes(Integer plannedMinutes) {
        this.plannedMinutes = plannedMinutes;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
