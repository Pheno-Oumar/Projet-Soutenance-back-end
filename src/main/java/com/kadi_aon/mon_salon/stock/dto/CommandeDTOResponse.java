package com.kadi_aon.mon_salon.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CommandeDTOResponse(
        Long id,
        String numeroCommande,
        LocalDateTime dateCommande,
        String statut,
        BigDecimal montantTotal,
        String clientNom,
        String clientEmail,
        String codeRetrait,
        String motifRejet,
        LocalDateTime dateTraitement,
        String traiteParNom,
        Long factureId,
        String numeroFacture,
        List<LigneCommandeDTOResponse> lignes,
        Boolean encaisseeEnCaisse,
        String moyenPaiement
) {
    public CommandeDTOResponse(
            Long id,
            String numeroCommande,
            LocalDateTime dateCommande,
            String statut,
            BigDecimal montantTotal,
            String clientNom,
            String clientEmail,
            String codeRetrait,
            String motifRejet,
            LocalDateTime dateTraitement,
            String traiteParNom,
            Long factureId,
            String numeroFacture,
            List<LigneCommandeDTOResponse> lignes) {
        this(id, numeroCommande, dateCommande, statut, montantTotal, clientNom, clientEmail,
                codeRetrait, motifRejet, dateTraitement, traiteParNom, factureId, numeroFacture, lignes, false, null);
    }
}
