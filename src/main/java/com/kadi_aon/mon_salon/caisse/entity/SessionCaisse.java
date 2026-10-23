package com.kadi_aon.mon_salon.caisse.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kadi_aon.mon_salon.caisse.enums.StatutSessionCaisse;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sessions_caisse")
@Getter
@Setter
public class SessionCaisse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affectation_id", nullable = false)
    private AffectationSalon affectation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    @Column(name = "solde_ouverture", nullable = false, precision = 12, scale = 2)
    private BigDecimal soldeOuverture;

    @Column(name = "solde_fermeture", precision = 12, scale = 2)
    private BigDecimal soldeFermeture;

    @Column(name = "solde_theorique", precision = 12, scale = 2)
    private BigDecimal soldeTheorique;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutSessionCaisse statut = StatutSessionCaisse.EN_COURS;

    @OneToMany(mappedBy = "sessionCaisse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperationCaisse> operations = new ArrayList<>();

    public SessionCaisse() {
        super();
    }

    public SessionCaisse(Long id, AffectationSalon affectation, Salon salon, LocalDateTime dateOuverture,
                         LocalDateTime dateCloture, BigDecimal soldeOuverture, BigDecimal soldeFermeture,
                         BigDecimal soldeTheorique, StatutSessionCaisse statut, List<OperationCaisse> operations) {
        super();
        this.id = id;
        this.affectation = affectation;
        this.salon = salon;
        this.dateOuverture = dateOuverture;
        this.dateCloture = dateCloture;
        this.soldeOuverture = soldeOuverture;
        this.soldeFermeture = soldeFermeture;
        this.soldeTheorique = soldeTheorique;
        this.statut = statut != null ? statut : StatutSessionCaisse.EN_COURS;
        this.operations = operations != null ? operations : new ArrayList<>();
    }

    public SessionCaisse(Long id, AffectationSalon affectation, Salon salon, LocalDateTime dateOuverture,
                         LocalDateTime dateCloture, BigDecimal soldeOuverture, BigDecimal soldeFermeture,
                         StatutSessionCaisse statut, List<OperationCaisse> operations) {
        this(id, affectation, salon, dateOuverture, dateCloture, soldeOuverture, soldeFermeture, soldeOuverture, statut, operations);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private AffectationSalon affectation;
        private Salon salon;
        private LocalDateTime dateOuverture;
        private LocalDateTime dateCloture;
        private BigDecimal soldeOuverture;
        private BigDecimal soldeFermeture;
        private BigDecimal soldeTheorique;
        private StatutSessionCaisse statut = StatutSessionCaisse.EN_COURS;
        private List<OperationCaisse> operations = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder affectation(AffectationSalon affectation) {
            this.affectation = affectation;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder dateOuverture(LocalDateTime dateOuverture) {
            this.dateOuverture = dateOuverture;
            return this;
        }

        public Builder dateCloture(LocalDateTime dateCloture) {
            this.dateCloture = dateCloture;
            return this;
        }

        public Builder soldeOuverture(BigDecimal soldeOuverture) {
            this.soldeOuverture = soldeOuverture;
            return this;
        }

        public Builder soldeFermeture(BigDecimal soldeFermeture) {
            this.soldeFermeture = soldeFermeture;
            return this;
        }

        public Builder soldeTheorique(BigDecimal soldeTheorique) {
            this.soldeTheorique = soldeTheorique;
            return this;
        }

        public Builder statut(StatutSessionCaisse statut) {
            this.statut = statut;
            return this;
        }

        public Builder operations(List<OperationCaisse> operations) {
            this.operations = operations != null ? operations : new ArrayList<>();
            return this;
        }

        public SessionCaisse build() {
            return new SessionCaisse(id, affectation, salon, dateOuverture, dateCloture, soldeOuverture, soldeFermeture, soldeTheorique, statut, operations);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateOuverture == null) {
            this.dateOuverture = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutSessionCaisse.EN_COURS;
        }
        if (this.soldeOuverture == null) {
            this.soldeOuverture = BigDecimal.ZERO;
        }
        if (this.soldeTheorique == null) {
            this.soldeTheorique = this.soldeOuverture != null ? this.soldeOuverture : BigDecimal.ZERO;
        }
        if (this.salon == null && this.affectation != null && this.affectation.getSalon() != null) {
            this.salon = this.affectation.getSalon();
        }
    }
}
