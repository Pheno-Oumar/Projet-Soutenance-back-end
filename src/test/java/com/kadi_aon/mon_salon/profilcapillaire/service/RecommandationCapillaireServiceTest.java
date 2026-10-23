package com.kadi_aon.mon_salon.profilcapillaire.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.notification.enums.TypeNotification;
import com.kadi_aon.mon_salon.notification.service.NotificationAppService;
import com.kadi_aon.mon_salon.profilcapillaire.dto.RecommandationCapillaireDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.entity.ProfilCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.entity.ProduitConseilCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.entity.RecommandationCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.DensiteCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.LongueurCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.mapper.RecommandationCapillaireDTOResponseMapper;
import com.kadi_aon.mon_salon.profilcapillaire.repository.ProfilCapillaireRepository;
import com.kadi_aon.mon_salon.profilcapillaire.repository.ProduitConseilCapillaireRepository;
import com.kadi_aon.mon_salon.profilcapillaire.repository.RecommandationCapillaireRepository;

@ExtendWith(MockitoExtension.class)
class RecommandationCapillaireServiceTest {

    @Mock
    private RecommandationCapillaireRepository recommandationRepository;

    @Mock
    private ProfilCapillaireRepository profilCapillaireRepository;

    @Mock
    private ProduitConseilCapillaireRepository produitConseilRepository;

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private RecommandationCapillaireDTOResponseMapper mapper;

    @Mock
    private NotificationAppService notificationAppService;

    @InjectMocks
    private RecommandationCapillaireService service;

    private Compte client;
    private ProfilCapillaire profil;
    private ProduitConseilCapillaire prodLavage;
    private ProduitConseilCapillaire prodSoin;
    private ProduitConseilCapillaire prodHydratation;
    private ProduitConseilCapillaire prodFinition;

    @BeforeEach
    void setUp() {
        client = Compte.builder().id(1L).email("client@test.com").telephone("+22370112233").build();

        profil = ProfilCapillaire.builder()
                .id(10L)
                .compte(client)
                .typeCheveux(TypeCheveux.CREPUS)
                .texture(TextureCheveux.EPAIS)
                .porosite(PorositeCheveux.FORTE)
                .cuirChevelu(CuirChevelu.SEC)
                .etatCheveux(EtatCheveux.SEC)
                .longueur(LongueurCheveux.MI_LONGS)
                .densite(DensiteCheveux.FORTE)
                .build();

        prodLavage = ProduitConseilCapillaire.builder()
                .id(101L).nom("Shampoing Doux").etapeRoutine(EtapeRoutine.LAVAGE)
                .cuirsChevelusCibles(Set.of(CuirChevelu.SEC))
                .etatsCheveuxCibles(Set.of(EtatCheveux.SEC))
                .porositesCibles(Set.of(PorositeCheveux.FORTE))
                .typesCheveuxCibles(Set.of(TypeCheveux.CREPUS))
                .texturesCibles(Set.of(TextureCheveux.EPAIS))
                .actif(true).build();

        prodSoin = ProduitConseilCapillaire.builder()
                .id(102L).nom("Masque Nutritif").etapeRoutine(EtapeRoutine.SOIN_PROFOND)
                .cuirsChevelusCibles(Set.of(CuirChevelu.SEC))
                .etatsCheveuxCibles(Set.of(EtatCheveux.SEC))
                .porositesCibles(Set.of(PorositeCheveux.FORTE))
                .typesCheveuxCibles(Set.of(TypeCheveux.CREPUS))
                .texturesCibles(Set.of(TextureCheveux.EPAIS))
                .actif(true).build();

        prodHydratation = ProduitConseilCapillaire.builder()
                .id(103L).nom("Leave-in Crème").etapeRoutine(EtapeRoutine.HYDRATATION)
                .cuirsChevelusCibles(Set.of(CuirChevelu.SEC))
                .etatsCheveuxCibles(Set.of(EtatCheveux.SEC))
                .porositesCibles(Set.of(PorositeCheveux.FORTE))
                .typesCheveuxCibles(Set.of(TypeCheveux.CREPUS))
                .texturesCibles(Set.of(TextureCheveux.EPAIS))
                .actif(true).build();

        prodFinition = ProduitConseilCapillaire.builder()
                .id(104L).nom("Huile de Scellage").etapeRoutine(EtapeRoutine.FINITION)
                .cuirsChevelusCibles(Set.of(CuirChevelu.SEC))
                .etatsCheveuxCibles(Set.of(EtatCheveux.SEC))
                .porositesCibles(Set.of(PorositeCheveux.FORTE))
                .typesCheveuxCibles(Set.of(TypeCheveux.CREPUS))
                .texturesCibles(Set.of(TextureCheveux.EPAIS))
                .actif(true).build();
    }

