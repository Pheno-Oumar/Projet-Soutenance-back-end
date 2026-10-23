package com.kadi_aon.mon_salon.salon.dto;

import java.math.BigDecimal;
import java.util.List;

public record VarianteServiceDTOResponse(
        Long id,
        String nom,
        String description,
        Integer dureeMinutes,
        BigDecimal prix,
        String imageUrl,
        List<String> imagesUrls,
        Boolean statut
) {
    public VarianteServiceDTOResponse(
            Long id,
            String nom,
            Integer dureeMinutes,
            BigDecimal prix,
            String imageUrl,
            List<String> imagesUrls,
            Boolean statut
    ) {
        this(id, nom, null, dureeMinutes, prix, imageUrl, imagesUrls, statut);
    }

    public VarianteServiceDTOResponse(
            Long id,
            String nom,
            Integer dureeMinutes,
            BigDecimal prix,
            String imageUrl,
            Boolean statut
    ) {
        this(id, nom, null, dureeMinutes, prix, imageUrl,
             imageUrl != null ? List.of(imageUrl) : List.of(),
             statut);
    }

    public VarianteServiceDTOResponse(
            Long id,
            String nom,
            Integer dureeMinutes,
            BigDecimal prix,
            Boolean statut
    ) {
        this(id, nom, null, dureeMinutes, prix, null, List.of(), statut);
    }
}
