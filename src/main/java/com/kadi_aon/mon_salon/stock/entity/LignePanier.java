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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ligne_panier", uniqueConstraints = {
        @UniqueConstraint(name = "uk_panier_produit", columnNames = {"panier_id", "produit_id"})
})
@Getter
@Setter
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "panier_id", nullable = false)
    private Panier panier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Positive(message = "La quantité doit être strictement positive.")
    @Column(nullable = false)
    private Integer quantite;

    public LignePanier() {
        super();
    }

    public LignePanier(Long id, Panier panier, Produit produit, Integer quantite) {
        super();
        this.id = id;
        this.panier = panier;
        this.produit = produit;
        this.quantite = quantite;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Panier panier;
        private Produit produit;
        private Integer quantite;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder panier(Panier panier) {
            this.panier = panier;
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

        public LignePanier build() {
            return new LignePanier(id, panier, produit, quantite);
        }
    }

    public BigDecimal calculerSousTotal() {
        if (produit == null || produit.getPrixVente() == null || quantite == null) {
            return BigDecimal.ZERO;
        }
        return produit.getPrixVente().multiply(BigDecimal.valueOf(quantite));
    }
}
