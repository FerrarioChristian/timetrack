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
import com.timetrack.routine.*;
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
    private final RoutineRepository routineRepository;
    private final RoutineCompletionRepository routineCompletionRepository;

    public DemoDataSeeder(UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          ActivityRepository activityRepository,
                          ActivitySessionRepository sessionRepository,
                          GoalRepository goalRepository,
                          PasswordEncoder passwordEncoder,
                          RoutineRepository routineRepository,
                          RoutineCompletionRepository routineCompletionRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
        this.goalRepository = goalRepository;
        this.passwordEncoder = passwordEncoder;
        this.routineRepository = routineRepository;
        this.routineCompletionRepository = routineCompletionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User demoUser = userRepository.findByUsername("demo");
        if (demoUser == null) {
            demoUser = initDemoUserAndStructure();
        }
        ensureFreshDemoSessions(demoUser);
        ensureDemoRoutines(demoUser);
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

    @Transactional
    public void ensureDemoRoutines(User demoUser) {
        if (routineRepository.countByUser(demoUser) > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. Pulizia Bagno
        Routine lavandinoBagno = createRoutine(demoUser, "Lavandino Bagno", "Pulizia e sanificazione lavandino e specchio",
                "Pulizia Bagno", 3, RecurrenceType.DAYS_INTERVAL, 3, null, now.minusDays(3));

        Routine wc = createRoutine(demoUser, "WC", "Igienizzazione completa water",
                "Pulizia Bagno", 3, RecurrenceType.DAYS_INTERVAL, 4, null, now.minusDays(5)); // overdue by 1 day!

        Routine bidet = createRoutine(demoUser, "Bidet", "Pulizia e disinfezione bidet",
                "Pulizia Bagno", 2, RecurrenceType.DAYS_INTERVAL, 4, null, now.minusDays(4)); // due today

        createRoutine(demoUser, "Doccia", "Lavaggio fondo e box doccia",
                "Pulizia Bagno", 8, RecurrenceType.DAYS_INTERVAL, 10, null, now.minusDays(6));

        createRoutine(demoUser, "Doccino", "Decalcificazione e pulizia doccino",
                "Pulizia Bagno", 3, RecurrenceType.DAYS_INTERVAL, 14, null, now.minusDays(10));

        createRoutine(demoUser, "Portasapone Doccia", "Lavaggio e rimozione residui sapone",
                "Pulizia Bagno", 2, RecurrenceType.DAYS_INTERVAL, 14, null, now.minusDays(12));

        createRoutine(demoUser, "Pareti Doccia", "Trattamento anticalcare vetri e piastrelle",
                "Pulizia Bagno", 8, RecurrenceType.DAYS_INTERVAL, 21, null, now.minusDays(15));

        createRoutine(demoUser, "Tappeto Bagno", "Lavaggio in lavatrice tappeto",
                "Pulizia Bagno", 5, RecurrenceType.DAYS_INTERVAL, 28, null, now.minusDays(20));

        // 2. Cucina & Casa
        Routine lavandinoCucina = createRoutine(demoUser, "Lavandino Cucina", "Sgrassatura vasca e sgombero piano",
                "Cucina & Casa", 4, RecurrenceType.DAYS_INTERVAL, 3, null, now.minusDays(3)); // due today

        createRoutine(demoUser, "Microonde", "Pulizia interna e piatto girevole",
                "Cucina & Casa", 8, RecurrenceType.DAYS_INTERVAL, 14, null, now.minusDays(14)); // due today

        createRoutine(demoUser, "Frigorifero", "Controllo scadenze e pulizia ripiani",
                "Cucina & Casa", 15, RecurrenceType.DAYS_INTERVAL, 30, null, now.minusDays(22));

        // 3. Cura Personale
        Routine barba = createRoutine(demoUser, "Regolazione Barba", "Rifinitura contorni e regolazione lunghezza",
                "Cura Personale", 12, RecurrenceType.DAYS_INTERVAL, 3, null, now.minusDays(3)); // due today

        createRoutine(demoUser, "Taglio Unghie", "Manicure e limatura",
                "Cura Personale", 8, RecurrenceType.DAYS_INTERVAL, 7, null, now.minusDays(2));

        // 4. Dispositivi & Salute
        Routine dispositivi = createRoutine(demoUser, "Disinfettare Dispositivi", "Disinfezione alcolica smartphone, auricolari e tastiera",
                "Dispositivi & Salute", 8, RecurrenceType.DAYS_INTERVAL, 7, null, now.minusDays(7)); // due today

        createRoutine(demoUser, "Riempire Omega3 & Integratori", "Preparazione dosatore settimanale",
                "Dispositivi & Salute", 2, RecurrenceType.WEEKLY_DAYS, null, "SUNDAY", now.minusDays(7));

        // Seed some historical completions for analytics
        seedHistoricalCompletion(lavandinoBagno, now.minusDays(3), 190, true);
        seedHistoricalCompletion(lavandinoBagno, now.minusDays(6), 175, true);
        seedHistoricalCompletion(lavandinoBagno, now.minusDays(9), 210, true);

        seedHistoricalCompletion(bidet, now.minusDays(4), 110, true);
        seedHistoricalCompletion(bidet, now.minusDays(8), 125, true);

        seedHistoricalCompletion(lavandinoCucina, now.minusDays(3), 230, true);
        seedHistoricalCompletion(lavandinoCucina, now.minusDays(6), 245, true);

        seedHistoricalCompletion(barba, now.minusDays(3), 710, true);
        seedHistoricalCompletion(barba, now.minusDays(6), 690, true);

        seedHistoricalCompletion(dispositivi, now.minusDays(7), 460, true);
    }

    private Routine createRoutine(User user, String name, String description, String sessionGroup,
                                  int estimatedMinutes, RecurrenceType recurrenceType, Integer frequencyDays,
                                  String targetDaysOfWeek, LocalDateTime lastCompletedAt) {
        Routine routine = new Routine();
        routine.setUser(user);
        routine.setName(name);
        routine.setDescription(description);
        routine.setSessionGroup(sessionGroup);
        routine.setEstimatedMinutes(estimatedMinutes);
        routine.setRecurrenceType(recurrenceType);
        routine.setFrequencyDays(frequencyDays);
        routine.setTargetDaysOfWeek(targetDaysOfWeek);
        routine.setLastCompletedAt(lastCompletedAt);
        routine.setCreatedAt(lastCompletedAt != null ? lastCompletedAt.minusDays(30) : LocalDateTime.now().minusDays(30));
        return routineRepository.save(routine);
    }

    private void seedHistoricalCompletion(Routine routine, LocalDateTime completedAt, int durationSeconds, boolean onTime) {
        RoutineCompletion completion = new RoutineCompletion(routine, completedAt, durationSeconds, onTime);
        routineCompletionRepository.save(completion);
    }
}
