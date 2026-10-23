package com.kadi_aon.mon_salon.salon.dto;

import java.util.Set;

import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record EmployeCreateDTORequest(
        @NotBlank(message = "Le nom de l'employé est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom de l'employé est obligatoire")
        String prenom,

        @Email(message = "Format d'email invalide")
        String email,

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        @Size(max = 30, message = "Le numéro de téléphone ne doit pas dépasser 30 caractères")
        String telephone,

        @NotBlank(message = "Le pays est obligatoire")
        String pays,

        @NotEmpty(message = "Au moins un rôle salon doit être attribué à l'employé")
        Set<TypeRoleSalon> roles
) {
    public EmployeCreateDTORequest(String nom, String prenom, String email, String telephone, Set<TypeRoleSalon> roles) {
        this(nom, prenom, email, telephone, "Côte d'Ivoire", roles);
    }
}
