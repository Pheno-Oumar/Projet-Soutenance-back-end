package com.kadi_aon.mon_salon.salon.mapper;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.kadi_aon.mon_salon.salon.dto.ServiceSalonDTOResponse;
import com.kadi_aon.mon_salon.salon.dto.VarianteServiceDTOResponse;
import com.kadi_aon.mon_salon.salon.entity.ServiceSalon;
import com.kadi_aon.mon_salon.salon.entity.VarianteService;

@Component
public class ServiceSalonDTOResponseMapper implements Function<ServiceSalon, ServiceSalonDTOResponse> {

    @Override
    public ServiceSalonDTOResponse apply(ServiceSalon service) {
        if (service == null) {
            return null;
        }

        List<VarianteServiceDTOResponse> variantes = (service.getVariantes() != null)
                ? service.getVariantes().stream().map(this::mapVariante).toList()
                : Collections.emptyList();

        String slug = (service.getSalon() != null) ? service.getSalon().getSlug() : null;
        String nomSalon = (service.getSalon() != null) ? service.getSalon().getNom() : null;

        return new ServiceSalonDTOResponse(
                service.getId(),
                service.getNom(),
                service.getDescription(),
                service.getImageUrl(),
                service.getStatut(),
                variantes,
                service.getDateCreation(),
                slug,
                nomSalon
        );
    }

    public VarianteServiceDTOResponse mapVariante(VarianteService v) {
        if (v == null) {
            return null;
        }
        return new VarianteServiceDTOResponse(
                v.getId(),
                v.getNom(),
                v.getDescription(),
                v.getDureeMinutes(),
                v.getPrix(),
                v.getImageUrl(),
                v.getImagesUrls() != null ? new java.util.ArrayList<>(v.getImagesUrls()) : java.util.List.of(),
                v.getStatut()
        );
    }
}
