package com.kadi_aon.mon_salon.salon.entity;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "salons")
@Getter
@Setter
public class Salon {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du salon est obligatoire")
    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 255)
    private String logoUrl;

    @Column(name = "cover_url", length = 1000)
    private String coverUrl;

    @NotBlank(message = "Le slug du salon est obligatoire")
    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String adresse;

    @Column(length = 30)
    private String telephone;

    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point localisation;

    @Column(nullable = false)
    private Boolean statut = true;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    public Salon() {
    }

    public Salon(Long id, String nom, String logoUrl, String coverUrl, String slug, String description,
                 String adresse, String telephone, String email, Point localisation, Boolean statut, LocalDateTime dateCreation) {
        this.id = id;
        this.nom = nom;
        this.logoUrl = logoUrl;
        this.coverUrl = coverUrl;
        this.slug = slug;
        this.description = description;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.localisation = localisation;
        this.statut = statut != null ? statut : true;
        this.dateCreation = dateCreation;
    }

    public static SalonBuilder builder() {
        return new SalonBuilder();
    }

    public static class SalonBuilder {
        private Long id;
        private String nom;
        private String logoUrl;
        private String coverUrl;
        private String slug;
        private String description;
        private String adresse;
        private String telephone;
        private String email;
        private Point localisation;
        private Boolean statut = true;
        private LocalDateTime dateCreation;

        SalonBuilder() {
        }

        public SalonBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SalonBuilder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public SalonBuilder logoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        public SalonBuilder coverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
            return this;
        }

        public SalonBuilder slug(String slug) {
            this.slug = slug;
            return this;
        }

        public SalonBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SalonBuilder adresse(String adresse) {
            this.adresse = adresse;
            return this;
        }

        public SalonBuilder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public SalonBuilder email(String email) {
            this.email = email;
            return this;
        }

        public SalonBuilder localisation(Point localisation) {
            this.localisation = localisation;
            return this;
        }

        public SalonBuilder statut(Boolean statut) {
            this.statut = statut;
            return this;
        }

        public SalonBuilder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public Salon build() {
            return new Salon(id, nom, logoUrl, coverUrl, slug, description, adresse, telephone, email, localisation, statut, dateCreation);
        }
    }

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = true;
        }
    }

    public Double getLatitude() {
        return (this.localisation != null) ? this.localisation.getY() : null;
    }

    public Double getLongitude() {
        return (this.localisation != null) ? this.localisation.getX() : null;
    }

    public void setCoordonnees(Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            this.localisation = GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
        } else {
            this.localisation = null;
        }
    }
}
