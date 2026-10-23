package com.kadi_aon.mon_salon.stock.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.stock.entity.Commande;
import com.kadi_aon.mon_salon.stock.enums.StatutCommande;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findByNumeroCommande(String numeroCommande);

    Optional<Commande> findByIdAndClientAffectationSalonSlug(Long id, String slug);

    @Query("SELECT c FROM Commande c WHERE c.id = :id AND (c.clientAffectation.compte.email = :identifiant OR c.clientAffectation.compte.telephone = :identifiant) AND c.clientAffectation.salon.slug = :slug")
    Optional<Commande> findByIdAndClientAffectationCompteEmailAndClientAffectationSalonSlug(@Param("id") Long id, @Param("identifiant") String identifiant, @Param("slug") String slug);

    List<Commande> findByClientAffectationSalonSlugOrderByDateCommandeDesc(String slug);

    List<Commande> findByClientAffectationSalonSlugAndStatutOrderByDateCommandeDesc(String slug, StatutCommande statut);

    List<Commande> findByClientAffectationSalonSlugAndDateCommandeBetweenOrderByDateCommandeDesc(String slug, LocalDateTime debut, LocalDateTime fin);

    List<Commande> findByClientAffectationSalonSlugAndStatutAndDateCommandeBetweenOrderByDateCommandeDesc(String slug, StatutCommande statut, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT c FROM Commande c WHERE (c.clientAffectation.compte.email = :identifiant OR c.clientAffectation.compte.telephone = :identifiant) AND c.clientAffectation.salon.slug = :slug ORDER BY c.dateCommande DESC")
    List<Commande> findByClientAffectationCompteEmailAndClientAffectationSalonSlugOrderByDateCommandeDesc(@Param("identifiant") String identifiant, @Param("slug") String slug);

    @Query("SELECT c FROM Commande c WHERE c.clientAffectation.compte.email = :identifiant OR c.clientAffectation.compte.telephone = :identifiant ORDER BY c.dateCommande DESC")
    List<Commande> findByClientAffectationCompteEmailOrderByDateCommandeDesc(@Param("identifiant") String identifiant);

    @Query("SELECT c FROM Commande c WHERE c.id = :id AND (c.clientAffectation.compte.email = :identifiant OR c.clientAffectation.compte.telephone = :identifiant)")
    Optional<Commande> findByIdAndClientAffectationCompteEmail(@Param("id") Long id, @Param("identifiant") String identifiant);

    long countByClientAffectationSalonSlug(String slug);

    long countByClientAffectationSalonSlugAndStatut(String slug, StatutCommande statut);

    @Query("SELECT COALESCE(SUM(c.montantTotal), 0) FROM Commande c WHERE c.clientAffectation.salon.slug = :slug AND c.statut IN ('VALIDEE', 'RECUPEREE')")
    BigDecimal totalChiffreAffairesCommandesValidees(@Param("slug") String slug);

    @Query("SELECT c FROM Commande c WHERE ((:clientId IS NOT NULL AND c.clientAffectation.compte.id = :clientId) OR c.clientAffectation.compte.email = :identifiant OR c.clientAffectation.compte.telephone = :identifiant) " +
           "AND (:slugSalon IS NULL OR LOWER(c.clientAffectation.salon.slug) = LOWER(:slugSalon)) " +
           "AND (:statut IS NULL OR c.statut = :statut) " +
           "ORDER BY c.dateCommande DESC")
    List<Commande> findCommandesClientFiltre(
            @Param("clientId") Long clientId,
            @Param("identifiant") String identifiant,
            @Param("slugSalon") String slugSalon,
            @Param("statut") StatutCommande statut);

    @Query("SELECT c FROM Commande c WHERE c.clientAffectation.salon.slug = :slug AND c.statut = :statut AND (c.encaisseeEnCaisse IS NULL OR c.encaisseeEnCaisse = false)")
    List<Commande> findCommandesEnAttenteEncaissement(@Param("slug") String slug, @Param("statut") StatutCommande statut);
}
