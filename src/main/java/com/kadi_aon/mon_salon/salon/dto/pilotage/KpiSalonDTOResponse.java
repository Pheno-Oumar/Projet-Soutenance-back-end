package com.kadi_aon.mon_salon.salon.dto.pilotage;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KpiSalonDTOResponse {

    private String slugSalon;
    private String nomSalon;

    private long nombreClients;
    private long nombreCoiffeurs;

    private long nombreRendezVousTotal;
    private long nombreRendezVousTermines;
    private long nombreRendezVousAnnules;

    private long nombrePrestationsTotal;
    private long nombrePrestationsTerminees;

    private BigDecimal totalRevenus;
    private BigDecimal totalDepenses;
    private BigDecimal totalDepensesEnAttente;
    private BigDecimal beneficeNet;

    public KpiSalonDTOResponse() {
    }

    public KpiSalonDTOResponse(String slugSalon, String nomSalon, long nombreClients, long nombreCoiffeurs,
            long nombreRendezVousTotal, long nombreRendezVousTermines, long nombreRendezVousAnnules,
            long nombrePrestationsTotal, long nombrePrestationsTerminees, BigDecimal totalRevenus,
            BigDecimal totalDepenses, BigDecimal totalDepensesEnAttente, BigDecimal beneficeNet) {
        this.slugSalon = slugSalon;
        this.nomSalon = nomSalon;
        this.nombreClients = nombreClients;
        this.nombreCoiffeurs = nombreCoiffeurs;
        this.nombreRendezVousTotal = nombreRendezVousTotal;
        this.nombreRendezVousTermines = nombreRendezVousTermines;
        this.nombreRendezVousAnnules = nombreRendezVousAnnules;
        this.nombrePrestationsTotal = nombrePrestationsTotal;
        this.nombrePrestationsTerminees = nombrePrestationsTerminees;
        this.totalRevenus = totalRevenus;
        this.totalDepenses = totalDepenses;
        this.totalDepensesEnAttente = totalDepensesEnAttente;
        this.beneficeNet = beneficeNet;
    }
}
