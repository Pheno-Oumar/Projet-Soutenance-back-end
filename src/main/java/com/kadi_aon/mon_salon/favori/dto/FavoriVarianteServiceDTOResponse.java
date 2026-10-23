package com.kadi_aon.mon_salon.favori.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FavoriVarianteServiceDTOResponse(
        Long id,
        Long varianteId,
        String varianteNom,
        Long serviceId,
        String serviceNom,
        String salonSlug,
        String salonNom,
        BigDecimal prix,
        Integer dureeMinutes,
        LocalDateTime dateAjout
) {}
