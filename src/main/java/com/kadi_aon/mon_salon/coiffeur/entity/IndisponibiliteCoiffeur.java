package com.kadi_aon.mon_salon.coiffeur.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.coiffeur.enums.MotifIndisponibilite;
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
@Table(name = "indisponibilites_coiffeur")
@Getter
@Setter
public class IndisponibiliteCoiffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coiffeur_affectation_id", nullable = false)
    private AffectationSalon coiffeur;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MotifIndisponibilite motif;

    @Column(length = 255)
    private String commentaire;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    public IndisponibiliteCoiffeur() {
        super();
    }

    public IndisponibiliteCoiffeur(
            Long id,
            AffectationSalon coiffeur,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            MotifIndisponibilite motif,
            String commentaire,
            LocalDateTime dateCreation) {
        super();
        this.id = id;
        this.coiffeur = coiffeur;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.motif = motif;
        this.commentaire = commentaire;
        this.dateCreation = dateCreation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private AffectationSalon coiffeur;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private MotifIndisponibilite motif;
        private String commentaire;
        private LocalDateTime dateCreation;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder coiffeur(AffectationSalon coiffeur) {
            this.coiffeur = coiffeur;
            return this;
        }

        public Builder dateDebut(LocalDateTime dateDebut) {
            this.dateDebut = dateDebut;
            return this;
        }

        public Builder dateFin(LocalDateTime dateFin) {
            this.dateFin = dateFin;
            return this;
        }

        public Builder motif(MotifIndisponibilite motif) {
            this.motif = motif;
            return this;
        }

        public Builder commentaire(String commentaire) {
            this.commentaire = commentaire;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public IndisponibiliteCoiffeur build() {
            return new IndisponibiliteCoiffeur(id, coiffeur, dateDebut, dateFin, motif, commentaire, dateCreation);
        }
    }

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}
