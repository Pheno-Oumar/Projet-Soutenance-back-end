package com.kadi_aon.mon_salon.notification.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kadi_aon.mon_salon.common.dto.response.APIResponse;
import com.kadi_aon.mon_salon.notification.dto.NotificationCountDTOResponse;
import com.kadi_aon.mon_salon.notification.dto.NotificationDTOResponse;
import com.kadi_aon.mon_salon.notification.service.NotificationAppService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/client/notifications")
@PreAuthorize("@salonSecurity.isClient()")
@Tag(name = "Notifications Client (In-App)", description = "Consultation et gestion des notifications reçues dans l'application")
public class NotificationClientController {

    private final NotificationAppService notificationAppService;

    public NotificationClientController(NotificationAppService notificationAppService) {
        super();
        this.notificationAppService = notificationAppService;
    }

    @GetMapping
    @Operation(summary = "Lister les notifications du compte connecté avec pagination et filtre optionnel lu/non-lu")
    public ResponseEntity<APIResponse<Page<NotificationDTOResponse>>> listerMesNotifications(
            @RequestParam(required = false) Boolean lu,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDTOResponse> pageResult = notificationAppService.listerMesNotifications(principal.getName(), lu, pageable);
        return ResponseEntity.ok(new APIResponse<>(true, "Notifications récupérées avec succès", pageResult));
    }

    @GetMapping("/non-lues/count")
    @Operation(summary = "Compter le nombre de notifications non lues (pour le badge de la cloche)")
    public ResponseEntity<APIResponse<NotificationCountDTOResponse>> compterNotifications(Principal principal) {
        NotificationCountDTOResponse count = notificationAppService.compterNotifications(principal.getName());
        return ResponseEntity.ok(new APIResponse<>(true, "Compteur de notifications récupéré", count));
    }

    @PatchMapping("/{id}/lire")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<APIResponse<NotificationDTOResponse>> marquerCommeLu(
            @PathVariable Long id,
            Principal principal) {

        NotificationDTOResponse response = notificationAppService.marquerCommeLu(principal.getName(), id);
        return ResponseEntity.ok(new APIResponse<>(true, "Notification marquée comme lue", response));
    }

    @PatchMapping("/tout-lire")
    @Operation(summary = "Marquer toutes les notifications comme lues")
    public ResponseEntity<APIResponse<Void>> toutMarquerCommeLu(Principal principal) {
        notificationAppService.toutMarquerCommeLu(principal.getName());
        return ResponseEntity.ok(new APIResponse<>(true, "Toutes les notifications ont été marquées comme lues", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une notification de son historique")
    public ResponseEntity<APIResponse<Void>> supprimerNotification(
            @PathVariable Long id,
            Principal principal) {

        notificationAppService.supprimerNotification(principal.getName(), id);
        return ResponseEntity.ok(new APIResponse<>(true, "Notification supprimée avec succès", null));
    }
}
