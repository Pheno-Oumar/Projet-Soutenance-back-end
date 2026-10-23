package com.kadi_aon.mon_salon.profilcapillaire.dto;

import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.DensiteCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.LongueurCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;

public record ProfilCapillaireDTORequest(
        TypeCheveux typeCheveux,
        TextureCheveux texture,
        PorositeCheveux porosite,
        LongueurCheveux longueur,
        DensiteCheveux densite,
        CuirChevelu cuirChevelu,
        EtatCheveux etatCheveux
) {
    public static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public ProfilCapillaireDTORequest(
            String typeCheveux,
            String texture,
            String porosite,
            String longueur,
            String densite,
            String cuirChevelu,
            String etatCheveux
    ) {
        this(
                parseEnum(TypeCheveux.class, typeCheveux),
                parseEnum(TextureCheveux.class, texture),
                parseEnum(PorositeCheveux.class, porosite),
                parseEnum(LongueurCheveux.class, longueur),
                parseEnum(DensiteCheveux.class, densite),
                parseEnum(CuirChevelu.class, cuirChevelu),
                parseEnum(EtatCheveux.class, etatCheveux)
        );
    }
}
