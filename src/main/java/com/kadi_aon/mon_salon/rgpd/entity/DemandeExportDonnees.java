package com.kadi_aon.mon_salon.rgpd.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.rgpd.enums.FormatExportDonnees;
import com.kadi_aon.mon_salon.rgpd.enums.StatutExportDonnees;

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
@Table(name = "demande_export_donnees")
@Getter
@Setter
public class DemandeExportDonnees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormatExportDonnees format;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutExportDonnees statut = StatutExportDonnees.EN_ATTENTE;

    @Column(name = "url_telechargement", length = 1000)
    private String urlTelechargement;

    @Column(name = "cloudinary_public_id", length = 255)
    private String cloudinaryPublicId;

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    public DemandeExportDonnees() {
        super();
        this.statut = StatutExportDonnees.EN_ATTENTE;
    }

    public DemandeExportDonnees(Long id, Compte compte, FormatExportDonnees format, StatutExportDonnees statut,
            String urlTelechargement, String cloudinaryPublicId, LocalDateTime dateDemande,
            LocalDateTime dateExpiration) {
        super();
        this.id = id;
        this.compte = compte;
        this.format = format;
        this.statut = (statut != null) ? statut : StatutExportDonnees.EN_ATTENTE;
        this.urlTelechargement = urlTelechargement;
        this.cloudinaryPublicId = cloudinaryPublicId;
        this.dateDemande = dateDemande;
        this.dateExpiration = dateExpiration;
    }

    @PrePersist
    public void prePersist() {
        if (this.dateDemande == null) {
            this.dateDemande = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutExportDonnees.EN_ATTENTE;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Compte compte;
        private FormatExportDonnees format;
        private StatutExportDonnees statut = StatutExportDonnees.EN_ATTENTE;
        private String urlTelechargement;
        private String cloudinaryPublicId;
        private LocalDateTime dateDemande;
        private LocalDateTime dateExpiration;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder compte(Compte compte) {
            this.compte = compte;
            return this;
        }

        public Builder format(FormatExportDonnees format) {
            this.format = format;
            return this;
        }

        public Builder statut(StatutExportDonnees statut) {
            this.statut = statut;
            return this;
        }

        public Builder urlTelechargement(String urlTelechargement) {
            this.urlTelechargement = urlTelechargement;
            return this;
        }

        public Builder cloudinaryPublicId(String cloudinaryPublicId) {
            this.cloudinaryPublicId = cloudinaryPublicId;
            return this;
        }

        public Builder dateDemande(LocalDateTime dateDemande) {
            this.dateDemande = dateDemande;
            return this;
        }

        public Builder dateExpiration(LocalDateTime dateExpiration) {
            this.dateExpiration = dateExpiration;
            return this;
        }

        public DemandeExportDonnees build() {
            return new DemandeExportDonnees(id, compte, format, statut, urlTelechargement, cloudinaryPublicId,
                    dateDemande, dateExpiration);
        }
    }
}
