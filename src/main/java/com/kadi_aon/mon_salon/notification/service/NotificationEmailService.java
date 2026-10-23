package com.kadi_aon.mon_salon.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.notification.entity.NotificationEmail;
import com.kadi_aon.mon_salon.notification.enums.StatutNotification;
import com.kadi_aon.mon_salon.notification.repository.NotificationEmailRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationEmailService {

    private final NotificationEmailRepository notificationEmailRepository;

    public NotificationEmailService(NotificationEmailRepository notificationEmailRepository) {
        this.notificationEmailRepository = notificationEmailRepository;
    }

    @Transactional
    public NotificationEmail queueNouveauProprietaireEmail(String destinataire, String nomSalon, String motDePasseTemporaire) {
        if (destinataire == null || destinataire.isBlank() || !destinataire.contains("@")) {
            log.info("Destinataire email absent ou invalide ('{}') pour le nouveau propriétaire, notification ignorée.", destinataire);
            return null;
        }
        String sujet = "Bienvenue sur HairStyle - Vos accès propriétaire pour " + nomSalon;
        String contenu = String.format(
                "Bonjour,\n\n" +
                "Un compte propriétaire a été créé pour vous sur la plateforme HairStyle pour gérer le salon '%s'.\n\n" +
                "Vos identifiants de connexion :\n" +
                "- Email : %s\n" +
                "- Mot de passe temporaire : %s\n\n" +
                "Pour des raisons de sécurité, nous vous recommandons de modifier ce mot de passe dès votre première connexion.\n\n" +
                "Cordialement,\n" +
                "L'équipe HairStyle.",
                nomSalon, destinataire, motDePasseTemporaire
        );

        NotificationEmail email = NotificationEmail.builder()
                .destinataire(destinataire)
                .sujet(sujet)
                .contenu(contenu)
                .statut(StatutNotification.EN_ATTENTE)
                .build();

        NotificationEmail saved = notificationEmailRepository.save(email);
        log.info("Email de bienvenue enfilé pour le propriétaire {} (id: {})", destinataire, saved.getId());
        return saved;
    }

    @Transactional
    public NotificationEmail queueNotificationEmail(String destinataire, String sujet, String contenu) {
        if (destinataire == null || destinataire.isBlank() || !destinataire.contains("@")) {
            log.info("Destinataire email absent ou invalide ('{}'), notification ignorée.", destinataire);
            return null;
        }
        NotificationEmail email = NotificationEmail.builder()
                .destinataire(destinataire)
                .sujet(sujet)
                .contenu(contenu)
                .statut(StatutNotification.EN_ATTENTE)
                .build();

        NotificationEmail saved = notificationEmailRepository.save(email);
        log.info("Notification email enfilée pour {} - Sujet: '{}' (id: {})", destinataire, sujet, saved.getId());
        return saved;
    }
}
