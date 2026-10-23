package com.kadi_aon.mon_salon.salon.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRegisterDTORequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        String prenom,

        @Email(message = "Format d'adresse email invalide")
        String email,

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        String telephone,

        String pays,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit comporter au moins 6 caractères")
        String password,

        LocalDate dateNaissance
) {
    public ClientRegisterDTORequest(
            String nom,
            String prenom,
            String email,
            String telephone,
            String password,
            LocalDate dateNaissance) {
        this(nom, prenom, email, telephone, null, password, dateNaissance);
    }
}
