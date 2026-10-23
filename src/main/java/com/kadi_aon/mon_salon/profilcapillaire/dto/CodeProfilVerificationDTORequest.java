package com.kadi_aon.mon_salon.profilcapillaire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CodeProfilVerificationDTORequest(
        @NotBlank(message = "Le code PIN est obligatoire pour consulter le profil capillaire")
        @Pattern(regexp = "^[0-9]{4,6}$", message = "Le code PIN doit être composé de 4 à 6 chiffres")
        String codePin
) {
}
