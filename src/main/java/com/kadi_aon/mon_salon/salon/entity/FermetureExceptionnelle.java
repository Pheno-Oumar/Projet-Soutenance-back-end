package com.kadi_aon.mon_salon.salon.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "fermetures_exceptionnelles")
@Getter
@Setter
public class FermetureExceptionnelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false, length = 255)
    private String motif;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    public FermetureExceptionnelle() {
        super();
    }

    public FermetureExceptionnelle(Long id, Salon salon, LocalDate dateDebut, LocalDate dateFin,
                                   String motif, LocalDateTime dateCreation) {
        super();
        this.id = id;
        this.salon = salon;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.motif = motif;
        this.dateCreation = dateCreation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Salon salon;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String motif;
        private LocalDateTime dateCreation;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder dateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;
            return this;
        }

        public Builder dateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
            return this;
        }

        public Builder motif(String motif) {
            this.motif = motif;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public FermetureExceptionnelle build() {
            return new FermetureExceptionnelle(id, salon, dateDebut, dateFin, motif, dateCreation);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
    }
}
