package com.kadi_aon.mon_salon.coiffeur.entity;

import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profils_coiffeur")
@Getter
@Setter
public class ProfilCoiffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affectation_id", nullable = false, unique = true)
    private AffectationSalon affectation;

    @Column(name = "nom_affichage", length = 100)
    private String nomAffichage;

    @Column(columnDefinition = "TEXT")
    private String biographie;

    @Column(name = "annee_experience")
    private Integer anneeExperience;

    @Column(name = "photo_profil_url")
    private String photoProfilUrl;

    @Column(length = 255)
    private String description;

    public ProfilCoiffeur() {
        super();
    }

    public ProfilCoiffeur(
            Long id,
            AffectationSalon affectation,
            String nomAffichage,
            String biographie,
            Integer anneeExperience,
            String photoProfilUrl,
            String description) {
        super();
        this.id = id;
        this.affectation = affectation;
        this.nomAffichage = nomAffichage;
        this.biographie = biographie;
        this.anneeExperience = anneeExperience;
        this.photoProfilUrl = photoProfilUrl;
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private AffectationSalon affectation;
        private String nomAffichage;
        private String biographie;
        private Integer anneeExperience;
        private String photoProfilUrl;
        private String description;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder affectation(AffectationSalon affectation) {
            this.affectation = affectation;
            return this;
        }

        public Builder nomAffichage(String nomAffichage) {
            this.nomAffichage = nomAffichage;
            return this;
        }

        public Builder biographie(String biographie) {
            this.biographie = biographie;
            return this;
        }

        public Builder anneeExperience(Integer anneeExperience) {
            this.anneeExperience = anneeExperience;
            return this;
        }

        public Builder photoProfilUrl(String photoProfilUrl) {
            this.photoProfilUrl = photoProfilUrl;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public ProfilCoiffeur build() {
            return new ProfilCoiffeur(id, affectation, nomAffichage, biographie, anneeExperience, photoProfilUrl, description);
        }
    }
}
