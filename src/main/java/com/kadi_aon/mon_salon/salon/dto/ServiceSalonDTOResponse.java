package com.kadi_aon.mon_salon.salon.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ServiceSalonDTOResponse(
        Long id,
        String nom,
        String description,
        String imageUrl,
        Boolean statut,
        List<VarianteServiceDTOResponse> variantes,
        LocalDateTime dateCreation,
        String slugSalon,
        String salonNom
) {
    public ServiceSalonDTOResponse(
            Long id,
            String nom,
            String description,
            String imageUrl,
            Boolean statut,
            List<VarianteServiceDTOResponse> variantes,
            LocalDateTime dateCreation
    ) {
        this(id, nom, description, imageUrl, statut, variantes, dateCreation, null, null);
    }

    public ServiceSalonDTOResponse(
            Long id,
            String nom,
            String description,
            Boolean statut,
            List<VarianteServiceDTOResponse> variantes,
            LocalDateTime dateCreation
    ) {
        this(id, nom, description, null, statut, variantes, dateCreation, null, null);
    }
}

