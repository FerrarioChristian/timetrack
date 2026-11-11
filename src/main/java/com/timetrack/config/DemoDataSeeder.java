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
    private final com.timetrack.calendar.DailyPlanItemRepository planItemRepository;

    public DemoDataSeeder(UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          ActivityRepository activityRepository,
                          ActivitySessionRepository sessionRepository,
                          GoalRepository goalRepository,
                          PasswordEncoder passwordEncoder,
                          RoutineRepository routineRepository,
                          RoutineCompletionRepository routineCompletionRepository,
                          com.timetrack.calendar.DailyPlanItemRepository planItemRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
        this.goalRepository = goalRepository;
        this.passwordEncoder = passwordEncoder;
        this.routineRepository = routineRepository;
        this.routineCompletionRepository = routineCompletionRepository;
        this.planItemRepository = planItemRepository;
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
        ensureDemoPlanItems(demoUser);
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
            // Check if user has legacy specific seeds to replace with clean generic demo data
            boolean hasOldData = routineRepository.findByUserOrderBySessionGroupAscNameAsc(demoUser)
                    .stream().anyMatch(r -> r.getName().contains("Lavandino Bagno") || r.getName().contains("Omega3") || r.getName().contains("Barba"));
            if (hasOldData) {
                routineRepository.deleteAll(routineRepository.findByUserOrderBySessionGroupAscNameAsc(demoUser));
            } else {
                return;
            }
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. Manutenzione & Setup (Workstation, Backup & IT)
        Routine puliziaWorkstation = createRoutine(demoUser, "Pulizia Workstation & Monitor", "Igienizzazione superficie scrivania, schermi e periferiche",
                "Manutenzione & Setup", 5, RecurrenceType.DAYS_INTERVAL, 7, null, now.minusDays(7),
                List.of("Spolverare schermi con microfibra", "Sanificare tastiera e mouse", "Sistemazione e riordino cavi")); // due today

        Routine backupFile = createRoutine(demoUser, "Backup Dati & Pulizia Download", "Manutenzione file locali e sync archivio cloud",
                "Manutenzione & Setup", 10, RecurrenceType.DAYS_INTERVAL, 14, null, now.minusDays(14),
                List.of("Svuotare cartella Download", "Verificare sync cloud drive / NAS", "Controllo spazio libero su disco")); // due today

        createRoutine(demoUser, "Aggiornamenti & Manutenzione OS", "Verifica aggiornamenti sicurezza sistema e pulizia cache",
                "Manutenzione & Setup", 15, RecurrenceType.DAYS_INTERVAL, 21, null, now.minusDays(14),
                List.of("Aggiornamenti di sistema e pacchetti", "Pulizia cache temporanea", "Riavvio e test diagnostico rapido")); // upcoming

        createRoutine(demoUser, "Check Sicurezza & Audit Credenziali", "Revisione accessi attivi e avvisi del password manager",
                "Manutenzione & Setup", 10, RecurrenceType.DAYS_INTERVAL, 30, null, now.minusDays(32),
                List.of("Verifica alert password riutilizzate o violate", "Controllo sessioni attive aperte", "Test recupero chiavi 2FA")); // overdue by 2 days!

        // 2. Cura della Casa & Spazi
        Routine aspirapolvere = createRoutine(demoUser, "Aspirapolvere & Pavimenti", "Pulizia approfondita pavimenti zona studio e living",
                "Cura della Casa", 15, RecurrenceType.DAYS_INTERVAL, 7, null, now.minusDays(7),
                List.of("Sgombero superfici a terra", "Aspirapolvere completo", "Lavaggio con detergente neutro")); // due today

        Routine superficiCucina = createRoutine(demoUser, "Sanificazione Superfici & Cucina", "Sgrassatura piano lavoro e igienizzazione lavello",
                "Cura della Casa", 8, RecurrenceType.DAYS_INTERVAL, 3, null, now.minusDays(3),
                List.of("Sgrassare piano cottura e paraschizzi", "Igienizzare lavello e miscelatore", "Cambio canovacci puliti")); // due today

        Routine scadenzeFrigo = createRoutine(demoUser, "Controllo Scadenze Frigo & Dispensa", "Verifica alimenti, pulizia ripiani e lista rifornimenti",
                "Cura della Casa", 10, RecurrenceType.DAYS_INTERVAL, 14, null, now.minusDays(14),
                List.of("Controllo date scadenze e scarti", "Pulizia ripiani con aceto o sgrassatore", "Compilazione lista spesa essenziali")); // due today

        createRoutine(demoUser, "Cambio Biancheria & Lenzuola", "Sostituzione e lavaggio settimanale biancheria",
                "Cura della Casa", 10, RecurrenceType.DAYS_INTERVAL, 14, null, now.minusDays(10),
                List.of("Rimozione lenzuola e federe", "Lavaggio a 60°", "Rifacimento completo")); // upcoming

        // 3. Benessere & Abitudini
        Routine stretching = createRoutine(demoUser, "Stretching Posturale & Mobilità", "Esercizi di decontrazione cervicale e colonna vertebrale",
                "Benessere & Abitudini", 10, RecurrenceType.DAYS_INTERVAL, 1, null, now.minusDays(1),
                List.of("Mobilizzazione spalle e collo", "Allungamento lombare e ischiocrurali", "Respirazione diaframmatica guidata")); // due today

        Routine idratazione = createRoutine(demoUser, "Checkup Idratazione & Tracker", "Controllo apporto idrico giornaliero e pause attive",
                "Benessere & Abitudini", 3, RecurrenceType.DAYS_INTERVAL, 1, null, now.minusDays(1),
                List.of("Bicchiere d'acqua grande", "Pianificazione pause a schermo spento", "Registrazione abitudini")); // due today

        Routine pianificazioneSettimanale = createRoutine(demoUser, "Review Settimanale & Obiettivi", "Analisi progressi settimana e definizione priorità",
                "Benessere & Abitudini", 15, RecurrenceType.WEEKLY_DAYS, null, "SUNDAY", now.minusDays(7),
                List.of("Revisione time tracking della settimana", "Definizione top 3 priorità per la prossima", "Allineamento to-do list"));

        // 4. Studio & Crescita Professionale
        Routine letturaTech = createRoutine(demoUser, "Lettura Tech News & Newsletter", "Aggiornamento su novità tecnologiche e best practices",
                "Studio & Crescita", 15, RecurrenceType.DAYS_INTERVAL, 3, null, now.minusDays(3),
                List.of("Selezione 2-3 articoli salvati", "Presa note e concetti chiave", "Archiviazione letture completate")); // due today

        createRoutine(demoUser, "Review Note Architetturali", "Ripasso pattern software design e architetture cloud",
                "Studio & Crescita", 12, RecurrenceType.DAYS_INTERVAL, 4, null, now.minusDays(2),
                List.of("Consultazione schede pattern GoF & Cloud", "Review casi studio architetturali", "Aggiunta nuove note tecniche")); // upcoming

        Routine syncRepo = createRoutine(demoUser, "Sync & Audit Repo Personali", "Controllo git status, dipendenze e pulizia branch locali",
                "Studio & Crescita", 10, RecurrenceType.WEEKLY_DAYS, null, "FRIDAY", now.minusDays(5),
                List.of("Verifica branch locali non fusi", "Controllo alert sicurezza dipendenze", "Aggiornamento documentazione README"));

        // Seed rich historical completions for heatmap and streaks across the last 4 weeks
        seedHistoricalCompletion(puliziaWorkstation, now.minusDays(7), 310, true);
        seedHistoricalCompletion(puliziaWorkstation, now.minusDays(14), 290, true);
        seedHistoricalCompletion(puliziaWorkstation, now.minusDays(21), 320, true);

        seedHistoricalCompletion(backupFile, now.minusDays(14), 580, true);
        seedHistoricalCompletion(backupFile, now.minusDays(28), 620, true);

        seedHistoricalCompletion(superficiCucina, now.minusDays(3), 460, true);
        seedHistoricalCompletion(superficiCucina, now.minusDays(6), 490, true);
        seedHistoricalCompletion(superficiCucina, now.minusDays(9), 450, true);
        seedHistoricalCompletion(superficiCucina, now.minusDays(12), 510, true);
        seedHistoricalCompletion(superficiCucina, now.minusDays(15), 470, true);

        seedHistoricalCompletion(aspirapolvere, now.minusDays(7), 890, true);
        seedHistoricalCompletion(aspirapolvere, now.minusDays(14), 920, true);
        seedHistoricalCompletion(aspirapolvere, now.minusDays(21), 870, true);

        seedHistoricalCompletion(scadenzeFrigo, now.minusDays(14), 590, true);
        seedHistoricalCompletion(scadenzeFrigo, now.minusDays(28), 610, true);

        seedHistoricalCompletion(stretching, now.minusDays(1), 600, true);
        seedHistoricalCompletion(stretching, now.minusDays(2), 580, true);
        seedHistoricalCompletion(stretching, now.minusDays(3), 610, true);
        seedHistoricalCompletion(stretching, now.minusDays(4), 590, true);
        seedHistoricalCompletion(stretching, now.minusDays(5), 620, true);

        seedHistoricalCompletion(idratazione, now.minusDays(1), 170, true);
        seedHistoricalCompletion(idratazione, now.minusDays(2), 160, true);
        seedHistoricalCompletion(idratazione, now.minusDays(3), 180, true);

        seedHistoricalCompletion(letturaTech, now.minusDays(3), 880, true);
        seedHistoricalCompletion(letturaTech, now.minusDays(6), 910, true);
        seedHistoricalCompletion(letturaTech, now.minusDays(9), 860, true);

        seedHistoricalCompletion(pianificazioneSettimanale, now.minusDays(7), 890, true);
        seedHistoricalCompletion(pianificazioneSettimanale, now.minusDays(14), 920, true);

        seedHistoricalCompletion(syncRepo, now.minusDays(5), 580, true);
        seedHistoricalCompletion(syncRepo, now.minusDays(12), 610, true);
    }

    private Routine createRoutine(User user, String name, String description, String sessionGroup,
                                  int estimatedMinutes, RecurrenceType recurrenceType, Integer frequencyDays,
                                  String targetDaysOfWeek, LocalDateTime lastCompletedAt, List<String> checklistItems) {
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
        routine.setChecklistItems(checklistItems != null ? checklistItems : List.of());
        routine.setCreatedAt(lastCompletedAt != null ? lastCompletedAt.minusDays(45) : LocalDateTime.now().minusDays(45));
        return routineRepository.save(routine);
    }

    private void seedHistoricalCompletion(Routine routine, LocalDateTime completedAt, int durationSeconds, boolean onTime) {
        RoutineCompletion completion = new RoutineCompletion(routine, completedAt, durationSeconds, onTime);
        routineCompletionRepository.save(completion);
    }

    private void ensureDemoPlanItems(User demoUser) {
        if (planItemRepository.count() > 0) {
            return;
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate tomorrow = today.plusDays(1);

        Activity actSpring = activityRepository.findByNameAndCreatedBy("Spring Boot Microservices", demoUser).orElse(null);

        // Today: Work target + sync
        planItemRepository.save(new com.timetrack.calendar.DailyPlanItem(
                demoUser,
                today,
                "4 ore Sviluppo TimeTrack",
                com.timetrack.calendar.DailyPlanType.WORK_TARGET,
                240,
                actSpring,
                "Implementazione e testing del modulo Calendario"
        ));
        planItemRepository.save(new com.timetrack.calendar.DailyPlanItem(
                demoUser,
                today,
                "Daily Sync con il team",
                com.timetrack.calendar.DailyPlanType.EVENT,
                null,
                null,
                "Allineamento priorità e sprint review"
        ));

        // Tomorrow: Exactly matching user prompt! "domani voglio fare 6 ore di lavoro, pulire il bagno e guardare il torneo"
        planItemRepository.save(new com.timetrack.calendar.DailyPlanItem(
                demoUser,
                tomorrow,
                "6 ore di lavoro",
                com.timetrack.calendar.DailyPlanType.WORK_TARGET,
                360,
                actSpring,
                "Sessione intensiva di sviluppo e ottimizzazione"
        ));
        planItemRepository.save(new com.timetrack.calendar.DailyPlanItem(
                demoUser,
                tomorrow,
                "Guardare il torneo",
                com.timetrack.calendar.DailyPlanType.EVENT,
                null,
                null,
                "Diretta semifinale e finale serale"
        ));
        planItemRepository.save(new com.timetrack.calendar.DailyPlanItem(
                demoUser,
                tomorrow,
                "Spesa settimanale",
                com.timetrack.calendar.DailyPlanType.PERSONAL,
                null,
                null,
                "Rifornimento ingredienti e prodotti per la casa"
        ));
    }
}
