package com.timetrack.config;

import com.timetrack.activity.Activity;
import com.timetrack.activity.ActivityRepository;
import com.timetrack.activity.ActivityType;
import com.timetrack.activitySession.ActivitySession;
import com.timetrack.activitySession.ActivitySessionRepository;
import com.timetrack.auth.User;
import com.timetrack.auth.UserRepository;
import com.timetrack.category.Category;
import com.timetrack.category.CategoryRepository;
import com.timetrack.goal.Goal;
import com.timetrack.goal.GoalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Data seeder that provisions a realistic demo account if it does not already exist,
 * and ensures that the demo account ALWAYS has fresh, populated sessions for the current week.
 * This guarantees that whenever a recruiter or visitor opens the demo at any date,
 * the dashboard, charts, and statistics are richly populated out of the box.
 */
@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ActivityRepository activityRepository;
    private final ActivitySessionRepository sessionRepository;
    private final GoalRepository goalRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          ActivityRepository activityRepository,
                          ActivitySessionRepository sessionRepository,
                          GoalRepository goalRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
        this.goalRepository = goalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User demoUser = userRepository.findByUsername("demo");
        if (demoUser == null) {
            demoUser = initDemoUserAndStructure();
        }
        ensureFreshDemoSessions(demoUser);
    }

    @Transactional
    public User initDemoUserAndStructure() {
        // 1. Create Demo User
        User demoUser = new User();
        demoUser.setUsername("demo");
        demoUser.setPassword(passwordEncoder.encode("demo1234"));
        demoUser = userRepository.save(demoUser);

        // 2. Categories
        Category catDev = new Category("Software Development");
        catDev.setCreatedBy(demoUser);
        catDev = categoryRepository.save(catDev);

        Category catDevOps = new Category("DevOps & Cloud");
        catDevOps.setCreatedBy(demoUser);
        catDevOps = categoryRepository.save(catDevOps);

        Category catStudy = new Category("Ricerca & Studio");
        catStudy.setCreatedBy(demoUser);
        catStudy = categoryRepository.save(catStudy);

        Category catFitness = new Category("Sport & Benessere");
        catFitness.setCreatedBy(demoUser);
        catFitness = categoryRepository.save(catFitness);

        // 3. Activities
        Activity actSpring = new Activity();
        actSpring.setName("Spring Boot Microservices");
        actSpring.setDescription("Sviluppo backend modulare e hardening delle API REST");
        actSpring.setCategory(catDev);
        actSpring.setCreatedBy(demoUser);
        actSpring.setActivityType(ActivityType.GOAL);
        actSpring.setTime(Duration.ofHours(20));
        activityRepository.save(actSpring);

        Activity actDocker = new Activity();
        actDocker.setName("Docker Containerization");
        actDocker.setDescription("Costruzione immagini multi-stage e orchestrazione compose");
        actDocker.setCategory(catDevOps);
        actDocker.setCreatedBy(demoUser);
        actDocker.setActivityType(ActivityType.GOAL);
        actDocker.setTime(Duration.ofHours(10));
        activityRepository.save(actDocker);

        Activity actAlgo = new Activity();
        actAlgo.setName("Algoritmi & Strutture Dati");
        actAlgo.setDescription("Approfondimento complessità computazionale e alberi binari");
        actAlgo.setCategory(catStudy);
        actAlgo.setCreatedBy(demoUser);
        actAlgo.setActivityType(ActivityType.GOAL);
        actAlgo.setTime(Duration.ofHours(8));
        activityRepository.save(actAlgo);

        Activity actRun = new Activity();
        actRun.setName("Corsa e Cardio");
        actRun.setDescription("Sessioni di allenamento aerobico e recupero attivo");
        actRun.setCategory(catFitness);
        actRun.setCreatedBy(demoUser);
        actRun.setActivityType(ActivityType.MAX_TIME);
        actRun.setTime(Duration.ofHours(5));
        activityRepository.save(actRun);

        // 4. Goals
        Goal goalDev = new Goal();
        goalDev.setUser(demoUser);
        goalDev.setCategory(catDev);
        goalDev.setTargetDuration(Duration.ofHours(15));
        goalDev.setPeriod(Goal.GoalPeriod.WEEKLY);
        goalRepository.save(goalDev);

        Goal goalDocker = new Goal();
        goalDocker.setUser(demoUser);
        goalDocker.setActivity(actDocker);
        goalDocker.setTargetDuration(Duration.ofHours(6));
        goalDocker.setPeriod(Goal.GoalPeriod.WEEKLY);
        goalRepository.save(goalDocker);

        Goal goalStudy = new Goal();
        goalStudy.setUser(demoUser);
        goalStudy.setCategory(catStudy);
        goalStudy.setTargetDuration(Duration.ofHours(8));
        goalStudy.setPeriod(Goal.GoalPeriod.WEEKLY);
        goalRepository.save(goalStudy);

        return demoUser;
    }

    @Transactional
    public void ensureFreshDemoSessions(User demoUser) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<ActivitySession> weekSessions = sessionRepository.findByActivityCreatedByAndStartTimeAfter(demoUser, startOfWeek);

        if (weekSessions.size() < 3) {
            List<Activity> activities = activityRepository.findByCreatedBy_Username(demoUser.getUsername());
            if (activities.isEmpty()) {
                return;
            }

            Activity actSpring = activities.stream().filter(a -> a.getName().contains("Spring")).findFirst().orElse(activities.get(0));
            Activity actDocker = activities.stream().filter(a -> a.getName().contains("Docker")).findFirst().orElse(activities.get(0));
            Activity actAlgo = activities.stream().filter(a -> a.getName().contains("Algoritmi")).findFirst().orElse(activities.get(0));
            Activity actRun = activities.stream().filter(a -> a.getName().contains("Corsa")).findFirst().orElse(activities.get(0));

            LocalDateTime monday = startOfWeek.withHour(9).withMinute(0);

            // Sessions on Monday
            createSession(actSpring, monday.plusHours(1), monday.plusHours(4).plusMinutes(30)); // 3h 30m
            createSession(actAlgo, monday.plusHours(5), monday.plusHours(7)); // 2h

            // If today is Tuesday or later
            if (now.getDayOfWeek() != DayOfWeek.MONDAY) {
                LocalDateTime midWeek = monday.plusDays(1);
                createSession(actDocker, midWeek.withHour(10), midWeek.withHour(12).plusMinutes(30)); // 2h 30m
                createSession(actRun, midWeek.withHour(18), midWeek.withHour(19)); // 1h
            }

            // If today is Wednesday or later
            if (now.getDayOfWeek().getValue() >= DayOfWeek.WEDNESDAY.getValue()) {
                LocalDateTime wednesday = monday.plusDays(2);
                createSession(actSpring, wednesday.withHour(14), wednesday.withHour(17).plusMinutes(45)); // 3h 45m
                createSession(actAlgo, wednesday.withHour(10), wednesday.withHour(12)); // 2h
            }

            // Always seed at least 2 sessions for TODAY
            LocalDateTime todayMorning = now.withHour(9).withMinute(15);
            if (todayMorning.isBefore(now)) {
                LocalDateTime endMorning = todayMorning.plusHours(1).plusMinutes(45);
                if (endMorning.isAfter(now)) {
                    endMorning = now.minusMinutes(10);
                }
                createSession(actSpring, todayMorning, endMorning);
            }

            LocalDateTime todayAfternoon = now.minusMinutes(50);
            createSession(actDocker, todayAfternoon, now.minusMinutes(5));
        }
    }

    private void createSession(Activity activity, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            return;
        }
        ActivitySession session = new ActivitySession();
        session.setActivity(activity);
        session.setStartTime(start);
        session.setEndTime(end);
        sessionRepository.save(session);
    }
}
