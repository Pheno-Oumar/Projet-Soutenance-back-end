package com.kadi_aon.mon_salon.coiffeur.dto;

import java.util.List;

import com.kadi_aon.mon_salon.rendezvous.dto.PlanningRendezVousDTOResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoiffeurDashboardDTOResponse {

    // Profil du coiffeur
    private Long affectationId;
    private String nomAffichage;
    private String biographie;
    private Integer anneeExperience;
    private String photoProfilUrl;

    // Métriques clés
    private long rdvAujourdhuiTotal;
    private long rdvAujourdhuiTermines;
    private long rdvAujourdhuiEnAttente;
    private long prestationsMoisTerminees;
    private Double noteMoyenne;
    private long totalAvis;

    // Planning du jour
    private List<PlanningRendezVousDTOResponse> planningAujourdhui;

    // Prochaines indisponibilités / congés prévus
    private List<IndisponibiliteDTOResponse> prochainesIndisponibilites;

    public CoiffeurDashboardDTOResponse() {
        super();
    }

    public CoiffeurDashboardDTOResponse(Long affectationId, String nomAffichage, String biographie,
                                       Integer anneeExperience, String photoProfilUrl,
                                       long rdvAujourdhuiTotal, long rdvAujourdhuiTermines,
                                       long rdvAujourdhuiEnAttente, long prestationsMoisTerminees,
                                       Double noteMoyenne, long totalAvis,
                                       List<PlanningRendezVousDTOResponse> planningAujourdhui,
                                       List<IndisponibiliteDTOResponse> prochainesIndisponibilites) {
        super();
        this.affectationId = affectationId;
        this.nomAffichage = nomAffichage;
        this.biographie = biographie;
        this.anneeExperience = anneeExperience;
        this.photoProfilUrl = photoProfilUrl;
        this.rdvAujourdhuiTotal = rdvAujourdhuiTotal;
        this.rdvAujourdhuiTermines = rdvAujourdhuiTermines;
        this.rdvAujourdhuiEnAttente = rdvAujourdhuiEnAttente;
        this.prestationsMoisTerminees = prestationsMoisTerminees;
        this.noteMoyenne = noteMoyenne;
        this.totalAvis = totalAvis;
        this.planningAujourdhui = planningAujourdhui;
        this.prochainesIndisponibilites = prochainesIndisponibilites;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long affectationId;
        private String nomAffichage;
        private String biographie;
        private Integer anneeExperience;
        private String photoProfilUrl;
        private long rdvAujourdhuiTotal;
        private long rdvAujourdhuiTermines;
        private long rdvAujourdhuiEnAttente;
        private long prestationsMoisTerminees;
        private Double noteMoyenne;
        private long totalAvis;
        private List<PlanningRendezVousDTOResponse> planningAujourdhui;
        private List<IndisponibiliteDTOResponse> prochainesIndisponibilites;

        public Builder affectationId(Long affectationId) { this.affectationId = affectationId; return this; }
        public Builder nomAffichage(String nomAffichage) { this.nomAffichage = nomAffichage; return this; }
        public Builder biographie(String biographie) { this.biographie = biographie; return this; }
        public Builder anneeExperience(Integer anneeExperience) { this.anneeExperience = anneeExperience; return this; }
        public Builder photoProfilUrl(String photoProfilUrl) { this.photoProfilUrl = photoProfilUrl; return this; }
        public Builder rdvAujourdhuiTotal(long rdvAujourdhuiTotal) { this.rdvAujourdhuiTotal = rdvAujourdhuiTotal; return this; }
        public Builder rdvAujourdhuiTermines(long rdvAujourdhuiTermines) { this.rdvAujourdhuiTermines = rdvAujourdhuiTermines; return this; }
        public Builder rdvAujourdhuiEnAttente(long rdvAujourdhuiEnAttente) { this.rdvAujourdhuiEnAttente = rdvAujourdhuiEnAttente; return this; }
        public Builder prestationsMoisTerminees(long prestationsMoisTerminees) { this.prestationsMoisTerminees = prestationsMoisTerminees; return this; }
        public Builder noteMoyenne(Double noteMoyenne) { this.noteMoyenne = noteMoyenne; return this; }
        public Builder totalAvis(long totalAvis) { this.totalAvis = totalAvis; return this; }
        public Builder planningAujourdhui(List<PlanningRendezVousDTOResponse> planningAujourdhui) { this.planningAujourdhui = planningAujourdhui; return this; }
        public Builder prochainesIndisponibilites(List<IndisponibiliteDTOResponse> prochainesIndisponibilites) { this.prochainesIndisponibilites = prochainesIndisponibilites; return this; }

        public CoiffeurDashboardDTOResponse build() {
            return new CoiffeurDashboardDTOResponse(
                affectationId, nomAffichage, biographie, anneeExperience, photoProfilUrl,
                rdvAujourdhuiTotal, rdvAujourdhuiTermines, rdvAujourdhuiEnAttente, prestationsMoisTerminees,
                noteMoyenne, totalAvis, planningAujourdhui, prochainesIndisponibilites
            );
        }
    }
}
