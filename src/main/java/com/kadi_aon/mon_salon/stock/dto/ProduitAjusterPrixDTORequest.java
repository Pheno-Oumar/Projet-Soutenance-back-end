package com.kadi_aon.mon_salon.stock.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProduitAjusterPrixDTORequest(
        @NotNull(message = "Le nouveau prix de vente est obligatoire.")
        @Positive(message = "Le nouveau prix de vente doit être strictement positif.")
        BigDecimal nouveauPrixVente
) {}
