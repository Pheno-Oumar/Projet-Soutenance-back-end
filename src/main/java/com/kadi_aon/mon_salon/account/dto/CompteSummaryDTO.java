package com.kadi_aon.mon_salon.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompteSummaryDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String pays;
    private String rolePlateforme;
    private Boolean doitChangerMotDePasse;

    public CompteSummaryDTO() {
        super();
    }

    public CompteSummaryDTO(
            Long id,
            String nom,
            String prenom,
            String email,
            String telephone,
            String pays,
            String rolePlateforme,
            Boolean doitChangerMotDePasse) {
        super();
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.pays = pays;
        this.rolePlateforme = rolePlateforme;
        this.doitChangerMotDePasse = doitChangerMotDePasse;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String pays;
        private String rolePlateforme;
        private Boolean doitChangerMotDePasse;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder prenom(String prenom) {
            this.prenom = prenom;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public Builder pays(String pays) {
            this.pays = pays;
            return this;
        }

        public Builder rolePlateforme(String rolePlateforme) {
            this.rolePlateforme = rolePlateforme;
            return this;
        }

        public Builder doitChangerMotDePasse(Boolean doitChangerMotDePasse) {
            this.doitChangerMotDePasse = doitChangerMotDePasse;
            return this;
        }

        public CompteSummaryDTO build() {
            return new CompteSummaryDTO(id, nom, prenom, email, telephone, pays, rolePlateforme, doitChangerMotDePasse);
        }
    }
}
