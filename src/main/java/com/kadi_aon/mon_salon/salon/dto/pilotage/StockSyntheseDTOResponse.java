package com.kadi_aon.mon_salon.salon.dto.pilotage;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockSyntheseDTOResponse {

    private Long produitId;
    private String produitNom;
    private String categorieNom;
    private BigDecimal prixVente;
    private Integer quantiteDisponible;
    private Integer seuilMinimum;
    private Integer seuilMaximum;
    private boolean alerteStockBas;

    public StockSyntheseDTOResponse() {
    }

    public StockSyntheseDTOResponse(Long produitId, String produitNom, String categorieNom, BigDecimal prixVente,
            Integer quantiteDisponible, Integer seuilMinimum, Integer seuilMaximum, boolean alerteStockBas) {
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.categorieNom = categorieNom;
        this.prixVente = prixVente;
        this.quantiteDisponible = quantiteDisponible;
        this.seuilMinimum = seuilMinimum;
        this.seuilMaximum = seuilMaximum;
        this.alerteStockBas = alerteStockBas;
    }
}
