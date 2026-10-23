package com.kadi_aon.mon_salon.profilcapillaire.dto;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.DensiteCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.LongueurCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;

public record RecommandationCapillaireDTOResponse(
        Long id,
        String reference,
        LocalDateTime dateRecommandation,
        TypeCheveux typeCheveux,
        TextureCheveux texture,
        PorositeCheveux porosite,
        LongueurCheveux longueur,
        DensiteCheveux densite,
        CuirChevelu cuirChevelu,
        EtatCheveux etatCheveux,
        ProduitConseilDTOResponse produitLavage,
        ProduitConseilDTOResponse produitSoinProfond,
        ProduitConseilDTOResponse produitHydratation,
        ProduitConseilDTOResponse produitFinition,
        Integer scoreLavage,
        Integer scoreSoinProfond,
        Integer scoreHydratation,
        Integer scoreFinition,
        Integer scoreGlobal,
        String conseilsPersonnalises,
        Long joursRestantsAvantProchainDiagnostic
) {
}
