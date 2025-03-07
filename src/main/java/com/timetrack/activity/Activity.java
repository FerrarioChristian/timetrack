package com.timetrack.activity;

import com.timetrack.activitySession.ActivitySession;
import com.timetrack.auth.User;
import com.timetrack.category.Category;
import com.timetrack.converter.DurationToStringConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.Duration;
import java.util.List;


@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Activity name cannot be empty.")
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startTime desc")
    private List<ActivitySession> sessions;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Category category;
    private ActivityType activityType;
    private Duration time;

    public Activity(Long id, String name, String description, ActivityType activityType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activityType = activityType;
    }

    public Activity() {
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

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public List<ActivitySession> getSessions() {
        return sessions;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Duration getTime() {
        return time;
    }

    public void setTime(Duration time) {
        this.time = time;
    }

    public String getTimeString() {
        return new DurationToStringConverter().convert(time);
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Activity{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", category=" + category + ", activityType=" + activityType + ", time=" + time + '}';
    }
}
