package com.kadi_aon.mon_salon.notification.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kadi_aon.mon_salon.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByCompteIdOrderByDateCreationDesc(Long compteId, Pageable pageable);

    Page<Notification> findByCompteIdAndLuOrderByDateCreationDesc(Long compteId, Boolean lu, Pageable pageable);

    long countByCompteIdAndLuFalse(Long compteId);

    long countByCompteId(Long compteId);

    Optional<Notification> findByIdAndCompteId(Long id, Long compteId);

    @Modifying
    @Query("UPDATE Notification n SET n.lu = true, n.dateLecture = CURRENT_TIMESTAMP WHERE n.compte.id = :compteId AND n.lu = false")
    void toutMarquerCommeLu(@Param("compteId") Long compteId);
}
