package com.kadi_aon.mon_salon.prestation.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.facturation.entity.Facture;
import com.kadi_aon.mon_salon.prestation.enums.StatutPrestation;
import com.kadi_aon.mon_salon.rendezvous.entity.RendezVous;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prestations")
@Getter
@Setter
public class Prestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coiffeur_affectation_id")
    private AffectationSalon coiffeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_compte_id")
    private Compte client;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_id", unique = true)
    private RendezVous rendezVous;

    @Column(name = "nom_client", nullable = false, length = 100)
    private String nomClient;

    @Column(name = "prenom_client", nullable = false, length = 100)
    private String prenomClient;

    @Column(name = "telephone_client", length = 30)
    private String telephoneClient;

    @Column(name = "date_heure_debut", nullable = false)
    private LocalDateTime dateHeureDebut;

    @Column(name = "date_heure_fin")
    private LocalDateTime dateHeureFin;

    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutPrestation statut = StatutPrestation.EN_COURS;

    // Numéro d'ordre dans la file d'attente (FIFO pour les walk-in)
    @Column(name = "ordre_file_attente")
    private Integer ordreFileAttente;

    @OneToMany(mappedBy = "prestation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePrestation> lignes = new ArrayList<>();

    @OneToOne(mappedBy = "prestation", cascade = CascadeType.ALL)
    private Facture facture;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public Prestation() {
        super();
        this.montantTotal = BigDecimal.ZERO;
        this.statut = StatutPrestation.EN_COURS;
        this.lignes = new ArrayList<>();
    }

    public Prestation(
            Long id,
            Salon salon,
            AffectationSalon coiffeur,
            Compte client,
            RendezVous rendezVous,
            String nomClient,
            String prenomClient,
            String telephoneClient,
            LocalDateTime dateHeureDebut,
            LocalDateTime dateHeureFin,
            BigDecimal montantTotal,
            StatutPrestation statut,
            Integer ordreFileAttente,
            List<LignePrestation> lignes,
            Facture facture,
            LocalDateTime dateCreation,
            LocalDateTime dateModification) {
        super();
        this.id = id;
        this.salon = salon;
        this.coiffeur = coiffeur;
        this.client = client;
        this.rendezVous = rendezVous;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.telephoneClient = telephoneClient;
        this.dateHeureDebut = dateHeureDebut;
        this.dateHeureFin = dateHeureFin;
        this.montantTotal = (montantTotal != null) ? montantTotal : BigDecimal.ZERO;
        this.statut = (statut != null) ? statut : StatutPrestation.EN_COURS;
        this.ordreFileAttente = ordreFileAttente;
        this.lignes = (lignes != null) ? lignes : new ArrayList<>();
        this.facture = facture;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Salon salon;
        private AffectationSalon coiffeur;
        private Compte client;
        private RendezVous rendezVous;
        private String nomClient;
        private String prenomClient;
        private String telephoneClient;
        private LocalDateTime dateHeureDebut;
        private LocalDateTime dateHeureFin;
        private BigDecimal montantTotal = BigDecimal.ZERO;
        private StatutPrestation statut = StatutPrestation.EN_COURS;
        private Integer ordreFileAttente;
        private List<LignePrestation> lignes = new ArrayList<>();
        private Facture facture;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;

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

        public Builder rendezVous(RendezVous rendezVous) {
            this.rendezVous = rendezVous;
            return this;
        }

        public Builder nomClient(String nomClient) {
            this.nomClient = nomClient;
            return this;
        }

        public Builder prenomClient(String prenomClient) {
            this.prenomClient = prenomClient;
            return this;
        }

        public Builder telephoneClient(String telephoneClient) {
            this.telephoneClient = telephoneClient;
            return this;
        }

        public Builder dateHeureDebut(LocalDateTime dateHeureDebut) {
            this.dateHeureDebut = dateHeureDebut;
            return this;
        }

        public Builder dateHeureFin(LocalDateTime dateHeureFin) {
            this.dateHeureFin = dateHeureFin;
            return this;
        }

        public Builder montantTotal(BigDecimal montantTotal) {
            this.montantTotal = montantTotal;
            return this;
        }

        public Builder statut(StatutPrestation statut) {
            this.statut = statut;
            return this;
        }

        public Builder ordreFileAttente(Integer ordreFileAttente) {
            this.ordreFileAttente = ordreFileAttente;
            return this;
        }

        public Builder lignes(List<LignePrestation> lignes) {
            this.lignes = lignes;
            return this;
        }

        public Builder facture(Facture facture) {
            this.facture = facture;
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

        public Prestation build() {
            return new Prestation(
                    id, salon, coiffeur, client, rendezVous, nomClient, prenomClient, telephoneClient,
                    dateHeureDebut, dateHeureFin, montantTotal, statut, ordreFileAttente, lignes, facture,
                    dateCreation, dateModification);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.dateHeureDebut == null) {
            this.dateHeureDebut = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutPrestation.EN_COURS;
        }
        if (this.montantTotal == null) {
            this.montantTotal = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public boolean isTransitionAutorisee(StatutPrestation nouveauStatut) {
        return switch (this.statut) {
            case EN_ATTENTE -> nouveauStatut == StatutPrestation.EN_COURS || nouveauStatut == StatutPrestation.ANNULEE;
            case EN_COURS -> nouveauStatut == StatutPrestation.TERMINEE || nouveauStatut == StatutPrestation.ANNULEE;
            case TERMINEE, ANNULEE -> false;
        };
    }
}
