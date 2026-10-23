package com.kadi_aon.mon_salon.rendezvous.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record CreneauDisponibleDTOResponse(
                LocalTime heureDebut,
                LocalTime heureFin,
                Integer dureeTotale,
                BigDecimal montantTotal,
                List<LigneCreneauDTO> lignesProposees,
                Long coiffeurId,
                String coiffeurNom) {

    public CreneauDisponibleDTOResponse(
            LocalTime heureDebut,
            LocalTime heureFin,
            Integer dureeTotale,
            BigDecimal montantTotal,
            List<LigneCreneauDTO> lignesProposees) {
        this(
                heureDebut,
                heureFin,
                dureeTotale,
                montantTotal,
                lignesProposees,
                (lignesProposees != null && !lignesProposees.isEmpty()) ? lignesProposees.get(0).coiffeurAffectationId() : null,
                (lignesProposees != null && !lignesProposees.isEmpty())
                        ? ((lignesProposees.get(0).coiffeurPrenom() != null ? lignesProposees.get(0).coiffeurPrenom() + " " : "")
                        + (lignesProposees.get(0).coiffeurNom() != null ? lignesProposees.get(0).coiffeurNom() : "")).trim()
                        : null
        );
    }
}
