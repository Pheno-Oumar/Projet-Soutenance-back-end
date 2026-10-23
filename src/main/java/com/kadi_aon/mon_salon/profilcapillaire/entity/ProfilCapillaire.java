package com.kadi_aon.mon_salon.profilcapillaire.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.DensiteCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.LongueurCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profils_capillaires")
@Getter
@Setter
public class ProfilCapillaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false, unique = true)
    private Compte compte;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_cheveux", length = 50)
    private TypeCheveux typeCheveux;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TextureCheveux texture;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PorositeCheveux porosite;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private LongueurCheveux longueur;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private DensiteCheveux densite;

    @Enumerated(EnumType.STRING)
    @Column(name = "cuir_chevelu", length = 50)
    private CuirChevelu cuirChevelu;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_cheveux", length = 50)
    private EtatCheveux etatCheveux;

    @Column(name = "code_profil", length = 255)
    private String codeProfil;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    public ProfilCapillaire() {
        super();
    }

    public ProfilCapillaire(
            Long id,
            Compte compte,
            TypeCheveux typeCheveux,
            TextureCheveux texture,
            PorositeCheveux porosite,
            LongueurCheveux longueur,
            DensiteCheveux densite,
            CuirChevelu cuirChevelu,
            EtatCheveux etatCheveux,
            String codeProfil,
            LocalDateTime dateCreation,
            LocalDateTime dateMiseAJour) {
        super();
        this.id = id;
        this.compte = compte;
        this.typeCheveux = typeCheveux;
        this.texture = texture;
        this.porosite = porosite;
        this.longueur = longueur;
        this.densite = densite;
        this.cuirChevelu = cuirChevelu;
        this.etatCheveux = etatCheveux;
        this.codeProfil = codeProfil;
        this.dateCreation = dateCreation;
        this.dateMiseAJour = dateMiseAJour;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Compte compte;
        private TypeCheveux typeCheveux;
        private TextureCheveux texture;
        private PorositeCheveux porosite;
        private LongueurCheveux longueur;
        private DensiteCheveux densite;
        private CuirChevelu cuirChevelu;
        private EtatCheveux etatCheveux;
        private String codeProfil;
        private LocalDateTime dateCreation;
        private LocalDateTime dateMiseAJour;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder compte(Compte compte) {
            this.compte = compte;
            return this;
        }

        public Builder typeCheveux(TypeCheveux typeCheveux) {
            this.typeCheveux = typeCheveux;
            return this;
        }

        public Builder texture(TextureCheveux texture) {
            this.texture = texture;
            return this;
        }

        public Builder porosite(PorositeCheveux porosite) {
            this.porosite = porosite;
            return this;
        }

        public Builder longueur(LongueurCheveux longueur) {
            this.longueur = longueur;
            return this;
        }

        public Builder densite(DensiteCheveux densite) {
            this.densite = densite;
            return this;
        }

        public Builder cuirChevelu(CuirChevelu cuirChevelu) {
            this.cuirChevelu = cuirChevelu;
            return this;
        }

        public Builder etatCheveux(EtatCheveux etatCheveux) {
            this.etatCheveux = etatCheveux;
            return this;
        }

        public Builder codeProfil(String codeProfil) {
            this.codeProfil = codeProfil;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public Builder dateMiseAJour(LocalDateTime dateMiseAJour) {
            this.dateMiseAJour = dateMiseAJour;
            return this;
        }

        public ProfilCapillaire build() {
            return new ProfilCapillaire(
                    id, compte, typeCheveux, texture, porosite, longueur,
                    densite, cuirChevelu, etatCheveux, codeProfil, dateCreation, dateMiseAJour);
        }
    }

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        this.dateMiseAJour = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dateMiseAJour = LocalDateTime.now();
    }
}
