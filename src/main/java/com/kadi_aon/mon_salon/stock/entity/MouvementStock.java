package com.kadi_aon.mon_salon.stock.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.stock.enums.TypeMouvementStock;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mouvement_stock")
@Getter
@Setter
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @NotNull(message = "La quantité est obligatoire.")
    @Positive(message = "La quantité du mouvement doit être strictement positive.")
    @Column(nullable = false)
    private Integer quantite;

    @NotNull(message = "Le type de mouvement est obligatoire.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeMouvementStock type;

    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement;

    @Column(length = 500)
    private String motif;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auteur_affectation_id", nullable = false)
    private AffectationSalon auteur;

    public MouvementStock() {
        super();
    }

    public MouvementStock(Long id, Produit produit, Integer quantite, TypeMouvementStock type,
                          BigDecimal prixUnitaire, LocalDateTime dateMouvement, String motif, AffectationSalon auteur) {
        super();
        this.id = id;
        this.produit = produit;
        this.quantite = quantite;
        this.type = type;
        this.prixUnitaire = prixUnitaire;
        this.dateMouvement = dateMouvement;
        this.motif = motif;
        this.auteur = auteur;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Produit produit;
        private Integer quantite;
        private TypeMouvementStock type;
        private BigDecimal prixUnitaire;
        private LocalDateTime dateMouvement;
        private String motif;
        private AffectationSalon auteur;

        public Builder id(Long id) {
            this.id = id;
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

        public Builder type(TypeMouvementStock type) {
            this.type = type;
            return this;
        }

        public Builder prixUnitaire(BigDecimal prixUnitaire) {
            this.prixUnitaire = prixUnitaire;
            return this;
        }

        public Builder dateMouvement(LocalDateTime dateMouvement) {
            this.dateMouvement = dateMouvement;
            return this;
        }

        public Builder motif(String motif) {
            this.motif = motif;
            return this;
        }

        public Builder auteur(AffectationSalon auteur) {
            this.auteur = auteur;
            return this;
        }

        public MouvementStock build() {
            return new MouvementStock(id, produit, quantite, type, prixUnitaire, dateMouvement, motif, auteur);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateMouvement == null) {
            this.dateMouvement = LocalDateTime.now();
        }
    }
}
