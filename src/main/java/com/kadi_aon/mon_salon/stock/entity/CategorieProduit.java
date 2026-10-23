package com.kadi_aon.mon_salon.stock.entity;

import java.util.ArrayList;
import java.util.List;

import com.kadi_aon.mon_salon.salon.entity.Salon;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categorie_produit", uniqueConstraints = {
        @UniqueConstraint(name = "uk_categorie_produit_salon_nom", columnNames = {"salon_id", "nom"})
})
@Getter
@Setter
public class CategorieProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie de produit est obligatoire.")
    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean statut = true;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> produits = new ArrayList<>();

    public CategorieProduit() {
        super();
    }

    public CategorieProduit(Long id, String nom, String description, boolean statut, String imageUrl,
                            Salon salon, List<Produit> produits) {
        super();
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.statut = statut;
        this.imageUrl = imageUrl;
        this.salon = salon;
        this.produits = produits != null ? produits : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private String description;
        private boolean statut = true;
        private String imageUrl;
        private Salon salon;
        private List<Produit> produits = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder statut(boolean statut) {
            this.statut = statut;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder produits(List<Produit> produits) {
            this.produits = produits != null ? produits : new ArrayList<>();
            return this;
        }

        public CategorieProduit build() {
            return new CategorieProduit(id, nom, description, statut, imageUrl, salon, produits);
        }
    }
}
