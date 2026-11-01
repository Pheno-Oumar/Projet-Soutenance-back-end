package com.kadi_aon.mon_salon.profilcapillaire.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.profilcapillaire.dto.CodeProfilDTORequest;
import com.kadi_aon.mon_salon.profilcapillaire.dto.CodeProfilVerificationDTORequest;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProfilCapillaireDTORequest;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProfilCapillaireDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.entity.ProfilCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.mapper.ProfilCapillaireDTOResponseMapper;
import com.kadi_aon.mon_salon.profilcapillaire.repository.ProfilCapillaireRepository;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProfilCapillaireServiceTest {

    @Mock
    private ProfilCapillaireRepository profilCapillaireRepository;
    @Mock
    private CompteRepository compteRepository;
    @Mock
    private SalonRepository salonRepository;
    @Mock
    private AffectationSalonRepository affectationSalonRepository;
    @Mock
    private ProfilCapillaireDTOResponseMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ProfilCapillaireService profilCapillaireService;

    private Compte clientCompte;
    private Salon salon;
    private AffectationSalon affectationCoiffeur;
    private ProfilCapillaire profil;

    @BeforeEach
    void setUp() {
        clientCompte = Compte.builder().id(1L).email("client@test.com").nom("Traore").prenom("Fatou").build();
        salon = Salon.builder().id(10L).slug("salon-elegance").statut(true).build();
        affectationCoiffeur = AffectationSalon.builder().id(20L).salon(salon).build();

        profil = ProfilCapillaire.builder()
                .id(100L)
                .compte(clientCompte)
                .typeCheveux(TypeCheveux.BOUCLES)
                .texture(TextureCheveux.MOYENS)
                .porosite(PorositeCheveux.MOYENNE)
                .codeProfil("hashedPin123456")
                .build();
    }

    @Test
    void getMonProfil_succes() {
        when(compteRepository.findByEmail("client@test.com")).thenReturn(Optional.of(clientCompte));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));

        ProfilCapillaireDTOResponse dto = new ProfilCapillaireDTOResponse(
                100L, 1L, "Traore", "Fatou", TypeCheveux.BOUCLES, TextureCheveux.MOYENS, PorositeCheveux.MOYENNE, null, null, true, null, null
        );
        when(mapper.apply(profil)).thenReturn(dto);

        ProfilCapillaireDTOResponse response = profilCapillaireService.getMonProfil("client@test.com");

        assertNotNull(response);
        assertEquals(TypeCheveux.BOUCLES, response.typeCheveux());
    }

    @Test
    void getMonProfil_inexistant_lanceException() {
        when(compteRepository.findByEmail("client@test.com")).thenReturn(Optional.of(clientCompte));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> profilCapillaireService.getMonProfil("client@test.com"));
    }

    @Test
    void enregistrerOuModifierProfil_succes() {
        when(compteRepository.findByEmail("client@test.com")).thenReturn(Optional.of(clientCompte));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));
        when(profilCapillaireRepository.save(any(ProfilCapillaire.class))).thenAnswer(i -> i.getArgument(0));

        ProfilCapillaireDTORequest request = new ProfilCapillaireDTORequest(
                TypeCheveux.CREPUS, TextureCheveux.EPAIS, PorositeCheveux.FAIBLE, CuirChevelu.SEC, EtatCheveux.SAIN
        );

        ProfilCapillaireDTOResponse dto = new ProfilCapillaireDTOResponse(
                100L, 1L, "Traore", "Fatou", TypeCheveux.CREPUS, TextureCheveux.EPAIS, PorositeCheveux.FAIBLE, CuirChevelu.SEC, EtatCheveux.SAIN, true, null, null
        );
        when(mapper.apply(any())).thenReturn(dto);

        ProfilCapillaireDTOResponse response = profilCapillaireService.enregistrerOuModifierProfil("client@test.com", request);

        assertNotNull(response);
        assertEquals(TypeCheveux.CREPUS, response.typeCheveux());
        verify(profilCapillaireRepository).save(profil);
    }

    @Test
    void definirOuChangerCodePin_succes() {
        when(compteRepository.findByEmail("client@test.com")).thenReturn(Optional.of(clientCompte));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));
        when(profilCapillaireRepository.save(any(ProfilCapillaire.class))).thenAnswer(i -> i.getArgument(0));
        when(passwordEncoder.encode("123456")).thenReturn("newHashedPin");

        CodeProfilDTORequest request = new CodeProfilDTORequest("123456");
        profilCapillaireService.definirOuChangerCodePin("client@test.com", request);

        assertEquals("newHashedPin", profil.getCodeProfil());
        verify(profilCapillaireRepository).save(profil);
    }

    @Test
    void consulterProfilClientParCoiffeur_succes() {
        when(salonRepository.findBySlug("salon-elegance")).thenReturn(Optional.of(salon));
        when(affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue("coiffeur@test.com", "salon-elegance"))
                .thenReturn(Optional.of(affectationCoiffeur));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));
        when(passwordEncoder.matches("123456", "hashedPin123456")).thenReturn(true);

        ProfilCapillaireDTOResponse dto = new ProfilCapillaireDTOResponse(
                100L, 1L, "Traore", "Fatou", TypeCheveux.BOUCLES, TextureCheveux.MOYENS, PorositeCheveux.MOYENNE, null, null, true, null, null
        );
        when(mapper.apply(profil)).thenReturn(dto);

        CodeProfilVerificationDTORequest request = new CodeProfilVerificationDTORequest("123456");
        ProfilCapillaireDTOResponse result = profilCapillaireService.consulterProfilClientParCoiffeur(
                "salon-elegance", 1L, request, "coiffeur@test.com");

        assertNotNull(result);
        assertEquals(TypeCheveux.BOUCLES, result.typeCheveux());
    }

    @Test
    void consulterProfilClientParCoiffeur_codeInvalide_lanceException() {
        when(salonRepository.findBySlug("salon-elegance")).thenReturn(Optional.of(salon));
        when(affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue("coiffeur@test.com", "salon-elegance"))
                .thenReturn(Optional.of(affectationCoiffeur));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));
        when(passwordEncoder.matches("wrongPin", "hashedPin123456")).thenReturn(false);

        CodeProfilVerificationDTORequest request = new CodeProfilVerificationDTORequest("wrongPin");

        assertThrows(IllegalArgumentException.class, () ->
                profilCapillaireService.consulterProfilClientParCoiffeur("salon-elegance", 1L, request, "coiffeur@test.com"));
    }

    @Test
    void consulterProfilClientParCoiffeur_codeNonConfigure_lanceException() {
        profil.setCodeProfil(null);
        when(salonRepository.findBySlug("salon-elegance")).thenReturn(Optional.of(salon));
        when(affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue("coiffeur@test.com", "salon-elegance"))
                .thenReturn(Optional.of(affectationCoiffeur));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));

        CodeProfilVerificationDTORequest request = new CodeProfilVerificationDTORequest("123456");

        assertThrows(IllegalArgumentException.class, () ->
                profilCapillaireService.consulterProfilClientParCoiffeur("salon-elegance", 1L, request, "coiffeur@test.com"));
    }
}
