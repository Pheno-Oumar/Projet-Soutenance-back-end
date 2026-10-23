package com.kadi_aon.mon_salon.favori.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FavoriProduitDTOResponse(
        Long id,
        Long produitId,
        String produitNom,
        String description,
        BigDecimal prixVente,
        String imageUrl,
        List<String> imagesUrls,
        String salonSlug,
        String salonNom,
        LocalDateTime dateAjout
) {}
