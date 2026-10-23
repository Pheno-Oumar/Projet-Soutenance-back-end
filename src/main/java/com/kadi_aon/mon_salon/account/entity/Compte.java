package com.kadi_aon.mon_salon.account.entity;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comptes")
@Getter
@Setter
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false, length = 100)
    private String prenom;

    private LocalDate dateNaissance;

    @Email(message = "Le format de l'email est invalide")
    @Column(unique = true, length = 150)
    private String email;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Column(nullable = false, unique = true, length = 30)
    private String telephone;

    @Column(length = 100)
    private String pays;

    @Column(name = "doit_changer_mot_de_passe")
    private Boolean doitChangerMotDePasse = false;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean statut = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_plateforme_id")
    private RolePlateforme rolePlateforme;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    public Compte() {
    }

    public Compte(Long id, String nom, String prenom, LocalDate dateNaissance, String email, String telephone,
                  String pays, Boolean doitChangerMotDePasse, String password, Boolean statut,
                  RolePlateforme rolePlateforme, LocalDateTime dateCreation, LocalDateTime dateModification) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.telephone = telephone;
        this.pays = pays;
        this.doitChangerMotDePasse = doitChangerMotDePasse != null ? doitChangerMotDePasse : false;
        this.password = password;
        this.statut = statut != null ? statut : true;
        this.rolePlateforme = rolePlateforme;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    public static CompteBuilder builder() {
        return new CompteBuilder();
    }

    public static class CompteBuilder {
        private Long id;
        private String nom;
        private String prenom;
        private LocalDate dateNaissance;
        private String email;
        private String telephone;
        private String pays;
        private Boolean doitChangerMotDePasse = false;
        private String password;
        private Boolean statut = true;
        private RolePlateforme rolePlateforme;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;

        CompteBuilder() {
        }

        public CompteBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CompteBuilder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public CompteBuilder prenom(String prenom) {
            this.prenom = prenom;
            return this;
        }

        public CompteBuilder dateNaissance(LocalDate dateNaissance) {
            this.dateNaissance = dateNaissance;
            return this;
        }

        public CompteBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CompteBuilder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public CompteBuilder pays(String pays) {
            this.pays = pays;
            return this;
        }

        public CompteBuilder doitChangerMotDePasse(Boolean doitChangerMotDePasse) {
            this.doitChangerMotDePasse = doitChangerMotDePasse;
            return this;
        }

        public CompteBuilder password(String password) {
            this.password = password;
            return this;
        }

        public CompteBuilder statut(Boolean statut) {
            this.statut = statut;
            return this;
        }

        public CompteBuilder rolePlateforme(RolePlateforme rolePlateforme) {
            this.rolePlateforme = rolePlateforme;
            return this;
        }

        public CompteBuilder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public CompteBuilder dateModification(LocalDateTime dateModification) {
            this.dateModification = dateModification;
            return this;
        }

        public Compte build() {
            return new Compte(id, nom, prenom, dateNaissance, email, telephone, pays, doitChangerMotDePasse,
                    password, statut, rolePlateforme, dateCreation, dateModification);
        }
    }

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public String getIdentifiant() {
        return (telephone != null && !telephone.isBlank()) ? telephone : email;
    }
}
