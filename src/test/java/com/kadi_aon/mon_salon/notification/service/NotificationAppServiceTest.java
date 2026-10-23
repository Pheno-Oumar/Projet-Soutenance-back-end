package com.kadi_aon.mon_salon.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.notification.dto.NotificationCountDTOResponse;
import com.kadi_aon.mon_salon.notification.dto.NotificationDTOResponse;
import com.kadi_aon.mon_salon.notification.entity.Notification;
import com.kadi_aon.mon_salon.notification.enums.TypeNotification;
import com.kadi_aon.mon_salon.notification.mapper.NotificationDTOResponseMapper;
import com.kadi_aon.mon_salon.notification.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationAppServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private CompteRepository compteRepository;
    @Mock
    private NotificationDTOResponseMapper mapper;

    @InjectMocks
    private NotificationAppService notificationAppService;

    private Compte compte;
    private Notification notification;
    private NotificationDTOResponse dtoResponse;

    @BeforeEach
    void setUp() {
        compte = Compte.builder()
                .id(1L)
                .nom("Diallo")
                .prenom("Fatou")
                .telephone("+22370112233")
                .email("fatou@test.com")
                .build();

        notification = Notification.builder()
                .id(10L)
                .compte(compte)
                .titre("Rappel Rendez-vous")
                .contenu("Votre rendez-vous est prévu demain à 14h.")
                .type(TypeNotification.RENDEZ_VOUS)
                .referenceMetier("RDV-2026-0042")
                .lu(false)
                .dateCreation(LocalDateTime.now())
                .build();

        dtoResponse = new NotificationDTOResponse(
                10L,
                "Rappel Rendez-vous",
                "Votre rendez-vous est prévu demain à 14h.",
                TypeNotification.RENDEZ_VOUS,
                "RDV-2026-0042",
                false,
                notification.getDateCreation(),
                null
        );
    }

    @Test
    void creerNotification_succes() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(mapper.apply(notification)).thenReturn(dtoResponse);

        NotificationDTOResponse result = notificationAppService.creerNotification(
                compte, "Rappel Rendez-vous", "Votre rendez-vous est prévu demain à 14h.",
                TypeNotification.RENDEZ_VOUS, "RDV-2026-0042"
        );

        assertNotNull(result);
        assertEquals("Rappel Rendez-vous", result.titre());
        assertEquals("RDV-2026-0042", result.referenceMetier());
        assertFalse(result.lu());
    }

    @Test
    void listerMesNotifications_succes() {
        when(compteRepository.findByIdentifiant("+22370112233")).thenReturn(Optional.of(compte));
        Page<Notification> page = new PageImpl<>(Collections.singletonList(notification));
        when(notificationRepository.findByCompteIdOrderByDateCreationDesc(1L, PageRequest.of(0, 10))).thenReturn(page);
        when(mapper.apply(notification)).thenReturn(dtoResponse);

        Page<NotificationDTOResponse> result = notificationAppService.listerMesNotifications("+22370112233", null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("RDV-2026-0042", result.getContent().get(0).referenceMetier());
    }

    @Test
    void compterNotifications_succes() {
        when(compteRepository.findByIdentifiant("+22370112233")).thenReturn(Optional.of(compte));
        when(notificationRepository.countByCompteId(1L)).thenReturn(5L);
        when(notificationRepository.countByCompteIdAndLuFalse(1L)).thenReturn(2L);

        NotificationCountDTOResponse count = notificationAppService.compterNotifications("+22370112233");

        assertEquals(5L, count.total());
        assertEquals(2L, count.nonLues());
    }

    @Test
    void marquerCommeLu_succes() {
        when(compteRepository.findByIdentifiant("+22370112233")).thenReturn(Optional.of(compte));
        when(notificationRepository.findByIdAndCompteId(10L, 1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationDTOResponse luDto = new NotificationDTOResponse(
                10L, "Rappel Rendez-vous", "Contenu", TypeNotification.RENDEZ_VOUS, "RDV-2026-0042", true, LocalDateTime.now(), LocalDateTime.now()
        );
        when(mapper.apply(notification)).thenReturn(luDto);

        NotificationDTOResponse result = notificationAppService.marquerCommeLu("+22370112233", 10L);

        assertNotNull(result);
        assertTrue(result.lu());
    }

    @Test
    void toutMarquerCommeLu_succes() {
        when(compteRepository.findByIdentifiant("+22370112233")).thenReturn(Optional.of(compte));

        notificationAppService.toutMarquerCommeLu("+22370112233");

        verify(notificationRepository).toutMarquerCommeLu(1L);
    }

    @Test
    void supprimerNotification_succes() {
        when(compteRepository.findByIdentifiant("+22370112233")).thenReturn(Optional.of(compte));
        when(notificationRepository.findByIdAndCompteId(10L, 1L)).thenReturn(Optional.of(notification));

        notificationAppService.supprimerNotification("+22370112233", 10L);

        verify(notificationRepository).delete(notification);
    }
}
