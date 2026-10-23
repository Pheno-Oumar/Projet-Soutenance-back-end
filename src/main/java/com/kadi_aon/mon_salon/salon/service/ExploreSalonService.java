package com.kadi_aon.mon_salon.salon.service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.salon.dto.SalonDTOResponse;
import com.kadi_aon.mon_salon.salon.dto.SalonDistanceProjection;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.mapper.SalonDTOResponseMapper;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExploreSalonService {

    private final SalonRepository salonRepository;
    private final SalonDTOResponseMapper salonDTOResponseMapper;

    public ExploreSalonService(
            SalonRepository salonRepository,
            SalonDTOResponseMapper salonDTOResponseMapper) {
        super();
        this.salonRepository = salonRepository;
        this.salonDTOResponseMapper = salonDTOResponseMapper;
    }

    @Transactional(readOnly = true)
    public Page<SalonDTOResponse> listerSalonsActifs(Pageable pageable) {
        return salonRepository.findByStatutTrue(pageable)
                .map(salonDTOResponseMapper::mapPublic);
    }

    @Transactional(readOnly = true)
    public List<SalonDTOResponse> rechercherSalons(String query) {
        if (query == null || query.isBlank()) {
            return salonRepository.findByStatutTrueOrderByNomAsc().stream()
                    .map(salonDTOResponseMapper::mapPublic)
                    .toList();
        }
        return salonRepository.searchSalons(query.trim()).stream()
                .map(salonDTOResponseMapper::mapPublic)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalonDTOResponse> rechercherSalonsNearby(double lat, double lng, double rayonKm) {
        try {
            // 1. Requête spatiale SQL native optimisée via MySQL ST_Distance_Sphere
            List<SalonDistanceProjection> projections = salonRepository.findNearbySalonIdsWithDistance(lat, lng, rayonKm);
            if (projections != null && !projections.isEmpty()) {
                Map<Long, Double> distanceMap = new LinkedHashMap<>();
                for (SalonDistanceProjection p : projections) {
                    if (p.getId() != null) {
                        distanceMap.put(p.getId(), p.getDistanceKm());
                    }
                }
                List<Salon> salons = salonRepository.findAllById(distanceMap.keySet());
                Map<Long, Salon> salonMap = salons.stream().collect(Collectors.toMap(Salon::getId, s -> s));

                List<SalonDTOResponse> resultats = new ArrayList<>();
                for (Map.Entry<Long, Double> entry : distanceMap.entrySet()) {
                    Salon s = salonMap.get(entry.getKey());
                    if (s != null && Boolean.TRUE.equals(s.getStatut())) {
                        double distRounded = Math.round(entry.getValue() * 10.0) / 10.0;
                        resultats.add(salonDTOResponseMapper.mapPublic(s, distRounded));
                    }
                }
                log.info("Recherche spatiale MySQL effectuée (lat={}, lng={}, rayon={} km) -> {} salons trouvés",
                        lat, lng, rayonKm, resultats.size());
                return resultats;
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la requête spatiale MySQL, bascule sur calcul Haversine : {}", e.getMessage());
        }

        // 2. Fallback avec calcul en mémoire si la fonction native MySQL rencontre un problème
        List<Salon> activeSalons = salonRepository.findByStatutTrueOrderByNomAsc();
        return activeSalons.stream()
                .filter(salon -> salon.getLatitude() != null && salon.getLongitude() != null)
                .map(salon -> {
                    double dist = calculerDistanceKm(lat, lng, salon.getLatitude(), salon.getLongitude());
                    double distRounded = Math.round(dist * 10.0) / 10.0;
                    return new AbstractMap.SimpleEntry<>(salon, distRounded);
                })
                .filter(entry -> entry.getValue() <= rayonKm)
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> salonDTOResponseMapper.mapPublic(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalonDTOResponse> rechercherSalonsParService(Long serviceId) {
        return salonRepository.findSalonByServiceId(serviceId)
                .map(s -> List.of(salonDTOResponseMapper.mapPublic(s)))
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public SalonDTOResponse getSalonDetail(String slugSalon) {
        Salon salon = salonRepository.findBySlugAndStatutTrue(slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Salon actif introuvable avec le slug : " + slugSalon));
        return salonDTOResponseMapper.mapPublic(salon);
    }

    private double calculerDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Rayon de la Terre en kilomètres
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
