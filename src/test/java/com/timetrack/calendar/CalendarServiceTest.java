package com.timetrack.calendar;

import com.timetrack.activity.Activity;
import com.timetrack.activity.ActivityRepository;
import com.timetrack.auth.User;
import com.timetrack.auth.UserRepository;
import com.timetrack.category.Category;
import com.timetrack.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CalendarServiceTest {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private DailyPlanItemRepository planItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ActivityRepository activityRepository;

    private User testUser;
    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("calendarTestUser");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        Category cat = new Category("Sviluppo");
        cat.setCreatedBy(testUser);
        cat = categoryRepository.save(cat);

        testActivity = new Activity();
        testActivity.setName("Coding");
        testActivity.setCategory(cat);
        testActivity.setCreatedBy(testUser);
        testActivity = activityRepository.save(testActivity);
    }

    @Test
    void testCreatePlanItem_WorkTarget() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        DailyPlanItemRequestDto request = new DailyPlanItemRequestDto(
                tomorrow,
                "6 ore di lavoro",
                DailyPlanType.WORK_TARGET,
                6.0,
                null,
                testActivity.getId(),
                "Sessione intensiva su calendario"
        );

        DailyPlanItem item = calendarService.createPlanItem(request, testUser);

        assertNotNull(item.getId());
        assertEquals("6 ore di lavoro", item.getTitle());
        assertEquals(DailyPlanType.WORK_TARGET, item.getItemType());
        assertEquals(360, item.getPlannedMinutes());
        assertNotNull(item.getActivity());
        assertEquals("Coding", item.getActivity().getName());
        assertFalse(item.isCompleted());
    }

    @Test
    void testCreatePlanItem_Event() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        DailyPlanItemRequestDto request = new DailyPlanItemRequestDto(
                tomorrow,
                "Guardare il torneo",
                DailyPlanType.EVENT,
                null,
                null,
                null,
                "Finale dalle ore 20:30"
        );

        DailyPlanItem item = calendarService.createPlanItem(request, testUser);

        assertNotNull(item.getId());
        assertEquals("Guardare il torneo", item.getTitle());
        assertEquals(DailyPlanType.EVENT, item.getItemType());
        assertNull(item.getPlannedMinutes());
        assertFalse(item.isCompleted());
    }

    @Test
    void testTogglePlanItem() {
        LocalDate today = LocalDate.now();
        DailyPlanItem item = new DailyPlanItem(testUser, today, "Fare la spesa", DailyPlanType.PERSONAL, null, null, null);
        item = planItemRepository.save(item);

        assertFalse(item.isCompleted());

        DailyPlanItem toggled = calendarService.togglePlanItem(item.getId(), testUser);
        assertTrue(toggled.isCompleted());

        DailyPlanItem toggledBack = calendarService.togglePlanItem(item.getId(), testUser);
        assertFalse(toggledBack.isCompleted());
    }

    @Test
    void testDeletePlanItem() {
        LocalDate today = LocalDate.now();
        DailyPlanItem item = new DailyPlanItem(testUser, today, "Da cancellare", DailyPlanType.PERSONAL, null, null, null);
        item = planItemRepository.save(item);

        Long id = item.getId();
        calendarService.deletePlanItem(id, testUser);

        assertTrue(planItemRepository.findById(id).isEmpty());
    }

    @Test
    void testGetCalendarViewAndUnifiedDayAgenda() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Add 1 work target: 6h
        calendarService.createPlanItem(new DailyPlanItemRequestDto(
                tomorrow,
                "6 ore di lavoro",
                DailyPlanType.WORK_TARGET,
                6.0,
                null,
                testActivity.getId(),
                "Coding"
        ), testUser);

        // Add 1 event: Guardare il torneo
        calendarService.createPlanItem(new DailyPlanItemRequestDto(
                tomorrow,
                "Guardare il torneo",
                DailyPlanType.EVENT,
                null,
                null,
                null,
                "Finale"
        ), testUser);

        CalendarViewDto view = calendarService.getCalendarView(tomorrow.getYear(), tomorrow.getMonthValue(), testUser);

        assertNotNull(view);
        assertFalse(view.days().isEmpty());

        CalendarDayDto tomorrowDay = view.daysByDate().get(tomorrow.toString());
        assertNotNull(tomorrowDay);
        assertEquals(360, tomorrowDay.totalTargetMinutes());
        assertEquals("6h", tomorrowDay.formattedTargetTime());
        assertEquals(1, tomorrowDay.workTargets().size());
        assertEquals("6 ore di lavoro", tomorrowDay.workTargets().get(0).title());
        assertEquals(1, tomorrowDay.events().size());
        assertEquals("Guardare il torneo", tomorrowDay.events().get(0).title());
    }
}
