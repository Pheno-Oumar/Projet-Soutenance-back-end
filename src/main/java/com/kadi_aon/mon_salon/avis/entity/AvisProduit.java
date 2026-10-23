package com.kadi_aon.mon_salon.avis.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.salon.entity.Salon;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "avis_produit", uniqueConstraints = {
        @UniqueConstraint(name = "uk_avis_produit_compte_produit", columnNames = {"client_id", "produit_id"})
})
@Getter
@Setter
public class AvisProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer note;

    @Column(length = 1000)
    private String commentaire;

    @Column(nullable = false)
    private boolean statut = false;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Compte client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    public AvisProduit() {
        super();
    }

    public AvisProduit(Long id, Integer note, String commentaire, boolean statut,
                       LocalDateTime dateCreation, LocalDateTime dateModification,
                       Compte client, Produit produit, Salon salon) {
        super();
        this.id = id;
        this.note = note;
        this.commentaire = commentaire;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.client = client;
        this.produit = produit;
        this.salon = salon;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Integer note;
        private String commentaire;
        private boolean statut = false;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;
        private Compte client;
        private Produit produit;
        private Salon salon;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder note(Integer note) {
            this.note = note;
            return this;
        }

        public Builder commentaire(String commentaire) {
            this.commentaire = commentaire;
            return this;
        }

        public Builder statut(boolean statut) {
            this.statut = statut;
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

        public Builder client(Compte client) {
            this.client = client;
            return this;
        }

        public Builder produit(Produit produit) {
            this.produit = produit;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public AvisProduit build() {
            return new AvisProduit(id, note, commentaire, statut, dateCreation, dateModification, client, produit, salon);
        }
    }
}
