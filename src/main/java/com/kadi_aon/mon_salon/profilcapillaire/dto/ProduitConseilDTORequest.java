package com.kadi_aon.mon_salon.profilcapillaire.dto;

import java.util.Set;

import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProduitConseilDTORequest(
                @NotBlank(message = "Le nom du produit est obligatoire") @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères") String nom,

                @Size(max = 100, message = "La marque ne doit pas dépasser 100 caractères") String marque,

                @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères") String description,

                @Size(max = 1000, message = "Le mode d'utilisation ne doit pas dépasser 1000 caractères") String modeUtilisation,

                @Size(max = 1000, message = "L'URL de l'image ne doit pas dépasser 1000 caractères") String imageUrl,

                @NotNull(message = "L'étape de la routine est obligatoire") EtapeRoutine etapeRoutine,

                Set<CuirChevelu> cuirsChevelusCibles,
                Set<EtatCheveux> etatsCheveuxCibles,
                Set<PorositeCheveux> porositesCibles,
                Set<TypeCheveux> typesCheveuxCibles,
                Set<TextureCheveux> texturesCibles,
                @Size(max = 150, message = "Le nom de l'alternative naturelle ne doit pas dépasser 150 caractères") String alternativeNaturelleNom,
                @Size(max = 500, message = "Les ingrédients de l'alternative naturelle ne doivent pas dépasser 500 caractères") String alternativeNaturelleIngredients,
                @Size(max = 1000, message = "La recette de l'alternative naturelle ne doit pas dépasser 1000 caractères") String alternativeNaturelleRecette,
                Boolean actif) {

    public ProduitConseilDTORequest(
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
            Boolean actif
    ) {
        this(nom, marque, description, modeUtilisation, imageUrl, etapeRoutine,
                cuirsChevelusCibles, etatsCheveuxCibles, porositesCibles,
                typesCheveuxCibles, texturesCibles, null, null, null, actif);
    }
}
