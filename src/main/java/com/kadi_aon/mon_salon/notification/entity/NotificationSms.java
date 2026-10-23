package com.kadi_aon.mon_salon.notification.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.notification.enums.CanalMessage;
import com.kadi_aon.mon_salon.notification.enums.StatutNotification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notifications_sms")
@Getter
@Setter
public class NotificationSms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String destinataire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalMessage canal = CanalMessage.SMS;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutNotification statut = StatutNotification.EN_ATTENTE;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(name = "sid_message", length = 100)
    private String sidMessage;

    @Column(name = "message_erreur", columnDefinition = "TEXT")
    private String messageErreur;

    public NotificationSms() {
        super();
    }

    public NotificationSms(Long id, String destinataire, CanalMessage canal, String message,
                           StatutNotification statut, LocalDateTime dateCreation,
                           LocalDateTime dateEnvoi, String sidMessage, String messageErreur) {
        super();
        this.id = id;
        this.destinataire = destinataire;
        this.canal = canal != null ? canal : CanalMessage.SMS;
        this.message = message;
        this.statut = statut != null ? statut : StatutNotification.EN_ATTENTE;
        this.dateCreation = dateCreation;
        this.dateEnvoi = dateEnvoi;
        this.sidMessage = sidMessage;
        this.messageErreur = messageErreur;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String destinataire;
        private CanalMessage canal = CanalMessage.SMS;
        private String message;
        private StatutNotification statut = StatutNotification.EN_ATTENTE;
        private LocalDateTime dateCreation;
        private LocalDateTime dateEnvoi;
        private String sidMessage;
        private String messageErreur;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder destinataire(String destinataire) {
            this.destinataire = destinataire;
            return this;
        }

        public Builder canal(CanalMessage canal) {
            this.canal = canal;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder statut(StatutNotification statut) {
            this.statut = statut;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public Builder dateEnvoi(LocalDateTime dateEnvoi) {
            this.dateEnvoi = dateEnvoi;
            return this;
        }

        public Builder sidMessage(String sidMessage) {
            this.sidMessage = sidMessage;
            return this;
        }

        public Builder messageErreur(String messageErreur) {
            this.messageErreur = messageErreur;
            return this;
        }

        public NotificationSms build() {
            return new NotificationSms(id, destinataire, canal, message, statut, dateCreation, dateEnvoi, sidMessage, messageErreur);
        }
    }

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutNotification.EN_ATTENTE;
        }
        if (canal == null) {
            canal = CanalMessage.SMS;
        }
    }
}
