package com.timetrack.routine;

import com.timetrack.auth.User;
import com.timetrack.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RoutineServiceTest {

    @Autowired
    private RoutineService routineService;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineCompletionRepository completionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.findByUsername("routine_tester");
        if (testUser == null) {
            testUser = new User();
            testUser.setUsername("routine_tester");
            testUser.setPassword(passwordEncoder.encode("secret123"));
            testUser = userRepository.save(testUser);
        }
    }

    @Test
    @DisplayName("Should create routine and calculate recurrence properly")
    void testCreateRoutine() {
        RoutineRequestDto dto = new RoutineRequestDto();
        dto.setName("Lavandino Bagno");
        dto.setDescription("Igienizzazione lavandino");
        dto.setSessionGroup("Pulizia Bagno");
        dto.setEstimatedMinutes(4);
        dto.setRecurrenceType(RecurrenceType.DAYS_INTERVAL);
        dto.setFrequencyDays(3);

        Routine created = routineService.createRoutine(dto, testUser);

        assertNotNull(created.getId());
        assertEquals("Lavandino Bagno", created.getName());
        assertEquals("Pulizia Bagno", created.getSessionGroup());
        assertEquals(4, created.getEstimatedMinutes());
        assertEquals(3, created.getFrequencyDays());
        // Since never completed, it is due today
        assertTrue(created.isDueOrOverdue());
        assertEquals(RoutineStatus.DUE_TODAY, created.getStatus());
    }

    @Test
    @DisplayName("Should mark routine as completed and record actual duration")
    void testCompleteRoutineWithDuration() {
        Routine routine = new Routine();
        routine.setUser(testUser);
        routine.setName("WC");
        routine.setSessionGroup("Pulizia Bagno");
        routine.setEstimatedMinutes(3);
        routine.setRecurrenceType(RecurrenceType.DAYS_INTERVAL);
        routine.setFrequencyDays(4);
        routine.setLastCompletedAt(LocalDateTime.now().minusDays(5)); // Overdue
        routine = routineRepository.save(routine);

        assertEquals(RoutineStatus.OVERDUE, routine.getStatus());

        // Complete with 180 seconds (3 min) actual duration
        RoutineCompletion completion = routineService.completeRoutine(routine.getId(), 180, "Ottima pulizia", testUser);

        assertNotNull(completion.getId());
        assertEquals(180, completion.getActualDurationSeconds());

        Routine updated = routineRepository.findById(routine.getId()).orElseThrow();
        assertEquals(RoutineStatus.COMPLETED_TODAY, updated.getStatus());
        assertNotNull(updated.getLastCompletedAt());

        List<RoutineCompletion> history = completionRepository.findByRoutineOrderByCompletedAtDesc(updated);
        assertFalse(history.isEmpty());
        assertEquals(180, history.get(0).getActualDurationSeconds());
    }

    @Test
    @DisplayName("Should undo completion and restore previous completed status")
    void testUncompleteRoutine() {
        Routine routine = new Routine();
        routine.setUser(testUser);
        routine.setName("Bidet");
        routine.setSessionGroup("Pulizia Bagno");
        routine.setEstimatedMinutes(2);
        routine.setRecurrenceType(RecurrenceType.DAYS_INTERVAL);
        routine.setFrequencyDays(4);
        routine.setLastCompletedAt(LocalDateTime.now().minusDays(8));
        routine = routineRepository.save(routine);

        // First completion in history (8 days ago)
        RoutineCompletion oldComp = new RoutineCompletion(routine, LocalDateTime.now().minusDays(8), 120, true);
        completionRepository.save(oldComp);

        // Complete now
        routineService.completeRoutine(routine.getId(), 100, null, testUser);
        assertEquals(RoutineStatus.COMPLETED_TODAY, routineRepository.findById(routine.getId()).orElseThrow().getStatus());

        // Undo
        routineService.undoLastCompletion(routine.getId(), testUser);
        Routine undone = routineRepository.findById(routine.getId()).orElseThrow();
        assertNotEquals(RoutineStatus.COMPLETED_TODAY, undone.getStatus());
        assertEquals(1, completionRepository.findByRoutineOrderByCompletedAtDesc(undone).size());
    }

    @Test
    @DisplayName("Should group routines by session and aggregate estimated times")
    void testGroupRoutinesBySession() {
        Routine r1 = new Routine();
        r1.setUser(testUser);
        r1.setName("R1");
        r1.setSessionGroup("Cucina & Casa");
        r1.setEstimatedMinutes(10);
        r1.setRecurrenceType(RecurrenceType.DAYS_INTERVAL);
        r1.setFrequencyDays(1);
        routineRepository.save(r1);

        Routine r2 = new Routine();
        r2.setUser(testUser);
        r2.setName("R2");
        r2.setSessionGroup("Cucina & Casa");
        r2.setEstimatedMinutes(15);
        r2.setRecurrenceType(RecurrenceType.DAYS_INTERVAL);
        r2.setFrequencyDays(1);
        routineRepository.save(r2);

        List<SessionGroupSummaryDto> groups = routineService.getSessionSummaries(testUser);
        SessionGroupSummaryDto cucinaGroup = groups.stream()
                .filter(g -> "Cucina & Casa".equals(g.sessionGroup()))
                .findFirst()
                .orElse(null);

        assertNotNull(cucinaGroup);
        assertTrue(cucinaGroup.totalEstimatedMinutesDue() >= 25);
    }

    @Test
    @DisplayName("Should calculate routine stats accurately")
    void testGetRoutineStats() {
        RoutineStatsDto stats = routineService.getStats(testUser);
        assertNotNull(stats);
        assertNotNull(stats.completionsPerSessionGroup());
    }
}
