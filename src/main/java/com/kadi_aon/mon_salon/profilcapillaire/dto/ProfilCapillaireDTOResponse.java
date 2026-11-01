package com.kadi_aon.mon_salon.profilcapillaire.dto;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;

public record ProfilCapillaireDTOResponse(
        Long id,
        Long compteId,
        String nomClient,
        String prenomClient,
        TypeCheveux typeCheveux,
        TextureCheveux texture,
        PorositeCheveux porosite,
        CuirChevelu cuirChevelu,
        EtatCheveux etatCheveux,
        boolean hasCodeProfil,
        LocalDateTime dateCreation,
        LocalDateTime dateMiseAJour
) {
    private static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public ProfilCapillaireDTOResponse(
            Long id,
            Long compteId,
            String nomClient,
            String prenomClient,
            String typeCheveux,
            String texture,
            String porosite,
            String cuirChevelu,
            String etatCheveux,
            boolean hasCodeProfil,
            LocalDateTime dateCreation,
            LocalDateTime dateMiseAJour
    ) {
        this(
                id,
                compteId,
                nomClient,
                prenomClient,
                parseEnum(TypeCheveux.class, typeCheveux),
                parseEnum(TextureCheveux.class, texture),
                parseEnum(PorositeCheveux.class, porosite),
                parseEnum(CuirChevelu.class, cuirChevelu),
                parseEnum(EtatCheveux.class, etatCheveux),
                hasCodeProfil,
                dateCreation,
                dateMiseAJour
        );
    }
}
