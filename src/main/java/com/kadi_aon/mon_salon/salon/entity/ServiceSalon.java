package com.kadi_aon.mon_salon.salon.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "services_salon", uniqueConstraints = {
        @UniqueConstraint(name = "uk_service_salon_nom", columnNames = {"salon_id", "nom"})
})
@Getter
@Setter
public class ServiceSalon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean statut = true;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @OneToMany(mappedBy = "serviceSalon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VarianteService> variantes = new ArrayList<>();

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    public ServiceSalon() {
        super();
    }

    public ServiceSalon(Long id, Salon salon, String nom, String description, Boolean statut,
                        String imageUrl, List<VarianteService> variantes, LocalDateTime dateCreation) {
        super();
        this.id = id;
        this.salon = salon;
        this.nom = nom;
        this.description = description;
        this.statut = statut != null ? statut : true;
        this.imageUrl = imageUrl;
        this.variantes = variantes != null ? variantes : new ArrayList<>();
        this.dateCreation = dateCreation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Salon salon;
        private String nom;
        private String description;
        private Boolean statut = true;
        private String imageUrl;
        private List<VarianteService> variantes = new ArrayList<>();
        private LocalDateTime dateCreation;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
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

        public Builder statut(Boolean statut) {
            this.statut = statut;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder variantes(List<VarianteService> variantes) {
            this.variantes = variantes != null ? variantes : new ArrayList<>();
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public ServiceSalon build() {
            return new ServiceSalon(id, salon, nom, description, statut, imageUrl, variantes, dateCreation);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = true;
        }
    }

    public void addVariante(VarianteService variante) {
        variantes.add(variante);
        variante.setServiceSalon(this);
    }

    public void removeVariante(VarianteService variante) {
        variantes.remove(variante);
        variante.setServiceSalon(null);
    }
}
