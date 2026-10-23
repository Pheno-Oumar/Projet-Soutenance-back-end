package com.kadi_aon.mon_salon.favori.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.favori.entity.FavoriProduit;

@Repository
public interface FavoriProduitRepository extends JpaRepository<FavoriProduit, Long> {

    Optional<FavoriProduit> findByClientIdAndProduitId(Long clientId, Long produitId);

    boolean existsByClientIdAndProduitId(Long clientId, Long produitId);

    void deleteByClientIdAndProduitId(Long clientId, Long produitId);

    List<FavoriProduit> findByClientIdOrderByDateAjoutDesc(Long clientId);

    List<FavoriProduit> findByClientEmailOrderByDateAjoutDesc(String clientEmail);
}
