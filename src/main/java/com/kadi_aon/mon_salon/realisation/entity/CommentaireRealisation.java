package com.kadi_aon.mon_salon.realisation.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.realisation.enums.StatutCommentaire;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;

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
@Table(name = "commentaires_realisation")
@Getter
@Setter
public class CommentaireRealisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "realisation_id", nullable = false)
    private Realisation realisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affectation_salon_id", nullable = false)
    private AffectationSalon affectationSalon;

    @Column(nullable = false, length = 1000)
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCommentaire statut = StatutCommentaire.ACTIF;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public CommentaireRealisation() {
        super();
        this.statut = StatutCommentaire.ACTIF;
    }

    public CommentaireRealisation(
            Long id,
            Realisation realisation,
            AffectationSalon affectationSalon,
            String contenu,
            StatutCommentaire statut,
            LocalDateTime dateCreation,
            LocalDateTime dateModification) {
        super();
        this.id = id;
        this.realisation = realisation;
        this.affectationSalon = affectationSalon;
        this.contenu = contenu;
        this.statut = (statut != null) ? statut : StatutCommentaire.ACTIF;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Realisation realisation;
        private AffectationSalon affectationSalon;
        private String contenu;
        private StatutCommentaire statut = StatutCommentaire.ACTIF;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder realisation(Realisation realisation) {
            this.realisation = realisation;
            return this;
        }

        public Builder affectationSalon(AffectationSalon affectationSalon) {
            this.affectationSalon = affectationSalon;
            return this;
        }

        public Builder contenu(String contenu) {
            this.contenu = contenu;
            return this;
        }

        public Builder statut(StatutCommentaire statut) {
            this.statut = statut;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public Builder dateModification(LocalDateTime dateModification) {
            this.dateModification = dateModification;
            return this;
        }

        public CommentaireRealisation build() {
            return new CommentaireRealisation(id, realisation, affectationSalon, contenu, statut, dateCreation, dateModification);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutCommentaire.ACTIF;
        }
    }
}
