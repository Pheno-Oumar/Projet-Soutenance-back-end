package com.kadi_aon.mon_salon.profilcapillaire.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "produits_conseils_capillaires")
@Getter
@Setter
public class ProduitConseilCapillaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit de conseil est obligatoire")
    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 100)
    private String marque;

    @Column(length = 1000)
    private String description;

    @Column(name = "mode_utilisation", length = 1000)
    private String modeUtilisation;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "etape_routine", nullable = false, length = 50)
    private EtapeRoutine etapeRoutine;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produit_conseil_cuirs_chevelus", joinColumns = @JoinColumn(name = "produit_conseil_id"))
    @Column(name = "cuir_chevelu", length = 50)
    @Enumerated(EnumType.STRING)
    private Set<CuirChevelu> cuirsChevelusCibles = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produit_conseil_etats_cheveux", joinColumns = @JoinColumn(name = "produit_conseil_id"))
    @Column(name = "etat_cheveux", length = 50)
    @Enumerated(EnumType.STRING)
    private Set<EtatCheveux> etatsCheveuxCibles = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produit_conseil_porosites", joinColumns = @JoinColumn(name = "produit_conseil_id"))
    @Column(name = "porosite", length = 50)
    @Enumerated(EnumType.STRING)
    private Set<PorositeCheveux> porositesCibles = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produit_conseil_types_cheveux", joinColumns = @JoinColumn(name = "produit_conseil_id"))
    @Column(name = "type_cheveux", length = 50)
    @Enumerated(EnumType.STRING)
    private Set<TypeCheveux> typesCheveuxCibles = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produit_conseil_textures", joinColumns = @JoinColumn(name = "produit_conseil_id"))
    @Column(name = "texture", length = 50)
    @Enumerated(EnumType.STRING)
    private Set<TextureCheveux> texturesCibles = new HashSet<>();

    @Column(name = "alternative_naturelle_nom", length = 150)
    private String alternativeNaturelleNom;

    @Column(name = "alternative_naturelle_ingredients", length = 500)
    private String alternativeNaturelleIngredients;

    @Column(name = "alternative_naturelle_recette", length = 1000)
    private String alternativeNaturelleRecette;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public ProduitConseilCapillaire() {
        super();
        this.cuirsChevelusCibles = new HashSet<>();
        this.etatsCheveuxCibles = new HashSet<>();
        this.porositesCibles = new HashSet<>();
        this.typesCheveuxCibles = new HashSet<>();
        this.texturesCibles = new HashSet<>();
        this.actif = true;
    }

    public ProduitConseilCapillaire(
            Long id,
            String nom,
            String marque,
            String description,
            String modeUtilisation,
            String imageUrl,
            EtapeRoutine etapeRoutine,
            Set<CuirChevelu> cuirsChevelusCibles,
            Set<EtatCheveux> etatsCheveuxCibles,
            Set<PorositeCheveux> porositesCibles,
            Set<TypeCheveux> typesCheveuxCibles,
            Set<TextureCheveux> texturesCibles,
            String alternativeNaturelleNom,
            String alternativeNaturelleIngredients,
            String alternativeNaturelleRecette,
            boolean actif,
            LocalDateTime dateCreation,
            LocalDateTime dateModification) {
        super();
        this.id = id;
        this.nom = nom;
        this.marque = marque;
        this.description = description;
        this.modeUtilisation = modeUtilisation;
        this.imageUrl = imageUrl;
        this.etapeRoutine = etapeRoutine;
        this.cuirsChevelusCibles = (cuirsChevelusCibles != null) ? cuirsChevelusCibles : new HashSet<>();
        this.etatsCheveuxCibles = (etatsCheveuxCibles != null) ? etatsCheveuxCibles : new HashSet<>();
        this.porositesCibles = (porositesCibles != null) ? porositesCibles : new HashSet<>();
        this.typesCheveuxCibles = (typesCheveuxCibles != null) ? typesCheveuxCibles : new HashSet<>();
        this.texturesCibles = (texturesCibles != null) ? texturesCibles : new HashSet<>();
        this.alternativeNaturelleNom = alternativeNaturelleNom;
        this.alternativeNaturelleIngredients = alternativeNaturelleIngredients;
        this.alternativeNaturelleRecette = alternativeNaturelleRecette;
        this.actif = actif;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private String marque;
        private String description;
        private String modeUtilisation;
        private String imageUrl;
        private EtapeRoutine etapeRoutine;
        private Set<CuirChevelu> cuirsChevelusCibles = new HashSet<>();
        private Set<EtatCheveux> etatsCheveuxCibles = new HashSet<>();
        private Set<PorositeCheveux> porositesCibles = new HashSet<>();
        private Set<TypeCheveux> typesCheveuxCibles = new HashSet<>();
        private Set<TextureCheveux> texturesCibles = new HashSet<>();
        private String alternativeNaturelleNom;
        private String alternativeNaturelleIngredients;
        private String alternativeNaturelleRecette;
        private boolean actif = true;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder marque(String marque) {
            this.marque = marque;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder modeUtilisation(String modeUtilisation) {
            this.modeUtilisation = modeUtilisation;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder etapeRoutine(EtapeRoutine etapeRoutine) {
            this.etapeRoutine = etapeRoutine;
            return this;
        }

        public Builder cuirsChevelusCibles(Set<CuirChevelu> cuirsChevelusCibles) {
            this.cuirsChevelusCibles = cuirsChevelusCibles;
            return this;
        }

        public Builder etatsCheveuxCibles(Set<EtatCheveux> etatsCheveuxCibles) {
            this.etatsCheveuxCibles = etatsCheveuxCibles;
            return this;
        }

        public Builder porositesCibles(Set<PorositeCheveux> porositesCibles) {
            this.porositesCibles = porositesCibles;
            return this;
        }

        public Builder typesCheveuxCibles(Set<TypeCheveux> typesCheveuxCibles) {
            this.typesCheveuxCibles = typesCheveuxCibles;
            return this;
        }

        public Builder texturesCibles(Set<TextureCheveux> texturesCibles) {
            this.texturesCibles = texturesCibles;
            return this;
        }

        public Builder alternativeNaturelleNom(String alternativeNaturelleNom) {
            this.alternativeNaturelleNom = alternativeNaturelleNom;
            return this;
        }

        public Builder alternativeNaturelleIngredients(String alternativeNaturelleIngredients) {
            this.alternativeNaturelleIngredients = alternativeNaturelleIngredients;
            return this;
        }

        public Builder alternativeNaturelleRecette(String alternativeNaturelleRecette) {
            this.alternativeNaturelleRecette = alternativeNaturelleRecette;
            return this;
        }

        public Builder actif(boolean actif) {
            this.actif = actif;
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

        public ProduitConseilCapillaire build() {
            return new ProduitConseilCapillaire(
                    id, nom, marque, description, modeUtilisation, imageUrl, etapeRoutine,
                    cuirsChevelusCibles, etatsCheveuxCibles, porositesCibles, typesCheveuxCibles, texturesCibles,
                    alternativeNaturelleNom, alternativeNaturelleIngredients, alternativeNaturelleRecette,
                    actif, dateCreation, dateModification);
        }
    }

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}
