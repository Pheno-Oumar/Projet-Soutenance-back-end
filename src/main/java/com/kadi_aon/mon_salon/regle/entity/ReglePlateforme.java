package com.kadi_aon.mon_salon.regle.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "regles_plateforme")
@Getter
@Setter
public class ReglePlateforme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre de la règle est obligatoire")
    @Column(nullable = false, length = 200)
    private String titre;

    @NotBlank(message = "La description de la règle est obligatoire")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public ReglePlateforme() {
        super();
    }

    public ReglePlateforme(Long id, String titre, String description, Boolean actif, LocalDateTime dateCreation, LocalDateTime dateModification) {
        super();
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.actif = actif != null ? actif : true;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String titre;
        private String description;
        private Boolean actif = true;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder titre(String titre) {
            this.titre = titre;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder actif(Boolean actif) {
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

        public ReglePlateforme build() {
            return new ReglePlateforme(id, titre, description, actif, dateCreation, dateModification);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.actif == null) {
            this.actif = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}
