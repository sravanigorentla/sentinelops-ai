package com.sentinelops.service;

import com.sentinelops.domain.entity.AlertRule;
import com.sentinelops.domain.entity.NotificationEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.dto.AlertRuleDto;
import com.sentinelops.dto.NotificationDto;
import com.sentinelops.repository.AlertRuleRepository;
import com.sentinelops.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(AlertNotificationService.class);

    private final AlertRuleRepository alertRuleRepository;
    private final NotificationRepository notificationRepository;

    public AlertNotificationService(AlertRuleRepository alertRuleRepository, NotificationRepository notificationRepository) {
        this.alertRuleRepository = alertRuleRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<AlertRuleDto> getAllAlertRules() {
        return alertRuleRepository.findAll().stream().map(AlertRuleDto::new).collect(Collectors.toList());
    }

    public List<NotificationDto> getRecentNotifications() {
        return notificationRepository.findTop50ByOrderBySentAtDesc()
                .stream().map(NotificationDto::new).collect(Collectors.toList());
    }

    public void sendAlertNotifications(ServiceEntity service, String title, String details, IncidentSeverity severity) {
        String message = String.format("[%s ALERT] %s - %s. Details: %s", severity, service.getName(), title, details);

        // Send Slack Webhook Notification
        sendSlackWebhook(service.getOwner(), message);

        // Send Email Notification
        sendEmailNotification(service.getOwner() + "@sentinelops.io", message);
    }

    private void sendSlackWebhook(String recipientChannel, String message) {
        logger.info("[SLACK NOTIFICATION Abstraction] Posting to #alerts-{}: {}", recipientChannel.toLowerCase().replaceAll("\\s+", "-"), message);
        
        NotificationEntity notification = new NotificationEntity();
        notification.setChannel("SLACK");
        notification.setRecipient("#alerts-" + recipientChannel.toLowerCase().replaceAll("\\s+", "-"));
        notification.setStatus("DELIVERED");
        notification.setMessage(message);
        notification.setSentAt(ZonedDateTime.now());
        notificationRepository.save(notification);
    }

    private void sendEmailNotification(String emailRecipient, String message) {
        logger.info("[EMAIL NOTIFICATION Abstraction] Sending SMTP Email to {}: {}", emailRecipient, message);

        NotificationEntity notification = new NotificationEntity();
        notification.setChannel("EMAIL");
        notification.setRecipient(emailRecipient);
        notification.setStatus("DELIVERED");
        notification.setMessage(message);
        notification.setSentAt(ZonedDateTime.now());
        notificationRepository.save(notification);
    }
}
