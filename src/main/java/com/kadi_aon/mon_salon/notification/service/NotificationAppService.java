package com.kadi_aon.mon_salon.notification.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.notification.dto.NotificationCountDTOResponse;
import com.kadi_aon.mon_salon.notification.dto.NotificationDTOResponse;
import com.kadi_aon.mon_salon.notification.entity.Notification;
import com.kadi_aon.mon_salon.notification.enums.TypeNotification;
import com.kadi_aon.mon_salon.notification.mapper.NotificationDTOResponseMapper;
import com.kadi_aon.mon_salon.notification.repository.NotificationRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationAppService {

    private final NotificationRepository notificationRepository;
    private final CompteRepository compteRepository;
    private final NotificationDTOResponseMapper mapper;

    public NotificationAppService(
            NotificationRepository notificationRepository,
            CompteRepository compteRepository,
            NotificationDTOResponseMapper mapper) {
        super();
        this.notificationRepository = notificationRepository;
        this.compteRepository = compteRepository;
        this.mapper = mapper;
    }

    private Compte getCompteByIdentifiant(String identifiant) {
        return compteRepository.findByIdentifiant(identifiant)
                .or(() -> compteRepository.findByEmail(identifiant))
                .or(() -> compteRepository.findByTelephone(identifiant))
                .orElseThrow(() -> new EntityNotFoundException("Compte non trouvé pour l'identifiant : " + identifiant));
    }

    @Transactional
    public NotificationDTOResponse creerNotification(
            Compte compte,
            String titre,
            String contenu,
            TypeNotification type,
            String referenceMetier) {

        Notification notification = Notification.builder()
                .compte(compte)
                .titre(titre.trim())
                .contenu(contenu.trim())
                .type(type)
                .referenceMetier(referenceMetier != null ? referenceMetier.trim() : null)
                .lu(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification In-App créée pour le compte {}: {} [{}]", compte.getId(), titre, type);
        return mapper.apply(saved);
    }

    @Transactional(readOnly = true)
    public Page<NotificationDTOResponse> listerMesNotifications(String identifiant, Boolean lu, Pageable pageable) {
        Compte compte = getCompteByIdentifiant(identifiant);
        Page<Notification> page;
        if (lu != null) {
            page = notificationRepository.findByCompteIdAndLuOrderByDateCreationDesc(compte.getId(), lu, pageable);
        } else {
            page = notificationRepository.findByCompteIdOrderByDateCreationDesc(compte.getId(), pageable);
        }
        return page.map(mapper);
    }

    @Transactional(readOnly = true)
    public NotificationCountDTOResponse compterNotifications(String identifiant) {
        Compte compte = getCompteByIdentifiant(identifiant);
        long total = notificationRepository.countByCompteId(compte.getId());
        long nonLues = notificationRepository.countByCompteIdAndLuFalse(compte.getId());
        return new NotificationCountDTOResponse(total, nonLues);
    }

    @Transactional
    public NotificationDTOResponse marquerCommeLu(String identifiant, Long notificationId) {
        Compte compte = getCompteByIdentifiant(identifiant);
        Notification notification = notificationRepository.findByIdAndCompteId(notificationId, compte.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification introuvable avec l'id : " + notificationId));

        if (!Boolean.TRUE.equals(notification.getLu())) {
            notification.setLu(true);
            notification.setDateLecture(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }
        return mapper.apply(notification);
    }

    @Transactional
    public void toutMarquerCommeLu(String identifiant) {
        Compte compte = getCompteByIdentifiant(identifiant);
        notificationRepository.toutMarquerCommeLu(compte.getId());
        log.info("Toutes les notifications ont été marquées comme lues pour le compte {}", compte.getId());
    }

    @Transactional
    public void supprimerNotification(String identifiant, Long notificationId) {
        Compte compte = getCompteByIdentifiant(identifiant);
        Notification notification = notificationRepository.findByIdAndCompteId(notificationId, compte.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification introuvable avec l'id : " + notificationId));

        notificationRepository.delete(notification);
        log.info("Notification {} supprimée pour le compte {}", notificationId, compte.getId());
    }
}
