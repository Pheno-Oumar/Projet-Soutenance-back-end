package com.kadi_aon.mon_salon.stock.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "produit", uniqueConstraints = {
        @UniqueConstraint(name = "uk_produit_categorie_nom", columnNames = {"categorie_id", "nom"})
})
@Getter
@Setter
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire.")
    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    @PositiveOrZero(message = "Le prix de vente doit être positif ou nul.")
    @Column(name = "prix_vente", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixVente;

    @Column(nullable = false)
    private boolean statut = true;

    @ElementCollection
    @CollectionTable(name = "produit_images", joinColumns = @JoinColumn(name = "produit_id"))
    @Column(name = "image_url", length = 1000)
    @OrderColumn(name = "ordre")
    private List<String> imagesUrls = new ArrayList<>();

    public String getImageUrl() {
        return (imagesUrls != null && !imagesUrls.isEmpty()) ? imagesUrls.get(0) : null;
    }

    public void setImageUrl(String url) {
        if (this.imagesUrls == null) {
            this.imagesUrls = new ArrayList<>();
        }
        if (url != null && !url.isBlank()) {
            if (this.imagesUrls.isEmpty()) {
                this.imagesUrls.add(url);
            } else {
                this.imagesUrls.set(0, url);
            }
        }
    }

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_id", nullable = false)
    private CategorieProduit categorie;

    @OneToOne(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private StockProduit stock;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MouvementStock> mouvements = new ArrayList<>();

    public com.kadi_aon.mon_salon.salon.entity.Salon getSalon() {
        return (this.categorie != null) ? this.categorie.getSalon() : null;
    }

    public Produit() {
        super();
    }

    public Produit(Long id, String nom, String description, BigDecimal prixVente, boolean statut,
                   List<String> imagesUrls, LocalDateTime dateCreation, LocalDateTime dateModification,
                   CategorieProduit categorie, StockProduit stock, List<MouvementStock> mouvements) {
        super();
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prixVente = prixVente;
        this.statut = statut;
        this.imagesUrls = imagesUrls != null ? imagesUrls : new ArrayList<>();
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.categorie = categorie;
        this.stock = stock;
        this.mouvements = mouvements != null ? mouvements : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private String description;
        private BigDecimal prixVente;
        private boolean statut = true;
        private List<String> imagesUrls = new ArrayList<>();
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;
        private CategorieProduit categorie;
        private StockProduit stock;
        private List<MouvementStock> mouvements = new ArrayList<>();

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

        public Builder prixVente(BigDecimal prixVente) {
            this.prixVente = prixVente;
            return this;
        }

        public Builder statut(boolean statut) {
            this.statut = statut;
            return this;
        }

        public Builder imagesUrls(List<String> imagesUrls) {
            this.imagesUrls = imagesUrls != null ? imagesUrls : new ArrayList<>();
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            if (imageUrl != null && !imageUrl.isBlank()) {
                this.imagesUrls = new ArrayList<>(List.of(imageUrl));
            }
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

        public Builder categorie(CategorieProduit categorie) {
            this.categorie = categorie;
            return this;
        }

        public Builder stock(StockProduit stock) {
            this.stock = stock;
            return this;
        }

        public Builder mouvements(List<MouvementStock> mouvements) {
            this.mouvements = mouvements != null ? mouvements : new ArrayList<>();
            return this;
        }

        public Produit build() {
            return new Produit(id, nom, description, prixVente, statut, imagesUrls,
                    dateCreation, dateModification, categorie, stock, mouvements);
        }
    }
}
