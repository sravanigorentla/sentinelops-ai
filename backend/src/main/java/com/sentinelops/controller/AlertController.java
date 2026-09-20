package com.sentinelops.controller;

import com.sentinelops.dto.AlertRuleDto;
import com.sentinelops.dto.NotificationDto;
import com.sentinelops.service.AlertNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertNotificationService notificationService;

    public AlertController(AlertNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/rules")
    public ResponseEntity<List<AlertRuleDto>> getAllAlertRules() {
        return ResponseEntity.ok(notificationService.getAllAlertRules());
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> getRecentNotifications() {
        return ResponseEntity.ok(notificationService.getRecentNotifications());
    }
}
