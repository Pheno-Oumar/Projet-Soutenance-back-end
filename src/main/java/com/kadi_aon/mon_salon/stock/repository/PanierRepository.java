package com.kadi_aon.mon_salon.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.stock.entity.Panier;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {

    Optional<Panier> findByClientAffectationId(Long affectationId);

    @Query("SELECT p FROM Panier p WHERE (p.clientAffectation.compte.email = :identifiant OR p.clientAffectation.compte.telephone = :identifiant) AND p.clientAffectation.salon.slug = :slug")
    Optional<Panier> findByClientAffectationCompteEmailAndClientAffectationSalonSlug(@Param("identifiant") String identifiant, @Param("slug") String slug);

    @Query("SELECT p FROM Panier p WHERE p.clientAffectation.compte.email = :identifiant OR p.clientAffectation.compte.telephone = :identifiant")
    List<Panier> findByClientAffectationCompteEmail(@Param("identifiant") String identifiant);

    boolean existsByClientAffectationId(Long affectationId);

}
