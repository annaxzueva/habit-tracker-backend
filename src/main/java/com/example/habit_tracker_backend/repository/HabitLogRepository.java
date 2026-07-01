package com.example.habit_tracker_backend.repository;

import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    List<HabitLog> findByHabit(Habit habit);

    Optional<HabitLog> findByHabitAndCompletedAt(Habit habit, LocalDate completedAt);
}