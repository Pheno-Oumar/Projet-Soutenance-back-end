package com.kadi_aon.mon_salon.stock.dto;

import java.math.BigDecimal;

public record ProduitRentabiliteDTOResponse(
        Long produitId,
        String nom,
        String imageUrl,
        Long categorieId,
        String categorieNom,
        BigDecimal prixVente,
        BigDecimal prixAchatMoyen,
        BigDecimal margeUnitaire,
        BigDecimal tauxMarge,
        BigDecimal tauxMarque,
        long quantiteVendue,
        long stockDisponible,
        BigDecimal chiffreAffairesTotal,
        BigDecimal beneficeTotal,
        String niveauRentabilite
) {}
