package com.kadi_aon.mon_salon.facturation.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kadi_aon.mon_salon.facturation.enums.StatutFacture;
import com.kadi_aon.mon_salon.prestation.entity.Prestation;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.stock.entity.Commande;

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
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "factures")
@Getter
@Setter
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_facture", nullable = false, unique = true, length = 50)
    private String numeroFacture;

    @Column(name = "date_emission", nullable = false)
    private LocalDateTime dateEmission;

    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "montant_paye", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(name = "reste_a_payer", nullable = false, precision = 12, scale = 2)
    private BigDecimal resteAPayer;

    @Column(name = "remise", precision = 12, scale = 2)
    private BigDecimal remise = BigDecimal.ZERO;

    @Column(name = "motif_remise", length = 255)
    private String motifRemise;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30)
    private StatutFacture statut = StatutFacture.EMISE;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id", unique = true)
    private Prestation prestation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", unique = true)
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_affectation_id")
    private AffectationSalon clientAffectation;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paiement> paiements = new ArrayList<>();

    public Facture() {
        super();
    }

    public Facture(Long id, String numeroFacture, LocalDateTime dateEmission, BigDecimal montantTotal,
                   BigDecimal montantPaye, BigDecimal resteAPayer, BigDecimal remise, String motifRemise,
                   StatutFacture statut, Long version, Salon salon, Prestation prestation,
                   Commande commande, AffectationSalon clientAffectation, List<Paiement> paiements) {
        super();
        this.id = id;
        this.numeroFacture = numeroFacture;
        this.dateEmission = dateEmission;
        this.montantTotal = montantTotal;
        this.montantPaye = montantPaye != null ? montantPaye : BigDecimal.ZERO;
        this.resteAPayer = resteAPayer;
        this.remise = remise != null ? remise : BigDecimal.ZERO;
        this.motifRemise = motifRemise;
        this.statut = statut != null ? statut : StatutFacture.EMISE;
        this.version = version;
        this.salon = salon;
        this.prestation = prestation;
        this.commande = commande;
        this.clientAffectation = clientAffectation;
        this.paiements = paiements != null ? paiements : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String numeroFacture;
        private LocalDateTime dateEmission;
        private BigDecimal montantTotal;
        private BigDecimal montantPaye = BigDecimal.ZERO;
        private BigDecimal resteAPayer;
        private BigDecimal remise = BigDecimal.ZERO;
        private String motifRemise;
        private StatutFacture statut = StatutFacture.EMISE;
        private Long version;
        private Salon salon;
        private Prestation prestation;
        private Commande commande;
        private AffectationSalon clientAffectation;
        private List<Paiement> paiements = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder numeroFacture(String numeroFacture) {
            this.numeroFacture = numeroFacture;
            return this;
        }

        public Builder dateEmission(LocalDateTime dateEmission) {
            this.dateEmission = dateEmission;
            return this;
        }

        public Builder montantTotal(BigDecimal montantTotal) {
            this.montantTotal = montantTotal;
            return this;
        }

        public Builder montantPaye(BigDecimal montantPaye) {
            this.montantPaye = montantPaye;
            return this;
        }

        public Builder resteAPayer(BigDecimal resteAPayer) {
            this.resteAPayer = resteAPayer;
            return this;
        }

        public Builder remise(BigDecimal remise) {
            this.remise = remise;
            return this;
        }

        public Builder motifRemise(String motifRemise) {
            this.motifRemise = motifRemise;
            return this;
        }

        public Builder statut(StatutFacture statut) {
            this.statut = statut;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder prestation(Prestation prestation) {
            this.prestation = prestation;
            return this;
        }

        public Builder commande(Commande commande) {
            this.commande = commande;
            return this;
        }

        public Builder clientAffectation(AffectationSalon clientAffectation) {
            this.clientAffectation = clientAffectation;
            return this;
        }

        public Builder paiements(List<Paiement> paiements) {
            this.paiements = paiements != null ? paiements : new ArrayList<>();
            return this;
        }

        public Facture build() {
            return new Facture(id, numeroFacture, dateEmission, montantTotal, montantPaye, resteAPayer,
                    remise, motifRemise, statut, version, salon, prestation, commande, clientAffectation, paiements);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateEmission == null) {
            this.dateEmission = LocalDateTime.now();
        }
        if (this.montantTotal == null) {
            this.montantTotal = BigDecimal.ZERO;
        }
        if (this.montantPaye == null) {
            this.montantPaye = BigDecimal.ZERO;
        }
        if (this.remise == null) {
            this.remise = BigDecimal.ZERO;
        }
        if (this.resteAPayer == null) {
            this.resteAPayer = getMontantNet();
        }
        if (this.statut == null) {
            this.statut = StatutFacture.EMISE;
        }
        if (this.salon == null) {
            if (this.prestation != null && this.prestation.getSalon() != null) {
                this.salon = this.prestation.getSalon();
            } else if (this.clientAffectation != null && this.clientAffectation.getSalon() != null) {
                this.salon = this.clientAffectation.getSalon();
            }
        }
    }

    public BigDecimal getMontantNet() {
        BigDecimal total = this.montantTotal != null ? this.montantTotal : BigDecimal.ZERO;
        BigDecimal rem = this.remise != null ? this.remise : BigDecimal.ZERO;
        BigDecimal net = total.subtract(rem);
        return net.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : net;
    }
}