    @Test
    void genererRecommandation_succes_score100EtNotification() {
        when(compteRepository.findByIdentifiant("+22370112233")).thenReturn(Optional.of(client));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));
        when(recommandationRepository.findFirstByClientIdOrderByDateRecommandationDesc(1L)).thenReturn(Optional.empty());
        when(produitConseilRepository.findByActifTrue()).thenReturn(List.of(prodLavage, prodSoin, prodHydratation, prodFinition));
        when(recommandationRepository.save(any(RecommandationCapillaire.class))).thenAnswer(i -> i.getArgument(0));

        RecommandationCapillaireDTOResponse mockDto = new RecommandationCapillaireDTOResponse(
                1L, "REC-2026-TEST", LocalDateTime.now(), TypeCheveux.CREPUS, TextureCheveux.EPAIS,
                PorositeCheveux.FORTE, LongueurCheveux.MI_LONGS, DensiteCheveux.FORTE, CuirChevelu.SEC,
                EtatCheveux.SEC, null, null, null, null, 100, 100, 100, 100, 100, "Conseils", 30L
        );
        when(mapper.apply(any())).thenReturn(mockDto);

        RecommandationCapillaireDTOResponse result = service.genererRecommandation("+22370112233");

        assertNotNull(result);
        assertEquals(100, result.scoreGlobal());
        verify(notificationAppService).creerNotification(
                eq(client),
                any(String.class),
                any(String.class),
                eq(TypeNotification.RECOMMANDATION_CAPILLAIRE),
                any(String.class)
        );
    }

    @Test
    void genererRecommandation_cooldownActif_lanceException() {
        when(compteRepository.findByIdentifiant("client@test.com")).thenReturn(Optional.of(client));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));

        // Recommandation générée il y a seulement 10 jours
        RecommandationCapillaire recoRecente = RecommandationCapillaire.builder()
                .id(50L)
                .client(client)
                .dateRecommandation(LocalDateTime.now().minusDays(10))
                .build();
        when(recommandationRepository.findFirstByClientIdOrderByDateRecommandationDesc(1L)).thenReturn(Optional.of(recoRecente));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.genererRecommandation("client@test.com"));

        assertTrue(ex.getMessage().contains("30 jours"));
    }

    @Test
    void genererRecommandation_cooldownExpire_succes() {
        when(compteRepository.findByIdentifiant("client@test.com")).thenReturn(Optional.of(client));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.of(profil));

        // Recommandation ancienne de 35 jours (cooldown expiré)
        RecommandationCapillaire recoAncienne = RecommandationCapillaire.builder()
                .id(50L)
                .client(client)
                .dateRecommandation(LocalDateTime.now().minusDays(35))
                .build();
        when(recommandationRepository.findFirstByClientIdOrderByDateRecommandationDesc(1L)).thenReturn(Optional.of(recoAncienne));
        when(produitConseilRepository.findByActifTrue()).thenReturn(List.of(prodLavage, prodSoin, prodHydratation, prodFinition));
        when(recommandationRepository.save(any(RecommandationCapillaire.class))).thenAnswer(i -> i.getArgument(0));

        RecommandationCapillaireDTOResponse mockDto = new RecommandationCapillaireDTOResponse(
                1L, "REC-2026-TEST", LocalDateTime.now(), TypeCheveux.CREPUS, TextureCheveux.EPAIS,
                PorositeCheveux.FORTE, LongueurCheveux.MI_LONGS, DensiteCheveux.FORTE, CuirChevelu.SEC,
                EtatCheveux.SEC, null, null, null, null, 100, 100, 100, 100, 100, "Conseils", 30L
        );
        when(mapper.apply(any())).thenReturn(mockDto);

        RecommandationCapillaireDTOResponse result = service.genererRecommandation("client@test.com");
        assertNotNull(result);
    }

    @Test
    void genererRecommandation_sansProfil_lanceException() {
        when(compteRepository.findByIdentifiant("client@test.com")).thenReturn(Optional.of(client));
        when(profilCapillaireRepository.findByCompteId(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> service.genererRecommandation("client@test.com"));
    }

    @Test
    void calculerScoreAffinite_formulePondereeCorrecte() {
        // CuirChevelu (30), Etat (30), Porosite (20), Type (10), Texture (10) = 100
        int scoreParfait = service.calculerScoreAffinite(prodLavage, profil);
        assertEquals(100, scoreParfait);

        // Produit universel sans critères cibles (reçoit la moitié des points par défaut = 50)
        ProduitConseilCapillaire universel = ProduitConseilCapillaire.builder()
                .id(200L).nom("Universel").etapeRoutine(EtapeRoutine.LAVAGE).actif(true).build();
        int scoreUniversel = service.calculerScoreAffinite(universel, profil);
        assertEquals(50, scoreUniversel);
    }
}
