package com.kadi_aon.mon_salon.profilcapillaire.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;

public record ProduitConseilDTOResponse(
                Long id,
                String nom,
                String marque,
                String description,
                String modeUtilisation,
                String imageUrl,
                EtapeRoutine etapeRoutine,
                Set<CuirChevelu> cuirsChevelusCibles,
                Set<EtatCheveux> etatsCheveuxCibles,
                Set<PorositeCheveux> porositesCibles,
                Set<TypeCheveux> typesCheveuxCibles,
                Set<TextureCheveux> texturesCibles,
                String alternativeNaturelleNom,
                String alternativeNaturelleIngredients,
                String alternativeNaturelleRecette,
                boolean actif,
                LocalDateTime dateCreation,
                LocalDateTime dateModification) {

    public ProduitConseilDTOResponse(
            Long id,
            String nom,
            String marque,
            String description,
            String modeUtilisation,
            String imageUrl,
            EtapeRoutine etapeRoutine,
            Set<CuirChevelu> cuirsChevelusCibles,
            Set<EtatCheveux> etatsCheveuxCibles,
            Set<PorositeCheveux> porositesCibles,
            Set<TypeCheveux> typesCheveuxCibles,
            Set<TextureCheveux> texturesCibles,
            boolean actif,
            LocalDateTime dateCreation,
            LocalDateTime dateModification
    ) {
        this(id, nom, marque, description, modeUtilisation, imageUrl, etapeRoutine,
                cuirsChevelusCibles, etatsCheveuxCibles, porositesCibles,
                typesCheveuxCibles, texturesCibles, null, null, null,
                actif, dateCreation, dateModification);
    }
}
