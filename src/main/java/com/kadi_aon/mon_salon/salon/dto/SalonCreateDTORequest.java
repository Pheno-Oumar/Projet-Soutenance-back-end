package com.kadi_aon.mon_salon.salon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SalonCreateDTORequest(
        @NotBlank(message = "Le nom du salon est obligatoire")
        String nom,

        @Email(message = "Format d'email du propriétaire invalide")
        String emailProprietaire,

        @NotBlank(message = "Le numéro de téléphone du propriétaire est obligatoire")
        @Size(max = 30, message = "Le numéro de téléphone ne doit pas dépasser 30 caractères")
        String telephoneProprietaire,

        @NotBlank(message = "Le pays est obligatoire")
        String pays
) {
    public SalonCreateDTORequest(String nom, String emailProprietaire, String telephoneProprietaire) {
        this(nom, emailProprietaire, telephoneProprietaire, "Côte d'Ivoire");
    }

    public SalonCreateDTORequest(String nom, String telephoneProprietaire) {
        this(nom, null, telephoneProprietaire, "Côte d'Ivoire");
    }
}
