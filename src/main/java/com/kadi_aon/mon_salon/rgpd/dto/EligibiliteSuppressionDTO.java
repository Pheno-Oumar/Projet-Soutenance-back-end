package com.kadi_aon.mon_salon.rgpd.dto;

import java.util.List;

public record EligibiliteSuppressionDTO(
        boolean eligible,
        List<String> motifsIneligibilite,
        boolean demandeEnCours
) {
}
