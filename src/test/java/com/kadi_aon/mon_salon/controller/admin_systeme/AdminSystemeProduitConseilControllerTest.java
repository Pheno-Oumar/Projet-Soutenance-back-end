package com.kadi_aon.mon_salon.controller.admin_systeme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.kadi_aon.mon_salon.common.dto.response.APIResponse;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTORequest;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilStatsDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.service.ProduitConseilCapillaireService;

@ExtendWith(MockitoExtension.class)
class AdminSystemeProduitConseilControllerTest {

    @Mock
    private ProduitConseilCapillaireService produitConseilService;

    @InjectMocks
    private AdminSystemeProduitConseilController controller;

    private ProduitConseilDTOResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new ProduitConseilDTOResponse(
                1L, "Shampoing Purifiant", "Kady Care", "Nettoyage doux", "Sur cheveux mouillés",
                "https://res.cloudinary.com/test.webp", EtapeRoutine.LAVAGE,
                Set.of(CuirChevelu.NORMAL), Set.of(EtatCheveux.SAIN),
                Set.of(PorositeCheveux.MOYENNE), Set.of(TypeCheveux.BOUCLES),
                Set.of(TextureCheveux.MOYENS), true, null, null
        );
    }

    @Test
    void creer_retourne201() {
        ProduitConseilDTORequest request = new ProduitConseilDTORequest(
                "Shampoing Purifiant", "Kady Care", "Nettoyage doux", "Sur cheveux mouillés",
                "https://res.cloudinary.com/test.webp", EtapeRoutine.LAVAGE,
                Set.of(CuirChevelu.NORMAL), Set.of(EtatCheveux.SAIN),
                Set.of(PorositeCheveux.MOYENNE), Set.of(TypeCheveux.BOUCLES),
                Set.of(TextureCheveux.MOYENS), true
        );

        when(produitConseilService.creerProduitConseil(request)).thenReturn(sampleResponse);

        ResponseEntity<APIResponse<ProduitConseilDTOResponse>> response = controller.creer(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Shampoing Purifiant", response.getBody().getData().nom());
    }

    @Test
    void modifier_retourne200() {
        ProduitConseilDTORequest request = new ProduitConseilDTORequest(
                "Shampoing Modifié", "Kady Care", "Nettoyage doux", "Sur cheveux mouillés",
                null, EtapeRoutine.LAVAGE, null, null, null, null, null, true
        );

        when(produitConseilService.modifierProduitConseil(1L, request)).thenReturn(sampleResponse);

        ResponseEntity<APIResponse<ProduitConseilDTOResponse>> response = controller.modifier(1L, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(produitConseilService).modifierProduitConseil(1L, request);
    }

    @Test
    void lister_retourneListe() {
        when(produitConseilService.listerProduitsConseils(true, EtapeRoutine.LAVAGE, "shampoing"))
                .thenReturn(List.of(sampleResponse));

        ResponseEntity<APIResponse<List<ProduitConseilDTOResponse>>> response =
                controller.lister(true, EtapeRoutine.LAVAGE, "shampoing");

        assertNotNull(response);
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void obtenirStatistiques_retourneStats() {
        ProduitConseilStatsDTOResponse stats = new ProduitConseilStatsDTOResponse(
                5, 4, 1, Map.of(EtapeRoutine.LAVAGE, 2L, EtapeRoutine.HYDRATATION, 3L)
        );
        when(produitConseilService.obtenirStatistiques()).thenReturn(stats);

        ResponseEntity<APIResponse<ProduitConseilStatsDTOResponse>> response = controller.obtenirStatistiques();

        assertNotNull(response);
        assertEquals(5, response.getBody().getData().total());
        assertEquals(4, response.getBody().getData().actifs());
    }

    @Test
    void uploadImage_retourneProduitMisAJour() {
        MockMultipartFile file = new MockMultipartFile("file", "image.webp", "image/webp", "bytes".getBytes());
        when(produitConseilService.uploadImageProduitConseil(1L, file)).thenReturn(sampleResponse);

        ResponseEntity<APIResponse<ProduitConseilDTOResponse>> response = controller.uploadImage(1L, file);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(produitConseilService).uploadImageProduitConseil(1L, file);
    }

    @Test
    void basculerStatut_succes() {
        when(produitConseilService.basculerStatut(1L)).thenReturn(sampleResponse);

        ResponseEntity<APIResponse<ProduitConseilDTOResponse>> response = controller.basculerStatut(1L);

        assertNotNull(response);
        verify(produitConseilService).basculerStatut(1L);
    }

    @Test
    void supprimer_succes() {
        ResponseEntity<APIResponse<Void>> response = controller.supprimer(1L);

        assertNotNull(response);
        verify(produitConseilService).supprimerProduitConseil(1L);
    }
}
