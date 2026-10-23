package com.kadi_aon.mon_salon.salon.dto.pilotage;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientSalonResumeDTOResponse {

    private Long clientId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;
    private LocalDateTime dateInscription;
    private Boolean hasProfilCapillaire;

    public ClientSalonResumeDTOResponse() {
    }

    public ClientSalonResumeDTOResponse(Long clientId, String nom, String prenom, String email, String telephone,
            LocalDate dateNaissance, LocalDateTime dateInscription, Boolean hasProfilCapillaire) {
        this.clientId = clientId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.dateInscription = dateInscription;
        this.hasProfilCapillaire = hasProfilCapillaire;
    }
}
