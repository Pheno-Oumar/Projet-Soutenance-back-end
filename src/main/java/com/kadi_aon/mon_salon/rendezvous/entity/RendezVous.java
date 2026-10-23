package com.kadi_aon.mon_salon.rendezvous.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.rendezvous.enums.StatutRendezVous;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rendez_vous")
@Getter
@Setter
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coiffeur_affectation_id", nullable = false)
    private AffectationSalon coiffeur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_compte_id", nullable = false)
    private Compte client;

    @Column(name = "date_heure_prevue", nullable = false)
    private LocalDateTime dateHeurePrevue;

    @Column(name = "date_heure_fin", nullable = false)
    private LocalDateTime dateHeureFin;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutRendezVous statut = StatutRendezVous.CONFIRME;

    @Column(name = "montant_estime", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantEstime;

    @OneToMany(mappedBy = "rendezVous", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneRendezVous> lignes = new ArrayList<>();

    public RendezVous() {
        super();
    }

    public RendezVous(Long id, Salon salon, AffectationSalon coiffeur, Compte client,
                      LocalDateTime dateHeurePrevue, LocalDateTime dateHeureFin,
                      LocalDateTime dateCreation, LocalDateTime dateModification,
                      String motifAnnulation, LocalDateTime dateAnnulation,
                      StatutRendezVous statut, BigDecimal montantEstime, List<LigneRendezVous> lignes) {
        super();
        this.id = id;
        this.salon = salon;
        this.coiffeur = coiffeur;
        this.client = client;
        this.dateHeurePrevue = dateHeurePrevue;
        this.dateHeureFin = dateHeureFin;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.motifAnnulation = motifAnnulation;
        this.dateAnnulation = dateAnnulation;
        this.statut = statut != null ? statut : StatutRendezVous.CONFIRME;
        this.montantEstime = montantEstime;
        this.lignes = lignes != null ? lignes : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Salon salon;
        private AffectationSalon coiffeur;
        private Compte client;
        private LocalDateTime dateHeurePrevue;
        private LocalDateTime dateHeureFin;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;
        private String motifAnnulation;
        private LocalDateTime dateAnnulation;
        private StatutRendezVous statut = StatutRendezVous.CONFIRME;
        private BigDecimal montantEstime;
        private List<LigneRendezVous> lignes = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder coiffeur(AffectationSalon coiffeur) {
            this.coiffeur = coiffeur;
            return this;
        }

        public Builder client(Compte client) {
            this.client = client;
            return this;
        }

        public Builder dateHeurePrevue(LocalDateTime dateHeurePrevue) {
            this.dateHeurePrevue = dateHeurePrevue;
            return this;
        }

        public Builder dateHeureFin(LocalDateTime dateHeureFin) {
            this.dateHeureFin = dateHeureFin;
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

        public Builder motifAnnulation(String motifAnnulation) {
            this.motifAnnulation = motifAnnulation;
            return this;
        }

        public Builder dateAnnulation(LocalDateTime dateAnnulation) {
            this.dateAnnulation = dateAnnulation;
            return this;
        }

        public Builder statut(StatutRendezVous statut) {
            this.statut = statut;
            return this;
        }

        public Builder montantEstime(BigDecimal montantEstime) {
            this.montantEstime = montantEstime;
            return this;
        }

        public Builder lignes(List<LigneRendezVous> lignes) {
            this.lignes = lignes != null ? lignes : new ArrayList<>();
            return this;
        }

        public RendezVous build() {
            return new RendezVous(id, salon, coiffeur, client, dateHeurePrevue, dateHeureFin,
                    dateCreation, dateModification, motifAnnulation, dateAnnulation,
                    statut, montantEstime, lignes);
        }
    }

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutRendezVous.CONFIRME;
        }
    }

    @PreUpdate
    public void preUpdate() {
        dateModification = LocalDateTime.now();
    }

    public void addLigne(LigneRendezVous ligne) {
        lignes.add(ligne);
        ligne.setRendezVous(this);
    }

    public boolean isTransitionAutorisee(StatutRendezVous nouveauStatut) {
        return switch (this.statut) {
            case CONFIRME -> nouveauStatut == StatutRendezVous.EN_COURS ||
                             nouveauStatut == StatutRendezVous.ANNULE ||
                             nouveauStatut == StatutRendezVous.NO_SHOW;
            case EN_COURS -> nouveauStatut == StatutRendezVous.TERMINE;
            case TERMINE, ANNULE, NO_SHOW -> false;
        };
    }
}
