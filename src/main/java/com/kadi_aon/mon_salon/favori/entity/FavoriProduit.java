package com.kadi_aon.mon_salon.favori.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.stock.entity.Produit;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favori_produit", uniqueConstraints = {
        @UniqueConstraint(name = "uk_favori_produit_compte_produit", columnNames = {"client_id", "produit_id"})
})
@Getter
@Setter
public class FavoriProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Compte client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    public FavoriProduit() {
        super();
    }

    public FavoriProduit(Long id, Compte client, Produit produit, LocalDateTime dateAjout) {
        super();
        this.id = id;
        this.client = client;
        this.produit = produit;
        this.dateAjout = dateAjout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Compte client;
        private Produit produit;
        private LocalDateTime dateAjout;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder client(Compte client) {
            this.client = client;
            return this;
        }

        public Builder produit(Produit produit) {
            this.produit = produit;
            return this;
        }

        public Builder dateAjout(LocalDateTime dateAjout) {
            this.dateAjout = dateAjout;
            return this;
        }

        public FavoriProduit build() {
            return new FavoriProduit(id, client, produit, dateAjout);
        }
    }
}
