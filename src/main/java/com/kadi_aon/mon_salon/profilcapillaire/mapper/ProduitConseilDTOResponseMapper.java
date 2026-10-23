package com.kadi_aon.mon_salon.profilcapillaire.mapper;

import java.util.HashSet;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.entity.ProduitConseilCapillaire;

@Component
public class ProduitConseilDTOResponseMapper implements Function<ProduitConseilCapillaire, ProduitConseilDTOResponse> {

    @Override
    public ProduitConseilDTOResponse apply(ProduitConseilCapillaire p) {
        if (p == null) {
            return null;
        }

        return new ProduitConseilDTOResponse(
                p.getId(),
                p.getNom(),
                p.getMarque(),
                p.getDescription(),
                p.getModeUtilisation(),
                p.getImageUrl(),
                p.getEtapeRoutine(),
                p.getCuirsChevelusCibles() != null ? new HashSet<>(p.getCuirsChevelusCibles()) : new HashSet<>(),
                p.getEtatsCheveuxCibles() != null ? new HashSet<>(p.getEtatsCheveuxCibles()) : new HashSet<>(),
                p.getPorositesCibles() != null ? new HashSet<>(p.getPorositesCibles()) : new HashSet<>(),
                p.getTypesCheveuxCibles() != null ? new HashSet<>(p.getTypesCheveuxCibles()) : new HashSet<>(),
                p.getTexturesCibles() != null ? new HashSet<>(p.getTexturesCibles()) : new HashSet<>(),
                p.getAlternativeNaturelleNom(),
                p.getAlternativeNaturelleIngredients(),
                p.getAlternativeNaturelleRecette(),
                p.isActif(),
                p.getDateCreation(),
                p.getDateModification()
        );
    }
}
