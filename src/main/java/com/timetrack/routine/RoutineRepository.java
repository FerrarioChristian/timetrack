package com.timetrack.routine;

import com.timetrack.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for managing Routine entities.
 */
@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByUserOrderBySessionGroupAscNameAsc(User user);

    List<Routine> findByUserAndSessionGroupOrderByNameAsc(User user, String sessionGroup);

    Optional<Routine> findByIdAndUser(Long id, User user);

    @Query("SELECT DISTINCT r.sessionGroup FROM Routine r WHERE r.user = :user ORDER BY r.sessionGroup ASC")
    List<String> findDistinctSessionGroupsByUser(@Param("user") User user);

    long countByUser(User user);
}
