package com.kadi_aon.mon_salon.depense.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.caisse.entity.OperationCaisse;
import com.kadi_aon.mon_salon.depense.enums.CategorieDepense;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.stock.entity.MouvementStock;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "depense")
@Getter
@Setter
public class Depense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le montant de la dépense est obligatoire.")
    @Positive(message = "Le montant de la dépense doit être strictement positif.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_depense", nullable = false)
    private LocalDateTime dateDepense;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "La catégorie de dépense est obligatoire.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategorieDepense categorie;

    @Column(nullable = false)
    private boolean statut = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comptable_affectation_id", nullable = false)
    private AffectationSalon comptable;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_caisse_id")
    private OperationCaisse operationCaisse;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mouvement_stock_id")
    private MouvementStock mouvementStock;

    public Depense() {
        super();
    }

    public Depense(Long id, BigDecimal montant, LocalDateTime dateDepense, String description,
                   CategorieDepense categorie, boolean statut, Salon salon,
                   AffectationSalon comptable, OperationCaisse operationCaisse, MouvementStock mouvementStock) {
        super();
        this.id = id;
        this.montant = montant;
        this.dateDepense = dateDepense;
        this.description = description;
        this.categorie = categorie;
        this.statut = statut;
        this.salon = salon;
        this.comptable = comptable;
        this.operationCaisse = operationCaisse;
        this.mouvementStock = mouvementStock;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private BigDecimal montant;
        private LocalDateTime dateDepense;
        private String description;
        private CategorieDepense categorie;
        private boolean statut = true;
        private Salon salon;
        private AffectationSalon comptable;
        private OperationCaisse operationCaisse;
        private MouvementStock mouvementStock;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder montant(BigDecimal montant) {
            this.montant = montant;
            return this;
        }

        public Builder dateDepense(LocalDateTime dateDepense) {
            this.dateDepense = dateDepense;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder categorie(CategorieDepense categorie) {
            this.categorie = categorie;
            return this;
        }

        public Builder statut(boolean statut) {
            this.statut = statut;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder comptable(AffectationSalon comptable) {
            this.comptable = comptable;
            return this;
        }

        public Builder operationCaisse(OperationCaisse operationCaisse) {
            this.operationCaisse = operationCaisse;
            return this;
        }

        public Builder mouvementStock(MouvementStock mouvementStock) {
            this.mouvementStock = mouvementStock;
            return this;
        }

        public Depense build() {
            return new Depense(id, montant, dateDepense, description, categorie, statut, salon, comptable, operationCaisse, mouvementStock);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateDepense == null) {
            this.dateDepense = LocalDateTime.now();
        }
    }
}
