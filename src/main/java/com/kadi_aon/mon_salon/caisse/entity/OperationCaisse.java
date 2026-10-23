package com.kadi_aon.mon_salon.caisse.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.caisse.enums.TypeOperationCaisse;
import com.kadi_aon.mon_salon.facturation.entity.Paiement;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "operations_caisse")
@Getter
@Setter
public class OperationCaisse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_caisse_id", nullable = false)
    private SessionCaisse sessionCaisse;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_operation", nullable = false)
    private LocalDateTime dateOperation;

    @Column(nullable = false, length = 255)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeOperationCaisse type;

    @Column(nullable = false)
    private Boolean statut = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;

    public OperationCaisse() {
        super();
    }

    public OperationCaisse(Long id, SessionCaisse sessionCaisse, BigDecimal montant,
                           LocalDateTime dateOperation, String libelle, TypeOperationCaisse type,
                           Boolean statut, Paiement paiement) {
        super();
        this.id = id;
        this.sessionCaisse = sessionCaisse;
        this.montant = montant;
        this.dateOperation = dateOperation;
        this.libelle = libelle;
        this.type = type;
        this.statut = statut != null ? statut : true;
        this.paiement = paiement;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private SessionCaisse sessionCaisse;
        private BigDecimal montant;
        private LocalDateTime dateOperation;
        private String libelle;
        private TypeOperationCaisse type;
        private Boolean statut = true;
        private Paiement paiement;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder sessionCaisse(SessionCaisse sessionCaisse) {
            this.sessionCaisse = sessionCaisse;
            return this;
        }

        public Builder montant(BigDecimal montant) {
            this.montant = montant;
            return this;
        }

        public Builder dateOperation(LocalDateTime dateOperation) {
            this.dateOperation = dateOperation;
            return this;
        }

        public Builder libelle(String libelle) {
            this.libelle = libelle;
            return this;
        }

        public Builder type(TypeOperationCaisse type) {
            this.type = type;
            return this;
        }

        public Builder statut(Boolean statut) {
            this.statut = statut;
            return this;
        }

        public Builder paiement(Paiement paiement) {
            this.paiement = paiement;
            return this;
        }

        public OperationCaisse build() {
            return new OperationCaisse(id, sessionCaisse, montant, dateOperation, libelle, type, statut, paiement);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateOperation == null) {
            this.dateOperation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = true;
        }
    }
}
