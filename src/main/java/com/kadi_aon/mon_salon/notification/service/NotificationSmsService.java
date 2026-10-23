package com.kadi_aon.mon_salon.notification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.notification.entity.NotificationSms;
import com.kadi_aon.mon_salon.notification.enums.CanalMessage;
import com.kadi_aon.mon_salon.notification.enums.StatutNotification;
import com.kadi_aon.mon_salon.notification.repository.NotificationSmsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationSmsService {

    private static final Pattern CLEAN_PHONE_PATTERN = Pattern.compile("[^0-9+]");

    private final NotificationSmsRepository notificationSmsRepository;

    public NotificationSmsService(NotificationSmsRepository notificationSmsRepository) {
        super();
        this.notificationSmsRepository = notificationSmsRepository;
    }

    /**
     * Normalise un numéro de téléphone au format international E.164 (+[indicatif][numéro]).
     * - Si le numéro contient déjà l'indicatif international (ex: +33612345678, +22177000000, +15551234567, +22507000000) : il est conservé tel quel.
     * - Si le numéro commence par '00' (ex: 0033612345678) : transformé en +33612345678.
     * - Si le numéro est malien national (8 chiffres, ex: 77123456) : préfixé par l'indicatif du Mali +223 (+22377123456).
     * - Si le numéro est français national (10 chiffres commençant par 0, ex: 0612345678) : transformé en +33612345678.
     * - Si le numéro commence directement par un indicatif pays sans '+' (ex: 33612345678, 22177000000) : préfixé par '+'.
     */
    public String normaliserTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            return null;
        }

        // Nettoie tous les espaces, tirets, parenthèses, points
        String cleaned = CLEAN_PHONE_PATTERN.matcher(telephone).replaceAll("");

        // 1. Remplacement du préfixe international '00' par '+'
        if (cleaned.startsWith("00")) {
            cleaned = "+" + cleaned.substring(2);
        }

        // 2. Si le numéro commence déjà par '+', il contient déjà son indicatif pays
        if (cleaned.startsWith("+")) {
            return cleaned.length() >= 8 ? cleaned : null;
        }

        // 3. Format national malien standard (8 chiffres, ex: 77 12 34 56, 66..., 20...)
        if (cleaned.length() == 8) {
            return "+223" + cleaned;
        }

        // 4. Format national français standard (10 chiffres commençant par 0, ex: 06 12 34 56 78)
        if (cleaned.length() == 10 && cleaned.startsWith("0")) {
            return "+33" + cleaned.substring(1);
        }

        // 5. Numéro international saisi directement avec l'indicatif pays sans le '+' (ex: 33612345678, 22177000000, 22507000000)
        if (cleaned.length() >= 9) {
            return "+" + cleaned;
        }

        return "+" + cleaned;
    }

    /**
     * Enfile une notification sur un canal unique (SMS ou WhatsApp).
     */
    @Transactional
    public NotificationSms queueCanalUnique(String telephone, String message, CanalMessage canal) {
        String numNormalise = normaliserTelephone(telephone);
        if (numNormalise == null) {
            log.warn("[NOTIF DIRECTE] Numéro de téléphone invalide ('{}'), notification ignorée.", telephone);
            return null;
        }
        if (message == null || message.isBlank()) {
            log.warn("[NOTIF DIRECTE] Message vide vers {}, notification ignorée.", numNormalise);
            return null;
        }

        CanalMessage canalFinal = (canal != null) ? canal : CanalMessage.SMS;

        NotificationSms notif = NotificationSms.builder()
                .destinataire(numNormalise)
                .canal(canalFinal)
                .message(message)
                .statut(StatutNotification.EN_ATTENTE)
                .build();

        NotificationSms saved = notificationSmsRepository.save(notif);
        log.info("[QUEUE NOTIF] Notification #{} [{}] mise en file pour {}",
                saved.getId(), canalFinal, numNormalise);
        return saved;
    }

    /**
     * RÈGLE GÉNÉRALE : Envoie TOUJOURS la notification simultanément par SMS ET par WhatsApp.
     */
    @Transactional
    public List<NotificationSms> queueNotification(String telephone, String message) {
        List<NotificationSms> resultats = new ArrayList<>(2);
        NotificationSms sms = queueCanalUnique(telephone, message, CanalMessage.SMS);
        if (sms != null) resultats.add(sms);

        NotificationSms wa = queueCanalUnique(telephone, message, CanalMessage.WHATSAPP);
        if (wa != null) resultats.add(wa);

        return resultats;
    }

    @Transactional
    public NotificationSms queueSms(String telephone, String message) {
        return queueCanalUnique(telephone, message, CanalMessage.SMS);
    }

    @Transactional
    public NotificationSms queueWhatsApp(String telephone, String message) {
        return queueCanalUnique(telephone, message, CanalMessage.WHATSAPP);
    }

    @Transactional
    public List<NotificationSms> queueDoubleNotification(String telephone, String message) {
        return queueNotification(telephone, message);
    }

    /**
     * Rappel de Rendez-Vous envoyé systématiquement par SMS ET WhatsApp.
     */
    @Transactional
    public List<NotificationSms> queueRappelRendezVous(String telephone, String prenomClient, String nomSalon, String dateHeure) {
        String salonStr = (nomSalon != null && !nomSalon.isBlank()) ? nomSalon : "votre salon";
        String clientStr = (prenomClient != null && !prenomClient.isBlank()) ? prenomClient : "cher client";
        String texte = String.format(
                "HairStyle [%s] : Bonjour %s, rappel de votre RDV prévu le %s chez %s. Pour modifier ou annuler, connectez-vous sur votre application HairStyle.",
                salonStr,
                clientStr,
                dateHeure,
                salonStr
        );
        return queueNotification(telephone, texte);
    }

    /**
     * Notification de Commande prête envoyée systématiquement par SMS ET WhatsApp avec le code de retrait.
     */
    @Transactional
    public List<NotificationSms> queueCommandePrete(String telephone, String numeroCommande, String codeRetrait, String nomSalon) {
        String salonStr = (nomSalon != null && !nomSalon.isBlank()) ? nomSalon : "le salon";
        String texte = String.format(
                "HairStyle [%s] : Votre commande %s est prête ! Présentez votre code de retrait : %s lors de votre passage au salon.",
                salonStr,
                numeroCommande,
                codeRetrait
        );
        return queueNotification(telephone, texte);
    }

    /**
     * Notification de mise à jour de Commande (expédiée / validée) envoyée par SMS ET WhatsApp.
     */
    @Transactional
    public List<NotificationSms> queueStatutCommande(String telephone, String numeroCommande, String nouveauStatut, String nomSalon) {
        String salonStr = (nomSalon != null && !nomSalon.isBlank()) ? nomSalon : "le salon";
        String texte = String.format(
                "HairStyle [%s] : Votre commande %s est désormais : %s.",
                salonStr,
                numeroCommande,
                nouveauStatut
        );
        return queueNotification(telephone, texte);
    }

    /**
     * Alerte de stock bas envoyée systématiquement par SMS ET WhatsApp au gérant / propriétaire.
     */
    @Transactional
    public List<NotificationSms> queueAlerteStockBas(String telephone, String nomProduit, int quantiteRestante, String nomSalon) {
        String salonStr = (nomSalon != null && !nomSalon.isBlank()) ? nomSalon : "votre salon";
        String texte = String.format(
                "[HairStyle - %s] ALERTE STOCK : Le produit '%s' n'a plus que %d unité(s) restante(s). Pensez au réapprovisionnement.",
                salonStr,
                nomProduit,
                quantiteRestante
        );
        return queueNotification(telephone, texte);
    }

    /**
     * Accès nouveaux comptes envoyés systématiquement par SMS ET WhatsApp.
     */
    @Transactional
    public List<NotificationSms> queueBienvenueCompte(String telephone, String nomSalon, String motDePasseTemporaire) {
        String salonStr = (nomSalon != null && !nomSalon.isBlank()) ? nomSalon : "votre salon";
        String texte = String.format(
                "Bienvenue sur HairStyle ! Vos accès pour le salon '%s' ont été générés. Votre mot de passe temporaire : %s. Modifiez-le dès votre première connexion.",
                salonStr,
                motDePasseTemporaire
        );
        return queueNotification(telephone, texte);
    }
}
