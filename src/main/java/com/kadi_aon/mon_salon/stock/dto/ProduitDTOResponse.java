package com.kadi_aon.mon_salon.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProduitDTOResponse(
        Long id,
        String nom,
        String description,
        BigDecimal prixVente,
        String imageUrl,
        List<String> imagesUrls,
        boolean statut,
        Long categorieId,
        String categorieNom,
        StockProduitDTOResponse stock,
        LocalDateTime dateCreation,
        LocalDateTime dateModification,
        String slugSalon,
        String salonNom
) {
    public ProduitDTOResponse(
            Long id,
            String nom,
            String description,
            BigDecimal prixVente,
            String imageUrl,
            List<String> imagesUrls,
            boolean statut,
            Long categorieId,
            String categorieNom,
            StockProduitDTOResponse stock,
            LocalDateTime dateCreation,
            LocalDateTime dateModification
    ) {
        this(id, nom, description, prixVente, imageUrl, imagesUrls, statut, categorieId, categorieNom, stock, dateCreation, dateModification, null, null);
    }

    public ProduitDTOResponse(
            Long id,
            String nom,
            String description,
            BigDecimal prixVente,
            String imageUrl,
            boolean statut,
            Long categorieId,
            String categorieNom,
            StockProduitDTOResponse stock,
            LocalDateTime dateCreation,
            LocalDateTime dateModification
    ) {
        this(id, nom, description, prixVente, imageUrl,
             imageUrl != null ? List.of(imageUrl) : List.of(),
             statut, categorieId, categorieNom, stock, dateCreation, dateModification, null, null);
    }

    public ProduitDTOResponse(
            Long id,
            String nom,
            String description,
            BigDecimal prixVente,
            boolean statut,
            Long categorieId,
            String categorieNom,
            StockProduitDTOResponse stock,
            LocalDateTime dateCreation,
            LocalDateTime dateModification
    ) {
        this(id, nom, description, prixVente, null, List.of(), statut, categorieId, categorieNom, stock, dateCreation, dateModification, null, null);
    }
}
