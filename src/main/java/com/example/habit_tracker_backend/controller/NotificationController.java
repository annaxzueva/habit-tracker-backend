package com.example.habit_tracker_backend.controller;

import com.example.habit_tracker_backend.entity.NotificationSubscription;
import com.example.habit_tracker_backend.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import com.example.habit_tracker_backend.entity.User;
import com.example.habit_tracker_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationService service,
            UserRepository userRepository
    ) {
        this.service = service;
        this.userRepository = userRepository;
    }

    // сохранить подписку
    @PostMapping("/subscribe")
    public void subscribe(@RequestBody SubscribeRequest request) {

        NotificationSubscription sub = new NotificationSubscription();
        sub.setUserId(request.getUserId());
        sub.setEndpoint(request.getSubscription().getEndpoint());
        sub.setP256dh(request.getSubscription().getKeys().getP256dh());
        sub.setAuth(request.getSubscription().getKeys().getAuth());

        service.saveSubscription(sub);

        System.out.println("✅ Подписка сохранена в БД");
    }

    // тестовая отправка
    @PostMapping("/send-test/{userId}")
    public void sendTest(@PathVariable Long userId) throws Exception {

        String payload =
                "{\"title\":\"Напоминание\",\"body\":\"Пора выполнить привычку!\"}";

        service.sendToUser(userId, payload);

        System.out.println("✅ Уведомления отправлены");
    }

    @PostMapping("/settings")
    public ResponseEntity<String> saveSettings(
            @RequestBody NotificationSettingsRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setNotificationsEnabled(request.getEnabled());

        if (request.getTime() != null && !request.getTime().isBlank()) {
            user.setNotificationTime(LocalTime.parse(request.getTime()));
        }

        userRepository.save(user);

        System.out.println("✅ Настройки уведомлений сохранены");

        return ResponseEntity.ok("Настройки сохранены");
    }

    public static class NotificationSettingsRequest {

        private Long userId;
        private Boolean enabled;
        private String time;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    // DTO
    public static class SubscribeRequest {

        private Long userId;
        private PushSubscription subscription;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public PushSubscription getSubscription() {
            return subscription;
        }

        public void setSubscription(PushSubscription subscription) {
            this.subscription = subscription;
        }
    }

    public static class PushSubscription {

        private String endpoint;
        private Keys keys;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public Keys getKeys() {
            return keys;
        }

        public void setKeys(Keys keys) {
            this.keys = keys;
        }
    }

    public static class Keys {

        private String p256dh;
        private String auth;

        public String getP256dh() {
            return p256dh;
        }

        public void setP256dh(String p256dh) {
            this.p256dh = p256dh;
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }
    }
}