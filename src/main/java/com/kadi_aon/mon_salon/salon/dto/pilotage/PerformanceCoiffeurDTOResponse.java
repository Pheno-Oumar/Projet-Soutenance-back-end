package com.kadi_aon.mon_salon.salon.dto.pilotage;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerformanceCoiffeurDTOResponse {

    private Long coiffeurId;
    private String nom;
    private String prenom;
    private String email;
    private long nombrePrestations;
    private BigDecimal chiffreAffaires;

    public PerformanceCoiffeurDTOResponse() {
    }

    public PerformanceCoiffeurDTOResponse(Long coiffeurId, String nom, String prenom, String email,
            long nombrePrestations, BigDecimal chiffreAffaires) {
        this.coiffeurId = coiffeurId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.nombrePrestations = nombrePrestations;
        this.chiffreAffaires = chiffreAffaires;
    }
}
