package com.kadi_aon.mon_salon.notification.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.notification.enums.TypeNotification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notifications_app")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeNotification type;

    @Column(name = "reference_metier", length = 100)
    private String referenceMetier;

    @Column(nullable = false)
    private Boolean lu = false;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    public Notification() {
        super();
    }

    public Notification(Long id, Compte compte, String titre, String contenu, TypeNotification type,
                        String referenceMetier, Boolean lu, LocalDateTime dateCreation, LocalDateTime dateLecture) {
        super();
        this.id = id;
        this.compte = compte;
        this.titre = titre;
        this.contenu = contenu;
        this.type = type;
        this.referenceMetier = referenceMetier;
        this.lu = lu != null ? lu : false;
        this.dateCreation = dateCreation;
        this.dateLecture = dateLecture;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Compte compte;
        private String titre;
        private String contenu;
        private TypeNotification type;
        private String referenceMetier;
        private Boolean lu = false;
        private LocalDateTime dateCreation;
        private LocalDateTime dateLecture;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder compte(Compte compte) {
            this.compte = compte;
            return this;
        }

        public Builder titre(String titre) {
            this.titre = titre;
            return this;
        }

        public Builder contenu(String contenu) {
            this.contenu = contenu;
            return this;
        }

        public Builder type(TypeNotification type) {
            this.type = type;
            return this;
        }

        public Builder referenceMetier(String referenceMetier) {
            this.referenceMetier = referenceMetier;
            return this;
        }

        public Builder lu(Boolean lu) {
            this.lu = lu;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public Builder dateLecture(LocalDateTime dateLecture) {
            this.dateLecture = dateLecture;
            return this;
        }

        public Notification build() {
            return new Notification(id, compte, titre, contenu, type, referenceMetier, lu, dateCreation, dateLecture);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.lu == null) {
            this.lu = false;
        }
    }
}
