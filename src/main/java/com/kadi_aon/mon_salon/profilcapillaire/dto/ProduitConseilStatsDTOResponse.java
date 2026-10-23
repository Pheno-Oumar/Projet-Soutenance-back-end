package com.kadi_aon.mon_salon.profilcapillaire.dto;

import java.util.Map;

import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;

public record ProduitConseilStatsDTOResponse(
        long total,
        long actifs,
        long inactifs,
        Map<EtapeRoutine, Long> parEtape
) {
}
