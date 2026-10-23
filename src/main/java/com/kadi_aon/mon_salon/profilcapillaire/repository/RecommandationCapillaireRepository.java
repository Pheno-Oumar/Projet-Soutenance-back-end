package com.kadi_aon.mon_salon.profilcapillaire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.profilcapillaire.entity.RecommandationCapillaire;

@Repository
public interface RecommandationCapillaireRepository extends JpaRepository<RecommandationCapillaire, Long> {

    Optional<RecommandationCapillaire> findFirstByClientIdOrderByDateRecommandationDesc(Long clientId);

    List<RecommandationCapillaire> findByClientIdOrderByDateRecommandationDesc(Long clientId);

    Optional<RecommandationCapillaire> findByIdAndClientId(Long id, Long clientId);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM RecommandationCapillaire r WHERE r.client.id = :clientId AND YEAR(r.dateRecommandation) = :annee AND MONTH(r.dateRecommandation) = :mois ORDER BY r.dateRecommandation DESC")
    List<RecommandationCapillaire> findByClientIdAndAnneeAndMoisOrderByDateRecommandationDesc(
            @org.springframework.data.repository.query.Param("clientId") Long clientId,
            @org.springframework.data.repository.query.Param("annee") int annee,
            @org.springframework.data.repository.query.Param("mois") int mois
    );

    Optional<RecommandationCapillaire> findByReference(String reference);
}
