package com.kadi_aon.mon_salon.profilcapillaire.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTORequest;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.entity.ProduitConseilCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.mapper.ProduitConseilDTOResponseMapper;
import com.kadi_aon.mon_salon.profilcapillaire.repository.ProduitConseilCapillaireRepository;

import com.kadi_aon.mon_salon.common.service.CloudinaryService;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilStatsDTOResponse;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProduitConseilCapillaireServiceTest {

    @Mock
    private ProduitConseilCapillaireRepository repository;

    @Mock
    private ProduitConseilDTOResponseMapper mapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private ProduitConseilCapillaireService service;

    private ProduitConseilCapillaire produit;
    private ProduitConseilDTOResponse responseDTO;

    @BeforeEach
    void setUp() {
        produit = ProduitConseilCapillaire.builder()
                .id(1L)
                .nom("Shampoing Nourrissant")
                .marque("Kady Care")
                .etapeRoutine(EtapeRoutine.LAVAGE)
                .cuirsChevelusCibles(Set.of(CuirChevelu.SEC))
                .etatsCheveuxCibles(Set.of(EtatCheveux.SEC, EtatCheveux.ABIME))
                .porositesCibles(Set.of(PorositeCheveux.FORTE))
                .typesCheveuxCibles(Set.of(TypeCheveux.CREPUS, TypeCheveux.FRISES))
                .texturesCibles(Set.of(TextureCheveux.EPAIS))
                .actif(true)
                .build();

        responseDTO = new ProduitConseilDTOResponse(
                1L, "Shampoing Nourrissant", "Kady Care", null, null, null,
                EtapeRoutine.LAVAGE, Set.of(CuirChevelu.SEC), Set.of(EtatCheveux.SEC),
                Set.of(PorositeCheveux.FORTE), Set.of(TypeCheveux.CREPUS), Set.of(TextureCheveux.EPAIS),
                true, null, null
        );
    }

    @Test
    void creerProduitConseil_succes() {
        ProduitConseilDTORequest request = new ProduitConseilDTORequest(
                "Shampoing Nourrissant", "Kady Care", "Nettoie en douceur", "Appliquer sur cuir chevelu",
                "http://img.png", EtapeRoutine.LAVAGE, Set.of(CuirChevelu.SEC), Set.of(EtatCheveux.SEC),
                Set.of(PorositeCheveux.FORTE), Set.of(TypeCheveux.CREPUS), Set.of(TextureCheveux.EPAIS), true
        );

        when(repository.save(any(ProduitConseilCapillaire.class))).thenReturn(produit);
        when(mapper.apply(produit)).thenReturn(responseDTO);

        ProduitConseilDTOResponse result = service.creerProduitConseil(request);

        assertNotNull(result);
        assertEquals("Shampoing Nourrissant", result.nom());
        verify(repository).save(any(ProduitConseilCapillaire.class));
    }

    @Test
    void modifierProduitConseil_succes() {
        ProduitConseilDTORequest request = new ProduitConseilDTORequest(
                "Shampoing Modifié", "Kady Care", "Description mise à jour", null,
                null, EtapeRoutine.LAVAGE, null, null, null, null, null, true
        );

        when(repository.findById(1L)).thenReturn(Optional.of(produit));
        when(repository.save(produit)).thenReturn(produit);
        when(mapper.apply(produit)).thenReturn(responseDTO);

        ProduitConseilDTOResponse result = service.modifierProduitConseil(1L, request);

        assertNotNull(result);
        verify(repository).save(produit);
    }

    @Test
    void basculerStatut_succes() {
        when(repository.findById(1L)).thenReturn(Optional.of(produit));
        when(repository.save(produit)).thenReturn(produit);
        when(mapper.apply(produit)).thenReturn(responseDTO);

        ProduitConseilDTOResponse result = service.basculerStatut(1L);

        assertNotNull(result);
        assertFalse(produit.isActif());
        verify(repository).save(produit);
    }

    @Test
    void supprimerProduitConseil_inexistant_lanceException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.supprimerProduitConseil(99L));
    }

    @Test
    void listerProduitsConseils_avecRecherche() {
        when(repository.findAll()).thenReturn(List.of(produit));
        when(mapper.apply(produit)).thenReturn(responseDTO);

        List<ProduitConseilDTOResponse> result = service.listerProduitsConseils(null, null, "shampoing");

        assertEquals(1, result.size());
        assertEquals("Shampoing Nourrissant", result.get(0).nom());
    }

    @Test
    void obtenirStatistiques_succes() {
        when(repository.findAll()).thenReturn(List.of(produit));

        ProduitConseilStatsDTOResponse stats = service.obtenirStatistiques();

        assertEquals(1, stats.total());
        assertEquals(1, stats.actifs());
        assertEquals(0, stats.inactifs());
        assertEquals(1L, stats.parEtape().get(EtapeRoutine.LAVAGE));
    }

    @Test
    void uploadImageProduitConseil_succes() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "fake-image".getBytes());
        when(repository.findById(1L)).thenReturn(Optional.of(produit));
        when(cloudinaryService.uploadImage(file, "produits-conseils")).thenReturn("https://cloudinary.com/test.webp");
        when(repository.save(produit)).thenReturn(produit);
        when(mapper.apply(produit)).thenReturn(responseDTO);

        ProduitConseilDTOResponse result = service.uploadImageProduitConseil(1L, file);

        assertNotNull(result);
        verify(cloudinaryService).uploadImage(file, "produits-conseils");
        verify(repository).save(produit);
    }
}
