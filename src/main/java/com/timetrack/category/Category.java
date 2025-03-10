package com.timetrack.category;

import com.timetrack.activity.Activity;
import com.timetrack.auth.User;
import jakarta.persistence.*;

import java.util.List;

/**
 * Represents a category of activities.
 */
@Entity
public class Category {
    /**
     * The unique identifier of the category.
     * This is the primary key of the table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the category.
     * This is a required field.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The user who created the category.
     * This is a required field.
     */
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * The activities that belong to the category.
     * This is a one-to-many relationship.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Activity> activities;

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", activities=" + activities +
                '}';
    }
}
