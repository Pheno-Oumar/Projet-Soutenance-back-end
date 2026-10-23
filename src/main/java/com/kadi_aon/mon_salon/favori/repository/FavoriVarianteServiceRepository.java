package com.kadi_aon.mon_salon.favori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.favori.entity.FavoriVarianteService;

@Repository
public interface FavoriVarianteServiceRepository extends JpaRepository<FavoriVarianteService, Long> {

    Optional<FavoriVarianteService> findByClientIdAndVarianteId(Long clientId, Long varianteId);

    boolean existsByClientIdAndVarianteId(Long clientId, Long varianteId);

    void deleteByClientIdAndVarianteId(Long clientId, Long varianteId);

    List<FavoriVarianteService> findByClientIdOrderByDateAjoutDesc(Long clientId);

    List<FavoriVarianteService> findByClientEmailOrderByDateAjoutDesc(String clientEmail);
}
