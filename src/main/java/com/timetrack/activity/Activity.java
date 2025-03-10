package com.timetrack.activity;

import com.timetrack.activitySession.ActivitySession;
import com.timetrack.auth.User;
import com.timetrack.category.Category;
import com.timetrack.converter.DurationToStringConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.Duration;
import java.util.List;


/**
 * Represents an activity that can be tracked by the user.
 */
@Entity
public class Activity {
    /**
     * The unique identifier of the activity.
     * This is the primary key of the activity table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the activity.
     * This field is required and cannot be empty.
     */
    @NotEmpty(message = "Activity name cannot be empty.")
    private String name;

    /**
     * The description of the activity.
     * This field is optional.
     */
    private String description;

    /**
     * The user who created the activity.
     * This field is required and cannot be empty.
     */
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * The sessions of the activity.
     * This field is a list of activity sessions.
     * The sessions are ordered by the start time in descending order.
     * The sessions are deleted when the activity is deleted.
     */
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startTime desc")
    private List<ActivitySession> sessions;

    /**
     * The category of the activity.
     * This field is required and cannot be empty.
     */
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Category category;
    
    /**
     * The type of the activity.
     * This field is required and cannot be empty.
     */
    private ActivityType activityType;

    /**
     * The time spent on the activity.
     * This field is required and cannot be empty.
     */
    private Duration time;

    /**
     * Creates a new activity with the given parameters.
     *
     * @param id The unique identifier of the activity.
     * @param name The name of the activity.
     * @param description The description of the activity.
     * @param activityType The type of the activity.
     */
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
