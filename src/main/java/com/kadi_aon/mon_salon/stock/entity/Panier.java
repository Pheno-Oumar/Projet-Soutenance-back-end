package com.kadi_aon.mon_salon.stock.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "panier")
@Getter
@Setter
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_affectation_id", nullable = false, unique = true)
    private AffectationSalon clientAffectation;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePanier> lignes = new ArrayList<>();

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public Panier() {
        super();
    }

    public Panier(Long id, AffectationSalon clientAffectation, List<LignePanier> lignes,
                  LocalDateTime dateCreation, LocalDateTime dateModification) {
        super();
        this.id = id;
        this.clientAffectation = clientAffectation;
        this.lignes = lignes != null ? lignes : new ArrayList<>();
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private AffectationSalon clientAffectation;
        private List<LignePanier> lignes = new ArrayList<>();
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder clientAffectation(AffectationSalon clientAffectation) {
            this.clientAffectation = clientAffectation;
            return this;
        }

        public Builder lignes(List<LignePanier> lignes) {
            this.lignes = lignes != null ? lignes : new ArrayList<>();
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

        public Panier build() {
            return new Panier(id, clientAffectation, lignes, dateCreation, dateModification);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public BigDecimal calculerTotal() {
        return lignes.stream()
                .map(LignePanier::calculerSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void vider() {
        this.lignes.clear();
    }
}
