package com.kadi_aon.mon_salon.account.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kadi_aon.mon_salon.common.deserializer.FlexibleLocalDateDeserializer;

import jakarta.validation.constraints.NotBlank;

public record CompteUpdateDTORequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        String prenom,

        @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
        LocalDate dateNaissance,

        @NotBlank(message = "Le téléphone est obligatoire")
        String telephone,

        String pays,

        String email
) {
    public CompteUpdateDTORequest(
            String nom,
            String prenom,
            LocalDate dateNaissance,
            String telephone,
            String pays) {
        this(nom, prenom, dateNaissance, telephone, pays, null);
    }

    public CompteUpdateDTORequest(
            String nom,
            String prenom,
            LocalDate dateNaissance,
            String telephone) {
        this(nom, prenom, dateNaissance, telephone, null, null);
    }
}
