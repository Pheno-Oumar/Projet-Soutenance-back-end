package com.kadi_aon.mon_salon.facturation.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.caisse.entity.OperationCaisse;
import com.kadi_aon.mon_salon.facturation.enums.MoyenPaiement;
import com.kadi_aon.mon_salon.facturation.enums.StatutPaiement;
import com.kadi_aon.mon_salon.facturation.enums.TypePaiement;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "paiements")
@Getter
@Setter
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_paiement", nullable = false, unique = true, length = 50)
    private String numeroPaiement;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypePaiement type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutPaiement statut = StatutPaiement.PAYE;

    @Enumerated(EnumType.STRING)
    @Column(name = "moyen_paiement", length = 30)
    private MoyenPaiement moyenPaiement = MoyenPaiement.ESPECES;

    @Column(name = "reference", length = 100)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_origine_id")
    private Paiement paiementOrigine;

    @Column(name = "montant_rembourse", precision = 12, scale = 2)
    private BigDecimal montantRembourse = BigDecimal.ZERO;

    @OneToMany(mappedBy = "paiementOrigine", cascade = CascadeType.ALL)
    private List<Paiement> remboursements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encaisse_par_affectation_id")
    private AffectationSalon encaissePar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_compte_id")
    private Compte client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_caisse_id")
    private OperationCaisse operationCaisse;

    public Paiement() {
        super();
    }

    public Paiement(Long id, String numeroPaiement, BigDecimal montant, LocalDateTime datePaiement,
                    TypePaiement type, StatutPaiement statut, MoyenPaiement moyenPaiement,
                    String reference, Paiement paiementOrigine, BigDecimal montantRembourse,
                    List<Paiement> remboursements, AffectationSalon encaissePar, Facture facture,
                    Compte client, Salon salon, OperationCaisse operationCaisse) {
        super();
        this.id = id;
        this.numeroPaiement = numeroPaiement;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.type = type;
        this.statut = statut != null ? statut : StatutPaiement.PAYE;
        this.moyenPaiement = moyenPaiement != null ? moyenPaiement : MoyenPaiement.ESPECES;
        this.reference = reference;
        this.paiementOrigine = paiementOrigine;
        this.montantRembourse = montantRembourse != null ? montantRembourse : BigDecimal.ZERO;
        this.remboursements = remboursements != null ? remboursements : new ArrayList<>();
        this.encaissePar = encaissePar;
        this.facture = facture;
        this.client = client;
        this.salon = salon;
        this.operationCaisse = operationCaisse;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String numeroPaiement;
        private BigDecimal montant;
        private LocalDateTime datePaiement;
        private TypePaiement type;
        private StatutPaiement statut = StatutPaiement.PAYE;
        private MoyenPaiement moyenPaiement = MoyenPaiement.ESPECES;
        private String reference;
        private Paiement paiementOrigine;
        private BigDecimal montantRembourse = BigDecimal.ZERO;
        private List<Paiement> remboursements = new ArrayList<>();
        private AffectationSalon encaissePar;
        private Facture facture;
        private Compte client;
        private Salon salon;
        private OperationCaisse operationCaisse;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder numeroPaiement(String numeroPaiement) {
            this.numeroPaiement = numeroPaiement;
            return this;
        }

        public Builder montant(BigDecimal montant) {
            this.montant = montant;
            return this;
        }

        public Builder datePaiement(LocalDateTime datePaiement) {
            this.datePaiement = datePaiement;
            return this;
        }

        public Builder type(TypePaiement type) {
            this.type = type;
            return this;
        }

        public Builder statut(StatutPaiement statut) {
            this.statut = statut;
            return this;
        }

        public Builder moyenPaiement(MoyenPaiement moyenPaiement) {
            this.moyenPaiement = moyenPaiement;
            return this;
        }

        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder paiementOrigine(Paiement paiementOrigine) {
            this.paiementOrigine = paiementOrigine;
            return this;
        }

        public Builder montantRembourse(BigDecimal montantRembourse) {
            this.montantRembourse = montantRembourse;
            return this;
        }

        public Builder remboursements(List<Paiement> remboursements) {
            this.remboursements = remboursements != null ? remboursements : new ArrayList<>();
            return this;
        }

        public Builder encaissePar(AffectationSalon encaissePar) {
            this.encaissePar = encaissePar;
            return this;
        }

        public Builder facture(Facture facture) {
            this.facture = facture;
            return this;
        }

        public Builder client(Compte client) {
            this.client = client;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder operationCaisse(OperationCaisse operationCaisse) {
            this.operationCaisse = operationCaisse;
            return this;
        }

        public Paiement build() {
            return new Paiement(id, numeroPaiement, montant, datePaiement, type, statut, moyenPaiement,
                    reference, paiementOrigine, montantRembourse, remboursements, encaissePar, facture,
                    client, salon, operationCaisse);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.datePaiement == null) {
            this.datePaiement = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutPaiement.PAYE;
        }
        if (this.montantRembourse == null) {
            this.montantRembourse = BigDecimal.ZERO;
        }
    }

    public BigDecimal getMontantRemboursable() {
        BigDecimal rem = this.montantRembourse != null ? this.montantRembourse : BigDecimal.ZERO;
        BigDecimal restant = this.montant.subtract(rem);
        return restant.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : restant;
    }

    public boolean isRemboursable() {
        return (this.statut == StatutPaiement.PAYE || this.statut == StatutPaiement.PARTIELLEMENT_REMBOURSE)
                && getMontantRemboursable().compareTo(BigDecimal.ZERO) > 0;
    }
}
