package com.kadi_aon.mon_salon.rendezvous.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.rendezvous.entity.RendezVous;
import com.kadi_aon.mon_salon.rendezvous.enums.StatutRendezVous;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findBySalonSlugOrderByDateHeurePrevueDesc(String slugSalon);

    List<RendezVous> findBySalonSlugAndClientEmailOrderByDateHeurePrevueDesc(String slugSalon, String email);

    Optional<RendezVous> findByIdAndSalonSlug(Long id, String slugSalon);

    @Query("SELECT r FROM RendezVous r WHERE r.salon.id = :salonId AND r.statut != :statutAnnule AND r.statut != com.kadi_aon.mon_salon.rendezvous.enums.StatutRendezVous.NO_SHOW AND r.dateHeurePrevue < :fin AND r.dateHeureFin > :debut")
    List<RendezVous> findRendezVousActifsBySalonAndPeriode(
            @Param("salonId") Long salonId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("statutAnnule") StatutRendezVous statutAnnule);

    @Query("SELECT r FROM RendezVous r WHERE r.client.id = :clientId AND r.salon.id = :salonId AND r.statut != :statutAnnule AND r.statut != com.kadi_aon.mon_salon.rendezvous.enums.StatutRendezVous.NO_SHOW AND r.dateHeurePrevue < :fin AND r.dateHeureFin > :debut")
    List<RendezVous> findRendezVousActifsByClientAndSalonAndPeriode(
            @Param("clientId") Long clientId,
            @Param("salonId") Long salonId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("statutAnnule") StatutRendezVous statutAnnule);

    @Query("SELECT r FROM RendezVous r WHERE r.salon.id = :salonId AND r.dateHeurePrevue >= :debutJour AND r.dateHeurePrevue <= :finJour ORDER BY r.dateHeurePrevue ASC")
    List<RendezVous> findPlanningBySalonAndJour(
            @Param("salonId") Long salonId,
            @Param("debutJour") LocalDateTime debutJour,
            @Param("finJour") LocalDateTime finJour);

    @Query("SELECT r FROM RendezVous r WHERE r.salon.id = :salonId AND r.coiffeur.id = :coiffeurId AND r.dateHeurePrevue >= :debutJour AND r.dateHeurePrevue <= :finJour ORDER BY r.dateHeurePrevue ASC")
    List<RendezVous> findPlanningBySalonAndCoiffeurAndJour(
            @Param("salonId") Long salonId,
            @Param("coiffeurId") Long coiffeurId,
            @Param("debutJour") LocalDateTime debutJour,
            @Param("finJour") LocalDateTime finJour);

    @Query("SELECT r FROM RendezVous r WHERE r.salon.id = :salonId AND r.statut = :statut AND r.dateHeurePrevue < :now AND r.dateHeurePrevue >= :debutJour ORDER BY r.dateHeurePrevue ASC")
    List<RendezVous> findRendezVousEnRetard(
            @Param("salonId") Long salonId,
            @Param("now") LocalDateTime now,
            @Param("debutJour") LocalDateTime debutJour,
            @Param("statut") StatutRendezVous statut);

    @Query("SELECT r FROM RendezVous r WHERE r.salon.id = :salonId AND (r.statut = :statutNoShow OR r.motifAnnulation LIKE '%retard%' OR r.motifAnnulation LIKE '%Retard%') ORDER BY r.dateHeurePrevue DESC")
    List<RendezVous> findHistoriqueRetards(
            @Param("salonId") Long salonId,
            @Param("statutNoShow") StatutRendezVous statutNoShow);

    long countBySalonSlug(String slugSalon);

    long countBySalonSlugAndStatut(String slugSalon, StatutRendezVous statut);

    List<RendezVous> findByClientIdOrderByDateHeurePrevueDesc(Long clientId);

    List<RendezVous> findByClientIdAndSalonSlugOrderByDateHeurePrevueDesc(Long clientId, String slugSalon);

    List<RendezVous> findByClientEmailOrderByDateHeurePrevueDesc(String email);

    @Query("SELECT r FROM RendezVous r WHERE ((:clientId IS NOT NULL AND r.client.id = :clientId) OR r.client.email = :identifiant OR r.client.telephone = :identifiant) " +
           "AND (:slugSalon IS NULL OR LOWER(r.salon.slug) = LOWER(:slugSalon)) " +
           "ORDER BY r.dateHeurePrevue DESC")
    List<RendezVous> findRendezVousClientFiltreSalon(
            @Param("clientId") Long clientId,
            @Param("identifiant") String identifiant,
            @Param("slugSalon") String slugSalon);

    @Query("SELECT COUNT(r) > 0 FROM RendezVous r WHERE (r.client.email = :email OR r.client.telephone = :email) AND LOWER(r.salon.slug) = LOWER(:slugSalon) AND r.statut = :statut")
    boolean existsByClientEmailAndSalonSlugAndStatut(@Param("email") String email, @Param("slugSalon") String slugSalon, @Param("statut") StatutRendezVous statut);

    @Query("SELECT COUNT(r) > 0 FROM RendezVous r WHERE (r.client.email = :email OR r.client.telephone = :email) AND LOWER(r.salon.slug) = LOWER(:slugSalon)")
    boolean existsByClientEmailAndSalonSlug(@Param("email") String email, @Param("slugSalon") String slugSalon);

    @Query("SELECT COUNT(r) > 0 FROM RendezVous r WHERE ((:compteId IS NOT NULL AND r.client.id = :compteId) OR r.client.email = :identifiant OR r.client.telephone = :identifiant) AND r.statut IN (com.kadi_aon.mon_salon.rendezvous.enums.StatutRendezVous.CONFIRME, com.kadi_aon.mon_salon.rendezvous.enums.StatutRendezVous.EN_COURS) AND r.dateHeureFin >= :now")
    boolean hasRendezVousEnCours(@Param("compteId") Long compteId, @Param("identifiant") String identifiant, @Param("now") LocalDateTime now);
}

