package com.example.habit_tracker_backend.controller;

import com.example.habit_tracker_backend.entity.Habit;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.HabitRepository;
import com.example.habit_tracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    // ПРОВЕРКА: просто возвращаем всех пользователей
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ПРОВЕРКА: возвращаем привычки пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Habit>> getHabitsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return ResponseEntity.ok(habitRepository.findByUser(user));
    }
}