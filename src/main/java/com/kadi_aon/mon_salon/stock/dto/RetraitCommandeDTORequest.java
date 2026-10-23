package com.kadi_aon.mon_salon.stock.dto;

import com.kadi_aon.mon_salon.facturation.enums.MoyenPaiement;
import jakarta.validation.constraints.NotBlank;

public record RetraitCommandeDTORequest(
        @NotBlank(message = "Le code de retrait est obligatoire.")
        String codeRetrait,
        MoyenPaiement moyenPaiement
) {
    public RetraitCommandeDTORequest(String codeRetrait) {
        this(codeRetrait, MoyenPaiement.ESPECES);
    }
}
