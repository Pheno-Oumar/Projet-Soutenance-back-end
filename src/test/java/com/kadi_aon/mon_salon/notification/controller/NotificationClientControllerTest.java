package com.kadi_aon.mon_salon.notification.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.kadi_aon.mon_salon.common.dto.response.APIResponse;
import com.kadi_aon.mon_salon.notification.dto.NotificationCountDTOResponse;
import com.kadi_aon.mon_salon.notification.dto.NotificationDTOResponse;
import com.kadi_aon.mon_salon.notification.enums.TypeNotification;
import com.kadi_aon.mon_salon.notification.service.NotificationAppService;

@ExtendWith(MockitoExtension.class)
class NotificationClientControllerTest {

    @Mock
    private NotificationAppService notificationAppService;

    @InjectMocks
    private NotificationClientController controller;

    private Principal principal;
    private NotificationDTOResponse sampleNotification;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("fatou@test.com");

        sampleNotification = new NotificationDTOResponse(
                1L,
                "Rendez-vous confirmé",
                "Votre rendez-vous a été confirmé",
                TypeNotification.RENDEZ_VOUS,
                "RDV-123",
                false,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void testListerMesNotifications() {
        Page<NotificationDTOResponse> page = new PageImpl<>(List.of(sampleNotification));
        when(notificationAppService.listerMesNotifications(eq("fatou@test.com"), eq(null), any(Pageable.class)))
                .thenReturn(page);

        ResponseEntity<APIResponse<Page<NotificationDTOResponse>>> response =
                controller.listerMesNotifications(null, 0, 10, principal);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getTotalElements());
    }

    @Test
    void testCompterNotifications() {
        NotificationCountDTOResponse count = new NotificationCountDTOResponse(5L, 2L);
        when(notificationAppService.compterNotifications("fatou@test.com")).thenReturn(count);

        ResponseEntity<APIResponse<NotificationCountDTOResponse>> response =
                controller.compterNotifications(principal);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2L, response.getBody().getData().nonLues());
    }

    @Test
    void testMarquerCommeLu() {
        when(notificationAppService.marquerCommeLu("fatou@test.com", 1L)).thenReturn(sampleNotification);

        ResponseEntity<APIResponse<NotificationDTOResponse>> response =
                controller.marquerCommeLu(1L, principal);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(notificationAppService).marquerCommeLu("fatou@test.com", 1L);
    }

    @Test
    void testToutMarquerCommeLu() {
        ResponseEntity<APIResponse<Void>> response = controller.toutMarquerCommeLu(principal);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(notificationAppService).toutMarquerCommeLu("fatou@test.com");
    }

    @Test
    void testSupprimerNotification() {
        ResponseEntity<APIResponse<Void>> response = controller.supprimerNotification(1L, principal);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(notificationAppService).supprimerNotification("fatou@test.com", 1L);
    }
}
