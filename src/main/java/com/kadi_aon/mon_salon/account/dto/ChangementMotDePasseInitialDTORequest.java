package com.kadi_aon.mon_salon.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangementMotDePasseInitialDTORequest(
        @NotBlank(message = "Le mot de passe initial ou actuel est obligatoire")
        String ancienMotDePasse,

        @NotBlank(message = "Le nouveau mot de passe est obligatoire")
        @Size(min = 8, message = "Le nouveau mot de passe doit comporter au moins 8 caractères")
        String nouveauMotDePasse
) {}
