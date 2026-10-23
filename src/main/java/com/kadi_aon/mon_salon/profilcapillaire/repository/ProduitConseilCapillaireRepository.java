package com.kadi_aon.mon_salon.profilcapillaire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.profilcapillaire.entity.ProduitConseilCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;

@Repository
public interface ProduitConseilCapillaireRepository extends JpaRepository<ProduitConseilCapillaire, Long> {

    List<ProduitConseilCapillaire> findByActifTrue();

    List<ProduitConseilCapillaire> findByEtapeRoutineAndActifTrue(EtapeRoutine etapeRoutine);
}
