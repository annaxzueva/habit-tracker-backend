package com.example.habit_tracker_backend.controller;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Value("${vapid.private.key}")
    private String privateKey;

    private final List<SubscriptionData> subscriptions = new ArrayList<>();

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody SubscribeRequest request) {

        Subscription.Keys keys = new Subscription.Keys(
                request.getSubscription().getKeys().getP256dh(),
                request.getSubscription().getKeys().getAuth()
        );

        Subscription subscription = new Subscription(
                request.getSubscription().getEndpoint(),
                keys
        );

        subscriptions.add(new SubscriptionData(
                request.getUserId(),
                subscription
        ));

        System.out.println("✅ Подписка сохранена");
    }

    @PostMapping("/send-test")
    public void sendTestNotification() throws Exception {

        PushService pushService = new PushService();

        pushService.setPrivateKey(privateKey);

        String payload =
                "{\"title\":\"Напоминание\",\"body\":\"Пора выполнить привычку!\"}";

        for (SubscriptionData data : subscriptions) {
            Notification notification =
                    new Notification(data.subscription, payload);

            pushService.send(notification);
        }

        System.out.println("✅ Все уведомления отправлены");
    }

    // ================= DTO =================

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

    public static class SubscriptionData {

        private final Long userId;
        private final Subscription subscription;

        public SubscriptionData(Long userId, Subscription subscription) {
            this.userId = userId;
            this.subscription = subscription;
        }
    }
}