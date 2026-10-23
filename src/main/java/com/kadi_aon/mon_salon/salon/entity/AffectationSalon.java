package com.kadi_aon.mon_salon.salon.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.kadi_aon.mon_salon.account.entity.Compte;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "affectations_salon")
@Getter
@Setter
public class AffectationSalon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "affectation_roles",
            joinColumns = @JoinColumn(name = "affectation_id"),
            inverseJoinColumns = @JoinColumn(name = "role_salon_id")
    )
    private Set<RoleSalon> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean statut = true;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    public AffectationSalon() {
    }

    public AffectationSalon(Long id, Compte compte, Salon salon, Set<RoleSalon> roles, Boolean statut, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.compte = compte;
        this.salon = salon;
        this.roles = roles != null ? roles : new HashSet<>();
        this.statut = statut != null ? statut : true;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public static AffectationSalonBuilder builder() {
        return new AffectationSalonBuilder();
    }

    public static class AffectationSalonBuilder {
        private Long id;
        private Compte compte;
        private Salon salon;
        private Set<RoleSalon> roles = new HashSet<>();
        private Boolean statut = true;
        private LocalDate dateDebut;
        private LocalDate dateFin;

        AffectationSalonBuilder() {
        }

        public AffectationSalonBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AffectationSalonBuilder compte(Compte compte) {
            this.compte = compte;
            return this;
        }

        public AffectationSalonBuilder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public AffectationSalonBuilder roles(Set<RoleSalon> roles) {
            this.roles = roles != null ? roles : new HashSet<>();
            return this;
        }

        public AffectationSalonBuilder statut(Boolean statut) {
            this.statut = statut;
            return this;
        }

        public AffectationSalonBuilder dateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;
            return this;
        }

        public AffectationSalonBuilder dateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
            return this;
        }

        public AffectationSalon build() {
            return new AffectationSalon(id, compte, salon, roles, statut, dateDebut, dateFin);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateDebut == null) {
            this.dateDebut = LocalDate.now();
        }
        if (this.statut == null) {
            this.statut = true;
        }
    }
}
