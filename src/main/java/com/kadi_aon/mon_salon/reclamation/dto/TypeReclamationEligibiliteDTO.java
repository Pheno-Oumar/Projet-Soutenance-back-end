package com.kadi_aon.mon_salon.reclamation.dto;

import com.kadi_aon.mon_salon.reclamation.enums.TypeReclamation;

public record TypeReclamationEligibiliteDTO(
        TypeReclamation type,
        String libelle,
        boolean eligible,
        String motifIneligibilite
) {}
