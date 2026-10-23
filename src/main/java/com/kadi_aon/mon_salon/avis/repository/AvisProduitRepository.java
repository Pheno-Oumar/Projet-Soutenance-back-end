package com.kadi_aon.mon_salon.avis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.avis.entity.AvisProduit;

@Repository
public interface AvisProduitRepository extends JpaRepository<AvisProduit, Long> {

    Optional<AvisProduit> findByClientIdAndProduitId(Long clientId, Long produitId);

    boolean existsByClientIdAndProduitId(Long clientId, Long produitId);

    Optional<AvisProduit> findByIdAndClientId(Long id, Long clientId);

    Optional<AvisProduit> findByIdAndSalonSlug(Long id, String slugSalon);

    List<AvisProduit> findByProduitIdAndStatutTrueOrderByDateCreationDesc(Long produitId);

    List<AvisProduit> findByProduitIdOrderByDateCreationDesc(Long produitId);

    List<AvisProduit> findBySalonSlugAndStatutOrderByDateCreationDesc(String slugSalon, boolean statut);

    List<AvisProduit> findBySalonSlugOrderByDateCreationDesc(String slugSalon);

    List<AvisProduit> findByClientIdOrderByDateCreationDesc(Long clientId);
}
