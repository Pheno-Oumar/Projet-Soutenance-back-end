package com.kadi_aon.mon_salon.avis.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.prestation.entity.LignePrestation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "avis_prestation", uniqueConstraints = {
        @UniqueConstraint(name = "uk_avis_prestation_compte_ligne", columnNames = {"client_id", "ligne_prestation_id"})
})
@Getter
@Setter
public class AvisPrestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer note;

    @Column(length = 1000)
    private String commentaire;

    @Column(nullable = false)
    private boolean statut = false;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Compte client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ligne_prestation_id", nullable = false)
    private LignePrestation lignePrestation;

    public AvisPrestation() {
        super();
    }

    public AvisPrestation(Long id, Integer note, String commentaire, boolean statut,
                          LocalDateTime dateCreation, LocalDateTime dateModification,
                          Compte client, LignePrestation lignePrestation) {
        super();
        this.id = id;
        this.note = note;
        this.commentaire = commentaire;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.client = client;
        this.lignePrestation = lignePrestation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Integer note;
        private String commentaire;
        private boolean statut = false;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;
        private Compte client;
        private LignePrestation lignePrestation;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder note(Integer note) {
            this.note = note;
            return this;
        }

        public Builder commentaire(String commentaire) {
            this.commentaire = commentaire;
            return this;
        }

        public Builder statut(boolean statut) {
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

        public Builder client(Compte client) {
            this.client = client;
            return this;
        }

        public Builder lignePrestation(LignePrestation lignePrestation) {
            this.lignePrestation = lignePrestation;
            return this;
        }

        public AvisPrestation build() {
            return new AvisPrestation(id, note, commentaire, statut, dateCreation, dateModification, client, lignePrestation);
        }
    }
}
