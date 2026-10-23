package com.kadi_aon.mon_salon.reclamation.dto;

import com.kadi_aon.mon_salon.reclamation.enums.TypeReclamation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReclamationCreateDTORequest(
        @NotBlank(message = "L'objet de la réclamation est obligatoire.")
        @Size(max = 255, message = "L'objet ne doit pas dépasser 255 caractères.")
        String objet,

        @NotBlank(message = "La description de la réclamation est obligatoire.")
        @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères.")
        String description,

        TypeReclamation type,
        String slugSalon
) {
    public ReclamationCreateDTORequest(String objet, String description, TypeReclamation type) {
        this(objet, description, type, null);
    }

    public ReclamationCreateDTORequest(String objet, String description) {
        this(objet, description, TypeReclamation.AUTRE, null);
    }

    public TypeReclamation typeSafe() {
        return type != null ? type : TypeReclamation.AUTRE;
    }
}
