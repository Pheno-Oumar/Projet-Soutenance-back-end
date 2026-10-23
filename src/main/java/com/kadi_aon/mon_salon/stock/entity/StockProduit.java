package com.kadi_aon.mon_salon.stock.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stock_produit")
@Getter
@Setter
public class StockProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false, unique = true)
    private Produit produit;

    @PositiveOrZero(message = "La quantité disponible ne peut être négative.")
    @Column(name = "quantite_disponible", nullable = false)
    private Integer quantiteDisponible = 0;

    @PositiveOrZero(message = "Le seuil minimum ne peut être négatif.")
    @Column(name = "seuil_minimum", nullable = false)
    private Integer seuilMinimum = 0;

    @Column(name = "seuil_maximum")
    private Integer seuilMaximum;

    @Column(name = "date_derniere_mise_a_jour", nullable = false)
    private LocalDateTime dateDerniereMiseAJour;

    public StockProduit() {
        super();
    }

    public StockProduit(Long id, Produit produit, Integer quantiteDisponible, Integer seuilMinimum,
                        Integer seuilMaximum, LocalDateTime dateDerniereMiseAJour) {
        super();
        this.id = id;
        this.produit = produit;
        this.quantiteDisponible = quantiteDisponible != null ? quantiteDisponible : 0;
        this.seuilMinimum = seuilMinimum != null ? seuilMinimum : 0;
        this.seuilMaximum = seuilMaximum;
        this.dateDerniereMiseAJour = dateDerniereMiseAJour;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Produit produit;
        private Integer quantiteDisponible = 0;
        private Integer seuilMinimum = 0;
        private Integer seuilMaximum;
        private LocalDateTime dateDerniereMiseAJour;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder produit(Produit produit) {
            this.produit = produit;
            return this;
        }

        public Builder quantiteDisponible(Integer quantiteDisponible) {
            this.quantiteDisponible = quantiteDisponible;
            return this;
        }

        public Builder seuilMinimum(Integer seuilMinimum) {
            this.seuilMinimum = seuilMinimum;
            return this;
        }

        public Builder seuilMaximum(Integer seuilMaximum) {
            this.seuilMaximum = seuilMaximum;
            return this;
        }

        public Builder dateDerniereMiseAJour(LocalDateTime dateDerniereMiseAJour) {
            this.dateDerniereMiseAJour = dateDerniereMiseAJour;
            return this;
        }

        public StockProduit build() {
            return new StockProduit(id, produit, quantiteDisponible, seuilMinimum, seuilMaximum, dateDerniereMiseAJour);
        }
    }
}
