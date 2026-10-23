package com.kadi_aon.mon_salon.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kadi_aon.mon_salon.notification.entity.NotificationSms;
import com.kadi_aon.mon_salon.notification.enums.CanalMessage;
import com.kadi_aon.mon_salon.notification.repository.NotificationSmsRepository;

@ExtendWith(MockitoExtension.class)
class NotificationSmsServiceTest {

    @Mock
    private NotificationSmsRepository notificationSmsRepository;

    @InjectMocks
    private NotificationSmsService notificationSmsService;

    @Test
    void normaliserTelephone_numeroInternationalAvecPlus_preserveIndicatif() {
        // France
        assertEquals("+33612345678", notificationSmsService.normaliserTelephone("+33 6 12 34 56 78"));
        // Sénégal
        assertEquals("+221771234567", notificationSmsService.normaliserTelephone("+221 77 123 45 67"));
        // Côte d'Ivoire
        assertEquals("+2250708091011", notificationSmsService.normaliserTelephone("+225 07 08 09 10 11"));
        // USA
        assertEquals("+15551234567", notificationSmsService.normaliserTelephone("+1 (555) 123-4567"));
        // Mali avec indicatif explicite
        assertEquals("+22370000000", notificationSmsService.normaliserTelephone("+223 70 00 00 00"));
    }

    @Test
    void normaliserTelephone_prefixe00_convertitEnPlus() {
        assertEquals("+33612345678", notificationSmsService.normaliserTelephone("0033 6 12 34 56 78"));
        assertEquals("+221771234567", notificationSmsService.normaliserTelephone("00221 77 123 45 67"));
    }

    @Test
    void normaliserTelephone_formatNationalMalien8Chiffres_ajouteIndicatifMali() {
        assertEquals("+22377123456", notificationSmsService.normaliserTelephone("77 12 34 56"));
        assertEquals("+22366112233", notificationSmsService.normaliserTelephone("66112233"));
    }

    @Test
    void normaliserTelephone_formatNationalFrancais10Chiffres_ajouteIndicatifFrance() {
        assertEquals("+33612345678", notificationSmsService.normaliserTelephone("06 12 34 56 78"));
        assertEquals("+33712345678", notificationSmsService.normaliserTelephone("0712345678"));
    }

    @Test
    void normaliserTelephone_indicatifSansPlus_ajoutePlus() {
        assertEquals("+33612345678", notificationSmsService.normaliserTelephone("33612345678"));
        assertEquals("+221771234567", notificationSmsService.normaliserTelephone("221771234567"));
    }

    @Test
    void normaliserTelephone_invalideOuVide_retourneNull() {
        assertNull(notificationSmsService.normaliserTelephone(null));
        assertNull(notificationSmsService.normaliserTelephone("   "));
        assertNull(notificationSmsService.normaliserTelephone("+123")); // Trop court
    }

    @Test
    void queueNotification_envoieSystematiquementDoubleSMS_Et_WhatsApp() {
        when(notificationSmsRepository.save(any(NotificationSms.class)))
                .thenAnswer(invocation -> {
                    NotificationSms notif = invocation.getArgument(0);
                    notif.setId(99L);
                    return notif;
                });

        List<NotificationSms> resultats = notificationSmsService.queueNotification("+33612345678", "Bonjour international");

        assertNotNull(resultats);
        assertEquals(2, resultats.size());
        assertEquals(CanalMessage.SMS, resultats.get(0).getCanal());
        assertEquals(CanalMessage.WHATSAPP, resultats.get(1).getCanal());
        assertEquals("+33612345678", resultats.get(0).getDestinataire());
        assertEquals("+33612345678", resultats.get(1).getDestinataire());

        verify(notificationSmsRepository, times(2)).save(any(NotificationSms.class));
    }

    @Test
    void queueRappelRendezVous_inclutNomSalonEtHairStyle() {
        when(notificationSmsRepository.save(any(NotificationSms.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<NotificationSms> notifs = notificationSmsService.queueRappelRendezVous("+22370000000", "Fatou", "Salon Kadi Coiffure", "25/10/2026 à 14h00");

        assertNotNull(notifs);
        assertEquals(2, notifs.size());
        assertTrue(notifs.get(0).getMessage().contains("HairStyle [Salon Kadi Coiffure]"));
        assertTrue(notifs.get(0).getMessage().contains("Salon Kadi Coiffure"));
        assertTrue(notifs.get(0).getMessage().contains("Fatou"));
    }

    @Test
    void queueCommandePrete_inclutNomSalonEtCodeRetrait() {
        when(notificationSmsRepository.save(any(NotificationSms.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<NotificationSms> notifs = notificationSmsService.queueCommandePrete("+22370000000", "CMD-1234", "RET-999", "Salon Kadi Coiffure");

        assertNotNull(notifs);
        assertTrue(notifs.get(0).getMessage().contains("HairStyle [Salon Kadi Coiffure]"));
        assertTrue(notifs.get(0).getMessage().contains("RET-999"));
    }
}
