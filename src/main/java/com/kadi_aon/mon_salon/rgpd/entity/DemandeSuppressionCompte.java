package com.kadi_aon.mon_salon.rgpd.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.rgpd.enums.StatutDemandeSuppression;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "demande_suppression_compte")
@Getter
@Setter
public class DemandeSuppressionCompte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @Column(length = 1000)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutDemandeSuppression statut = StatutDemandeSuppression.EN_ATTENTE;

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_decision")
    private LocalDateTime dateDecision;

    @Column(name = "motif_decision", length = 1000)
    private String motifDecision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traite_par_compte_id")
    private Compte traitePar;

    public DemandeSuppressionCompte() {
        super();
        this.statut = StatutDemandeSuppression.EN_ATTENTE;
    }

    public DemandeSuppressionCompte(Long id, Compte compte, String motif, StatutDemandeSuppression statut,
            LocalDateTime dateDemande, LocalDateTime dateDecision, String motifDecision, Compte traitePar) {
        super();
        this.id = id;
        this.compte = compte;
        this.motif = motif;
        this.statut = (statut != null) ? statut : StatutDemandeSuppression.EN_ATTENTE;
        this.dateDemande = dateDemande;
        this.dateDecision = dateDecision;
        this.motifDecision = motifDecision;
        this.traitePar = traitePar;
    }

    @PrePersist
    public void prePersist() {
        if (this.dateDemande == null) {
            this.dateDemande = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutDemandeSuppression.EN_ATTENTE;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Compte compte;
        private String motif;
        private StatutDemandeSuppression statut = StatutDemandeSuppression.EN_ATTENTE;
        private LocalDateTime dateDemande;
        private LocalDateTime dateDecision;
        private String motifDecision;
        private Compte traitePar;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder compte(Compte compte) {
            this.compte = compte;
            return this;
        }

        public Builder motif(String motif) {
            this.motif = motif;
            return this;
        }

        public Builder statut(StatutDemandeSuppression statut) {
            this.statut = statut;
            return this;
        }

        public Builder dateDemande(LocalDateTime dateDemande) {
            this.dateDemande = dateDemande;
            return this;
        }

        public Builder dateDecision(LocalDateTime dateDecision) {
            this.dateDecision = dateDecision;
            return this;
        }

        public Builder motifDecision(String motifDecision) {
            this.motifDecision = motifDecision;
            return this;
        }

        public Builder traitePar(Compte traitePar) {
            this.traitePar = traitePar;
            return this;
        }

        public DemandeSuppressionCompte build() {
            return new DemandeSuppressionCompte(id, compte, motif, statut, dateDemande, dateDecision,
                    motifDecision, traitePar);
        }
    }
}
