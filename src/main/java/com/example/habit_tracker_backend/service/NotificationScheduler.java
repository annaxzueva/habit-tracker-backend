package com.example.habit_tracker_backend.service;

import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.time.LocalTime;
import java.util.List;

@Service
public class NotificationScheduler {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public NotificationScheduler(UserRepository userRepository,
                                 NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }
    @Scheduled(fixedRate = 60000)
    public void checkNotifications() {

        System.out.println("🔔 Проверка уведомлений");

        LocalTime now = LocalTime.now(ZoneId.of("Europe/Moscow"))
                .withSecond(0)
                .withNano(0);

        System.out.println("🕒 Сейчас: " + now);

        List<User> users = userRepository.findAll();

        for (User user : users) {

            System.out.println(
                    "Пользователь " + user.getId()
                            + " время=" + user.getNotificationTime()
                            + " enabled=" + user.getNotificationsEnabled());

            if (Boolean.FALSE.equals(user.getNotificationsEnabled())) {
                continue;
            }

            if (user.getNotificationTime() == null) {
                continue;
            }

            LocalTime userTime = user.getNotificationTime()
                    .withSecond(0)
                    .withNano(0);

            if (userTime.equals(now)) {

                System.out.println("🚀 Отправляем пользователю " + user.getId());

                try {
                    notificationService.sendToUser(user.getId(),
                            "{\"title\":\"Напоминание\",\"body\":\"Пора выполнить привычку\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // каждые 60 секунд
//    @Scheduled(fixedRate = 60000)
//    public void checkNotifications() {
//
//        LocalTime now = LocalTime.now(ZoneId.of("Europe/Moscow"))
//                .withSecond(0)
//                .withNano(0);
//
//        List<User> users = userRepository.findAll();
//
//        for (User user : users) {
//
//            // 1. выключено — пропускаем
//            if (Boolean.FALSE.equals(user.getNotificationsEnabled())) {
//                continue;
//            }
//
//            // 2. нет времени — пропускаем
//            if (user.getNotificationTime() == null) {
//                continue;
//            }
//
//            LocalTime userTime = user.getNotificationTime()
//                    .withSecond(0)
//                    .withNano(0);
//
//            // 3. время совпало → отправляем
//            if (userTime.equals(now)) {
//
//                String payload =
//                        "{\"title\":\"Напоминание\",\"body\":\"Пора выполнить привычку 💪\"}";
//
//                try {
//                    notificationService.sendToUser(user.getId(), payload);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}