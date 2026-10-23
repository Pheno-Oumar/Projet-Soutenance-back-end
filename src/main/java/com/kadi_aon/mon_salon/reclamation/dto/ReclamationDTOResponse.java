package com.kadi_aon.mon_salon.reclamation.dto;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.reclamation.enums.TypeReclamation;

public record ReclamationDTOResponse(
        Long id,
        String objet,
        String description,
        TypeReclamation type,
        String statut,
        String reponseTraitement,
        LocalDateTime dateCreation,
        LocalDateTime dateTraitement,
        String clientNomComplet,
        String clientEmail,
        String traiteParNomComplet,
        String salonSlug,
        String salonNom
) {
    public ReclamationDTOResponse(
            Long id,
            String objet,
            String description,
            TypeReclamation type,
            String statut,
            String reponseTraitement,
            LocalDateTime dateCreation,
            LocalDateTime dateTraitement,
            String clientNomComplet,
            String clientEmail,
            String traiteParNomComplet,
            String salonSlug
    ) {
        this(id, objet, description, type, statut, reponseTraitement, dateCreation, dateTraitement, clientNomComplet, clientEmail, traiteParNomComplet, salonSlug, null);
    }

    public ReclamationDTOResponse(
            Long id,
            String objet,
            String description,
            String statut,
            String reponseTraitement,
            LocalDateTime dateCreation,
            LocalDateTime dateTraitement,
            String clientNomComplet,
            String clientEmail,
            String traiteParNomComplet,
            String salonSlug
    ) {
        this(id, objet, description, TypeReclamation.AUTRE, statut, reponseTraitement, dateCreation, dateTraitement, clientNomComplet, clientEmail, traiteParNomComplet, salonSlug, null);
    }
}
