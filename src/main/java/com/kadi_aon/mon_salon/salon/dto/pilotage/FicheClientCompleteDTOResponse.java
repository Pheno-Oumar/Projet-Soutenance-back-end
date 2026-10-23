package com.kadi_aon.mon_salon.salon.dto.pilotage;

import java.time.LocalDate;
import java.util.List;

import com.kadi_aon.mon_salon.facturation.dto.FactureDTOResponse;
import com.kadi_aon.mon_salon.facturation.dto.PaiementDTOResponse;
import com.kadi_aon.mon_salon.prestation.dto.PrestationDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProfilCapillaireDTOResponse;
import com.kadi_aon.mon_salon.rendezvous.dto.RendezVousDTOResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FicheClientCompleteDTOResponse {

    private Long clientId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;

    private ProfilCapillaireDTOResponse profilCapillaire;
    private List<RendezVousDTOResponse> rendezVous;
    private List<PrestationDTOResponse> prestations;
    private List<FactureDTOResponse> factures;
    private List<PaiementDTOResponse> paiements;

    public FicheClientCompleteDTOResponse() {
    }

    public FicheClientCompleteDTOResponse(Long clientId, String nom, String prenom, String email, String telephone,
            LocalDate dateNaissance, ProfilCapillaireDTOResponse profilCapillaire, List<RendezVousDTOResponse> rendezVous,
            List<PrestationDTOResponse> prestations, List<FactureDTOResponse> factures, List<PaiementDTOResponse> paiements) {
        this.clientId = clientId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateNaissance = dateNaissance;
        this.profilCapillaire = profilCapillaire;
        this.rendezVous = rendezVous;
        this.prestations = prestations;
        this.factures = factures;
        this.paiements = paiements;
    }
}
