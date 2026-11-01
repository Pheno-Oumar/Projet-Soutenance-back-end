package com.kadi_aon.mon_salon.reclamation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.reclamation.entity.Reclamation;
import com.kadi_aon.mon_salon.reclamation.enums.StatutReclamation;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    List<Reclamation> findBySalonSlugOrderByDateCreationDesc(String slugSalon);

    List<Reclamation> findBySalonSlugAndStatutOrderByDateCreationDesc(String slugSalon, StatutReclamation statut);

    List<Reclamation> findByAffectationClientCompteEmailAndSalonSlugOrderByDateCreationDesc(String email, String slugSalon);

    Optional<Reclamation> findByIdAndSalonSlug(Long id, String slugSalon);

    Optional<Reclamation> findByIdAndAffectationClientCompteEmailAndSalonSlug(Long id, String email, String slugSalon);

    List<Reclamation> findByAffectationClientCompteEmailOrderByDateCreationDesc(String email);

    Optional<Reclamation> findByIdAndAffectationClientCompteEmail(Long id, String email);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Reclamation r WHERE (r.affectationClient.compte.email = :identifiant OR r.affectationClient.compte.telephone = :identifiant) AND (:slugSalon IS NULL OR r.salon.slug = :slugSalon) AND (:statut IS NULL OR r.statut = :statut) ORDER BY r.dateCreation DESC")
    List<Reclamation> findByClientFiltre(
            @org.springframework.data.repository.query.Param("identifiant") String identifiant,
            @org.springframework.data.repository.query.Param("slugSalon") String slugSalon,
            @org.springframework.data.repository.query.Param("statut") StatutReclamation statut);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(r) > 0 FROM Reclamation r WHERE ((:compteId IS NOT NULL AND r.affectationClient.compte.id = :compteId) OR r.affectationClient.compte.email = :identifiant OR r.affectationClient.compte.telephone = :identifiant) AND r.statut IN (com.kadi_aon.mon_salon.reclamation.enums.StatutReclamation.EN_ATTENTE, com.kadi_aon.mon_salon.reclamation.enums.StatutReclamation.EN_COURS)")
    boolean hasReclamationsEnCours(@org.springframework.data.repository.query.Param("compteId") Long compteId, @org.springframework.data.repository.query.Param("identifiant") String identifiant);
}

