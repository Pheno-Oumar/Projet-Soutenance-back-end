package com.kadi_aon.mon_salon.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.stock.entity.LigneCommande;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    List<LigneCommande> findByCommandeId(Long commandeId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(lc) > 0 FROM LigneCommande lc WHERE lc.produit.id = :produitId AND lc.commande.clientAffectation.compte.id = :clientId AND lc.commande.statut IN (com.kadi_aon.mon_salon.stock.enums.StatutCommande.VALIDEE, com.kadi_aon.mon_salon.stock.enums.StatutCommande.RECUPEREE)")
    boolean existsAchatClientProduit(
            @org.springframework.data.repository.query.Param("clientId") Long clientId,
            @org.springframework.data.repository.query.Param("produitId") Long produitId);

    @org.springframework.data.jpa.repository.Query("SELECT lc.produit.id, SUM(lc.quantite), SUM(lc.sousTotal) " +
           "FROM LigneCommande lc " +
           "WHERE lc.commande.clientAffectation.salon.slug = :slug " +
           "AND lc.commande.statut IN (com.kadi_aon.mon_salon.stock.enums.StatutCommande.VALIDEE, com.kadi_aon.mon_salon.stock.enums.StatutCommande.RECUPEREE) " +
           "GROUP BY lc.produit.id")
    List<Object[]> agregationVentesParProduit(@org.springframework.data.repository.query.Param("slug") String slug);
}
