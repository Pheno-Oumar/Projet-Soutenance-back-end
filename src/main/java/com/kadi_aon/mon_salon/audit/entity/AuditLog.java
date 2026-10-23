package com.kadi_aon.mon_salon.audit.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;

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
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_heure", nullable = false, updatable = false)
    private LocalDateTime dateHeure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeActionAudit action;

    @Column(nullable = false, length = 100)
    private String entite;

    @Column(name = "entite_id", length = 100)
    private String entiteId;

    @Column(name = "ancienne_valeur", columnDefinition = "TEXT")
    private String ancienneValeur;

    @Column(name = "nouvelle_valeur", columnDefinition = "TEXT")
    private String nouvelleValeur;

    @Column(name = "adresse_ip", length = 60)
    private String adresseIP;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "role_utilise", length = 50)
    private String roleUtilise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id")
    private Compte compte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affectation_salon_id")
    private AffectationSalon affectationSalon;

    public AuditLog() {
    }

    public AuditLog(Long id, LocalDateTime dateHeure, TypeActionAudit action, String entite, String entiteId,
                    String ancienneValeur, String nouvelleValeur, String adresseIP, String userAgent,
                    String roleUtilise, Compte compte, AffectationSalon affectationSalon) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.action = action;
        this.entite = entite;
        this.entiteId = entiteId;
        this.ancienneValeur = ancienneValeur;
        this.nouvelleValeur = nouvelleValeur;
        this.adresseIP = adresseIP;
        this.userAgent = userAgent;
        this.roleUtilise = roleUtilise;
        this.compte = compte;
        this.affectationSalon = affectationSalon;
    }

    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    public static class AuditLogBuilder {
        private Long id;
        private LocalDateTime dateHeure;
        private TypeActionAudit action;
        private String entite;
        private String entiteId;
        private String ancienneValeur;
        private String nouvelleValeur;
        private String adresseIP;
        private String userAgent;
        private String roleUtilise;
        private Compte compte;
        private AffectationSalon affectationSalon;

        AuditLogBuilder() {
        }

        public AuditLogBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuditLogBuilder dateHeure(LocalDateTime dateHeure) {
            this.dateHeure = dateHeure;
            return this;
        }

        public AuditLogBuilder action(TypeActionAudit action) {
            this.action = action;
            return this;
        }

        public AuditLogBuilder entite(String entite) {
            this.entite = entite;
            return this;
        }

        public AuditLogBuilder entiteId(String entiteId) {
            this.entiteId = entiteId;
            return this;
        }

        public AuditLogBuilder ancienneValeur(String ancienneValeur) {
            this.ancienneValeur = ancienneValeur;
            return this;
        }

        public AuditLogBuilder nouvelleValeur(String nouvelleValeur) {
            this.nouvelleValeur = nouvelleValeur;
            return this;
        }

        public AuditLogBuilder adresseIP(String adresseIP) {
            this.adresseIP = adresseIP;
            return this;
        }

        public AuditLogBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public AuditLogBuilder roleUtilise(String roleUtilise) {
            this.roleUtilise = roleUtilise;
            return this;
        }

        public AuditLogBuilder compte(Compte compte) {
            this.compte = compte;
            return this;
        }

        public AuditLogBuilder affectationSalon(AffectationSalon affectationSalon) {
            this.affectationSalon = affectationSalon;
            return this;
        }

        public AuditLog build() {
            return new AuditLog(id, dateHeure, action, entite, entiteId, ancienneValeur, nouvelleValeur,
                    adresseIP, userAgent, roleUtilise, compte, affectationSalon);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateHeure == null) {
            this.dateHeure = LocalDateTime.now();
        }
    }
}
