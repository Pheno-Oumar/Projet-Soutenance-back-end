package com.kadi_aon.mon_salon.stock.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ligne_commande")
@Getter
@Setter
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Positive(message = "La quantité doit être strictement positive.")
    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "sous_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal sousTotal;

    public LigneCommande() {
        super();
    }

    public LigneCommande(Long id, Commande commande, Produit produit, Integer quantite,
                         BigDecimal prixUnitaire, BigDecimal sousTotal) {
        super();
        this.id = id;
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = sousTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Commande commande;
        private Produit produit;
        private Integer quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal sousTotal;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder commande(Commande commande) {
            this.commande = commande;
            return this;
        }

        public Builder produit(Produit produit) {
            this.produit = produit;
            return this;
        }

        public Builder quantite(Integer quantite) {
            this.quantite = quantite;
            return this;
        }

        public Builder prixUnitaire(BigDecimal prixUnitaire) {
            this.prixUnitaire = prixUnitaire;
            return this;
        }

        public Builder sousTotal(BigDecimal sousTotal) {
            this.sousTotal = sousTotal;
            return this;
        }

        public LigneCommande build() {
            return new LigneCommande(id, commande, produit, quantite, prixUnitaire, sousTotal);
        }
    }
}
