package com.kadi_aon.mon_salon.notification.entity;

import java.time.LocalDateTime;

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
@Table(name = "notifications_email")
@Getter
@Setter
public class NotificationEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String destinataire;

    @Column(nullable = false, length = 200)
    private String sujet;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutNotification statut = StatutNotification.EN_ATTENTE;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(name = "message_erreur", columnDefinition = "TEXT")
    private String messageErreur;

    public NotificationEmail() {
        super();
    }

    public NotificationEmail(Long id, String destinataire, String sujet, String contenu,
                             StatutNotification statut, LocalDateTime dateCreation,
                             LocalDateTime dateEnvoi, String messageErreur) {
        super();
        this.id = id;
        this.destinataire = destinataire;
        this.sujet = sujet;
        this.contenu = contenu;
        this.statut = statut != null ? statut : StatutNotification.EN_ATTENTE;
        this.dateCreation = dateCreation;
        this.dateEnvoi = dateEnvoi;
        this.messageErreur = messageErreur;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String destinataire;
        private String sujet;
        private String contenu;
        private StatutNotification statut = StatutNotification.EN_ATTENTE;
        private LocalDateTime dateCreation;
        private LocalDateTime dateEnvoi;
        private String messageErreur;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder destinataire(String destinataire) {
            this.destinataire = destinataire;
            return this;
        }

        public Builder sujet(String sujet) {
            this.sujet = sujet;
            return this;
        }

        public Builder contenu(String contenu) {
            this.contenu = contenu;
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

        public Builder messageErreur(String messageErreur) {
            this.messageErreur = messageErreur;
            return this;
        }

        public NotificationEmail build() {
            return new NotificationEmail(id, destinataire, sujet, contenu, statut, dateCreation, dateEnvoi, messageErreur);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutNotification.EN_ATTENTE;
        }
    }
}
