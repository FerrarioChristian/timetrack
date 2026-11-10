package com.timetrack.routine;

import com.timetrack.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA Repository for managing RoutineCompletion entities.
 */
@Repository
public interface RoutineCompletionRepository extends JpaRepository<RoutineCompletion, Long> {

    List<RoutineCompletion> findByRoutine_UserAndCompletedAtAfterOrderByCompletedAtDesc(User user, LocalDateTime after);

    List<RoutineCompletion> findByRoutineOrderByCompletedAtDesc(Routine routine);

    long countByRoutine_User(User user);

    long countByRoutine_UserAndCompletedAtAfter(User user, LocalDateTime after);

    @Query("SELECT COUNT(c) FROM RoutineCompletion c WHERE c.routine.user = :user AND c.onTime = true")
    long countOnTimeByUser(@Param("user") User user);

    @Query("SELECT AVG(c.actualDurationSeconds) FROM RoutineCompletion c WHERE c.routine.user = :user AND c.actualDurationSeconds IS NOT NULL")
    Double findAverageDurationSecondsByUser(@Param("user") User user);
}
