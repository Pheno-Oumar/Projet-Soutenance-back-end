package com.kadi_aon.mon_salon.stock.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.facturation.entity.Facture;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.stock.enums.StatutCommande;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "commandes")
@Getter
@Setter
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_commande", nullable = false, unique = true, length = 50)
    private String numeroCommande;

    @Column(name = "date_commande", nullable = false)
    private LocalDateTime dateCommande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutCommande statut = StatutCommande.EN_ATTENTE;

    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_affectation_id", nullable = false)
    private AffectationSalon clientAffectation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traite_par_affectation_id")
    private AffectationSalon traiteParAffectation;

    @Column(name = "motif_rejet", length = 500)
    private String motifRejet;

    @Column(name = "code_retrait", length = 30)
    private String codeRetrait;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignes = new ArrayList<>();

    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Facture facture;

    @Column(name = "encaissee_en_caisse")
    private Boolean encaisseeEnCaisse = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "moyen_paiement", length = 30)
    private com.kadi_aon.mon_salon.facturation.enums.MoyenPaiement moyenPaiement;

    @Column(name = "date_encaissement_caisse")
    private LocalDateTime dateEncaissementCaisse;

    public Commande() {
        super();
    }

    public Commande(Long id, String numeroCommande, LocalDateTime dateCommande, StatutCommande statut,
                    BigDecimal montantTotal, AffectationSalon clientAffectation,
                    AffectationSalon traiteParAffectation, String motifRejet, String codeRetrait,
                    LocalDateTime dateTraitement, List<LigneCommande> lignes, Facture facture) {
        this(id, numeroCommande, dateCommande, statut, montantTotal, clientAffectation,
                traiteParAffectation, motifRejet, codeRetrait, dateTraitement, lignes, facture, false, null, null);
    }

    public Commande(Long id, String numeroCommande, LocalDateTime dateCommande, StatutCommande statut,
                    BigDecimal montantTotal, AffectationSalon clientAffectation,
                    AffectationSalon traiteParAffectation, String motifRejet, String codeRetrait,
                    LocalDateTime dateTraitement, List<LigneCommande> lignes, Facture facture,
                    Boolean encaisseeEnCaisse, com.kadi_aon.mon_salon.facturation.enums.MoyenPaiement moyenPaiement,
                    LocalDateTime dateEncaissementCaisse) {
        super();
        this.id = id;
        this.numeroCommande = numeroCommande;
        this.dateCommande = dateCommande;
        this.statut = statut != null ? statut : StatutCommande.EN_ATTENTE;
        this.montantTotal = montantTotal;
        this.clientAffectation = clientAffectation;
        this.traiteParAffectation = traiteParAffectation;
        this.motifRejet = motifRejet;
        this.codeRetrait = codeRetrait;
        this.dateTraitement = dateTraitement;
        this.lignes = lignes != null ? lignes : new ArrayList<>();
        this.facture = facture;
        this.encaisseeEnCaisse = encaisseeEnCaisse != null ? encaisseeEnCaisse : false;
        this.moyenPaiement = moyenPaiement;
        this.dateEncaissementCaisse = dateEncaissementCaisse;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String numeroCommande;
        private LocalDateTime dateCommande;
        private StatutCommande statut = StatutCommande.EN_ATTENTE;
        private BigDecimal montantTotal;
        private AffectationSalon clientAffectation;
        private AffectationSalon traiteParAffectation;
        private String motifRejet;
        private String codeRetrait;
        private LocalDateTime dateTraitement;
        private List<LigneCommande> lignes = new ArrayList<>();
        private Facture facture;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder numeroCommande(String numeroCommande) {
            this.numeroCommande = numeroCommande;
            return this;
        }

        public Builder dateCommande(LocalDateTime dateCommande) {
            this.dateCommande = dateCommande;
            return this;
        }

        public Builder statut(StatutCommande statut) {
            this.statut = statut;
            return this;
        }

        public Builder montantTotal(BigDecimal montantTotal) {
            this.montantTotal = montantTotal;
            return this;
        }

        public Builder clientAffectation(AffectationSalon clientAffectation) {
            this.clientAffectation = clientAffectation;
            return this;
        }

        public Builder traiteParAffectation(AffectationSalon traiteParAffectation) {
            this.traiteParAffectation = traiteParAffectation;
            return this;
        }

        public Builder motifRejet(String motifRejet) {
            this.motifRejet = motifRejet;
            return this;
        }

        public Builder codeRetrait(String codeRetrait) {
            this.codeRetrait = codeRetrait;
            return this;
        }

        public Builder dateTraitement(LocalDateTime dateTraitement) {
            this.dateTraitement = dateTraitement;
            return this;
        }

        public Builder lignes(List<LigneCommande> lignes) {
            this.lignes = lignes != null ? lignes : new ArrayList<>();
            return this;
        }

        public Builder facture(Facture facture) {
            this.facture = facture;
            return this;
        }

        private Boolean encaisseeEnCaisse = false;
        private com.kadi_aon.mon_salon.facturation.enums.MoyenPaiement moyenPaiement;
        private LocalDateTime dateEncaissementCaisse;

        public Builder encaisseeEnCaisse(Boolean encaisseeEnCaisse) {
            this.encaisseeEnCaisse = encaisseeEnCaisse;
            return this;
        }

        public Builder moyenPaiement(com.kadi_aon.mon_salon.facturation.enums.MoyenPaiement moyenPaiement) {
            this.moyenPaiement = moyenPaiement;
            return this;
        }

        public Builder dateEncaissementCaisse(LocalDateTime dateEncaissementCaisse) {
            this.dateEncaissementCaisse = dateEncaissementCaisse;
            return this;
        }

        public Commande build() {
            return new Commande(id, numeroCommande, dateCommande, statut, montantTotal,
                    clientAffectation, traiteParAffectation, motifRejet, codeRetrait,
                    dateTraitement, lignes, facture, encaisseeEnCaisse, moyenPaiement, dateEncaissementCaisse);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCommande == null) {
            this.dateCommande = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutCommande.EN_ATTENTE;
        }
        if (this.montantTotal == null) {
            this.montantTotal = BigDecimal.ZERO;
        }
    }

    public Salon getSalon() {
        return clientAffectation != null ? clientAffectation.getSalon() : null;
    }

    public Compte getClient() {
        return clientAffectation != null ? clientAffectation.getCompte() : null;
    }
}
