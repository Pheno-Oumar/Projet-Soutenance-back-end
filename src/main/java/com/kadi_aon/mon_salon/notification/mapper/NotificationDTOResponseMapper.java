package com.kadi_aon.mon_salon.notification.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.kadi_aon.mon_salon.notification.dto.NotificationDTOResponse;
import com.kadi_aon.mon_salon.notification.entity.Notification;

@Component
public class NotificationDTOResponseMapper implements Function<Notification, NotificationDTOResponse> {

    @Override
    public NotificationDTOResponse apply(Notification notification) {
        if (notification == null) {
            return null;
        }

        return new NotificationDTOResponse(
                notification.getId(),
                notification.getTitre(),
                notification.getContenu(),
                notification.getType(),
                notification.getReferenceMetier(),
                notification.getLu(),
                notification.getDateCreation(),
                notification.getDateLecture()
        );
    }
}
