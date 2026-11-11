package com.timetrack.calendar;

import com.timetrack.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyPlanItemRepository extends JpaRepository<DailyPlanItem, Long> {

    List<DailyPlanItem> findByUserAndDateBetweenOrderByDateAscIdAsc(User user, LocalDate startDate, LocalDate endDate);

    List<DailyPlanItem> findByUserAndDateOrderByItemTypeAscIdAsc(User user, LocalDate date);

    Optional<DailyPlanItem> findByIdAndUser(Long id, User user);
}
