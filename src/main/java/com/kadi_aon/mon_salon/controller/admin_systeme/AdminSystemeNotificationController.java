package com.kadi_aon.mon_salon.controller.admin_systeme;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kadi_aon.mon_salon.common.dto.response.APIResponse;
import com.kadi_aon.mon_salon.notification.entity.NotificationSms;
import com.kadi_aon.mon_salon.notification.enums.CanalMessage;
import com.kadi_aon.mon_salon.notification.service.NotificationSmsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin-systeme/notifications")
@PreAuthorize("hasRole('ADMIN_SYSTEME')")
@Tag(name = "Admin Système - Tests Notifications SMS & WhatsApp", description = "Endpoints de test pour valider l'envoi de messages SMS et WhatsApp via Twilio et Scheduler")
public class AdminSystemeNotificationController {

    private final NotificationSmsService notificationSmsService;

    public AdminSystemeNotificationController(NotificationSmsService notificationSmsService) {
        super();
        this.notificationSmsService = notificationSmsService;
    }

    @PostMapping("/test-sms")
    @Operation(summary = "Tester l'envoi d'un SMS vers un numéro de téléphone")
    public ResponseEntity<APIResponse<NotificationSms>> testerEnvoiSms(
            @RequestParam String telephone,
            @RequestParam(defaultValue = "HairStyle : Ceci est un SMS de test envoyé depuis la plateforme HairStyle !") String message) {

        NotificationSms notif = notificationSmsService.queueSms(telephone, message);
        return ResponseEntity.ok(new APIResponse<>(true, "SMS mis en file d'attente avec succès", notif));
    }

    @PostMapping("/test-whatsapp")
    @Operation(summary = "Tester l'envoi d'un message WhatsApp vers un numéro de téléphone")
    public ResponseEntity<APIResponse<NotificationSms>> testerEnvoiWhatsApp(
            @RequestParam String telephone,
            @RequestParam(defaultValue = "💈 *HairStyle* : Ceci est un message WhatsApp de test envoyé avec succès !") String message) {

        NotificationSms notif = notificationSmsService.queueWhatsApp(telephone, message);
        return ResponseEntity.ok(new APIResponse<>(true, "Message WhatsApp mis en file d'attente avec succès", notif));
    }

    @PostMapping("/test-double")
    @Operation(summary = "Tester l'envoi simultané par SMS et WhatsApp")
    public ResponseEntity<APIResponse<String>> testerEnvoiDouble(
            @RequestParam String telephone,
            @RequestParam(defaultValue = "💈 *HairStyle* : Rappel automatique multi-canal (SMS + WhatsApp) !") String message) {

        notificationSmsService.queueDoubleNotification(telephone, message);
        return ResponseEntity.ok(new APIResponse<>(true, "Notifications SMS et WhatsApp mises en file d'attente avec succès", telephone));
    }
}
