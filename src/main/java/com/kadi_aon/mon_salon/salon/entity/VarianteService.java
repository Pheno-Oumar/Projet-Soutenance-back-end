package com.kadi_aon.mon_salon.salon.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "variantes_service", uniqueConstraints = {
        @UniqueConstraint(name = "uk_variante_service_nom", columnNames = {"service_salon_id", "nom"})
})
@Getter
@Setter
public class VarianteService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_salon_id", nullable = false)
    private ServiceSalon serviceSalon;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Positive(message = "La durée doit être strictement positive")
    @Column(name = "duree_minutes", nullable = false)
    private Integer dureeMinutes;

    @PositiveOrZero(message = "Le prix doit être positif ou nul")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(nullable = false)
    private Boolean statut = true;

    @ElementCollection
    @CollectionTable(name = "variante_service_images", joinColumns = @JoinColumn(name = "variante_service_id"))
    @Column(name = "image_url", length = 1000)
    @OrderColumn(name = "ordre")
    private List<String> imagesUrls = new ArrayList<>();

    public String getImageUrl() {
        return (imagesUrls != null && !imagesUrls.isEmpty()) ? imagesUrls.get(0) : null;
    }

    public void setImageUrl(String url) {
        if (this.imagesUrls == null) {
            this.imagesUrls = new ArrayList<>();
        }
        if (url != null && !url.isBlank()) {
            if (this.imagesUrls.isEmpty()) {
                this.imagesUrls.add(url);
            } else {
                this.imagesUrls.set(0, url);
            }
        }
    }

    public VarianteService() {
        super();
    }

    public VarianteService(Long id, ServiceSalon serviceSalon, String nom, String description, Integer dureeMinutes,
                           BigDecimal prix, Boolean statut, List<String> imagesUrls) {
        super();
        this.id = id;
        this.serviceSalon = serviceSalon;
        this.nom = nom;
        this.description = description;
        this.dureeMinutes = dureeMinutes;
        this.prix = prix;
        this.statut = statut != null ? statut : true;
        this.imagesUrls = imagesUrls != null ? imagesUrls : new ArrayList<>();
    }

    public VarianteService(Long id, ServiceSalon serviceSalon, String nom, Integer dureeMinutes,
                           BigDecimal prix, Boolean statut, List<String> imagesUrls) {
        this(id, serviceSalon, nom, null, dureeMinutes, prix, statut, imagesUrls);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private ServiceSalon serviceSalon;
        private String nom;
        private String description;
        private Integer dureeMinutes;
        private BigDecimal prix;
        private Boolean statut = true;
        private List<String> imagesUrls = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder serviceSalon(ServiceSalon serviceSalon) {
            this.serviceSalon = serviceSalon;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder dureeMinutes(Integer dureeMinutes) {
            this.dureeMinutes = dureeMinutes;
            return this;
        }

        public Builder prix(BigDecimal prix) {
            this.prix = prix;
            return this;
        }

        public Builder statut(Boolean statut) {
            this.statut = statut;
            return this;
        }

        public Builder imagesUrls(List<String> imagesUrls) {
            this.imagesUrls = imagesUrls != null ? imagesUrls : new ArrayList<>();
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            if (imageUrl != null && !imageUrl.isBlank()) {
                this.imagesUrls = new ArrayList<>(List.of(imageUrl));
            }
            return this;
        }

        public VarianteService build() {
            return new VarianteService(id, serviceSalon, nom, description, dureeMinutes, prix, statut, imagesUrls);
        }
    }
}
