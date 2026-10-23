package com.kadi_aon.mon_salon.notification.dto;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.notification.enums.TypeNotification;

public record NotificationDTOResponse(
        Long id,
        String titre,
        String contenu,
        TypeNotification type,
        String referenceMetier,
        Boolean lu,
        LocalDateTime dateCreation,
        LocalDateTime dateLecture
) {}
