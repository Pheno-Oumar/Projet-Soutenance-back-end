package com.kadi_aon.mon_salon.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.notification.entity.NotificationSms;
import com.kadi_aon.mon_salon.notification.enums.StatutNotification;

@Repository
public interface NotificationSmsRepository extends JpaRepository<NotificationSms, Long> {

    List<NotificationSms> findByDestinataireOrderByDateCreationDesc(String destinataire);

    List<NotificationSms> findByStatutOrderByDateCreationAsc(StatutNotification statut);
}
