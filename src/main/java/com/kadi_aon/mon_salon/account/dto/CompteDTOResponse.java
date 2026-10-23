package com.kadi_aon.mon_salon.account.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CompteDTOResponse(
        Long id,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String email,
        String telephone,
        String pays,
        Boolean statut,
        String rolePlateforme,
        LocalDateTime dateCreation
) {
    public CompteDTOResponse(
            Long id,
            String nom,
            String prenom,
            LocalDate dateNaissance,
            String email,
            String telephone,
            Boolean statut,
            String rolePlateforme,
            LocalDateTime dateCreation) {
        this(id, nom, prenom, dateNaissance, email, telephone, null, statut, rolePlateforme, dateCreation);
    }
}
