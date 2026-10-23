package com.kadi_aon.mon_salon.salon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TransfertProprieteDTORequest(
        @NotBlank(message = "L'identifiant (téléphone ou email) du nouveau propriétaire est obligatoire.")
        String nouvelEmailProprietaire,

        @NotBlank(message = "Le mot de passe actuel est requis pour confirmer le transfert.")
        String motDePasseConfirmation
) {}
