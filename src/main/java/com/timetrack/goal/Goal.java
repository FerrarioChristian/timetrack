package com.timetrack.goal;

import com.timetrack.activity.Activity;
import com.timetrack.auth.User;
import com.timetrack.category.Category;
import jakarta.persistence.*;
import java.time.Duration;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Column(nullable = false)
    private Duration targetDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalPeriod period;

    public enum GoalPeriod {
        WEEKLY, MONTHLY
    }

    public Goal() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }
    public Duration getTargetDuration() { return targetDuration; }
    public void setTargetDuration(Duration targetDuration) { this.targetDuration = targetDuration; }
    public GoalPeriod getPeriod() { return period; }
    public void setPeriod(GoalPeriod period) { this.period = period; }
}
