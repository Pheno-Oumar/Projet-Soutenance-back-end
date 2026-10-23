package com.kadi_aon.mon_salon.profilcapillaire.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kadi_aon.mon_salon.common.service.CloudinaryService;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTORequest;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilStatsDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.entity.ProduitConseilCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.mapper.ProduitConseilDTOResponseMapper;
import com.kadi_aon.mon_salon.profilcapillaire.repository.ProduitConseilCapillaireRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProduitConseilCapillaireService {

    private final ProduitConseilCapillaireRepository repository;
    private final ProduitConseilDTOResponseMapper mapper;
    private final CloudinaryService cloudinaryService;

    public ProduitConseilCapillaireService(
            ProduitConseilCapillaireRepository repository,
            ProduitConseilDTOResponseMapper mapper,
            CloudinaryService cloudinaryService) {
        super();
        this.repository = repository;
        this.mapper = mapper;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    public ProduitConseilDTOResponse creerProduitConseil(ProduitConseilDTORequest request) {
        ProduitConseilCapillaire p = ProduitConseilCapillaire.builder()
                .nom(request.nom())
                .marque(request.marque())
                .description(request.description())
                .modeUtilisation(request.modeUtilisation())
                .imageUrl(request.imageUrl())
                .etapeRoutine(request.etapeRoutine())
                .cuirsChevelusCibles(request.cuirsChevelusCibles() != null ? new HashSet<>(request.cuirsChevelusCibles()) : new HashSet<>())
                .etatsCheveuxCibles(request.etatsCheveuxCibles() != null ? new HashSet<>(request.etatsCheveuxCibles()) : new HashSet<>())
                .porositesCibles(request.porositesCibles() != null ? new HashSet<>(request.porositesCibles()) : new HashSet<>())
                .typesCheveuxCibles(request.typesCheveuxCibles() != null ? new HashSet<>(request.typesCheveuxCibles()) : new HashSet<>())
                .texturesCibles(request.texturesCibles() != null ? new HashSet<>(request.texturesCibles()) : new HashSet<>())
                .alternativeNaturelleNom(request.alternativeNaturelleNom())
                .alternativeNaturelleIngredients(request.alternativeNaturelleIngredients())
                .alternativeNaturelleRecette(request.alternativeNaturelleRecette())
                .actif(request.actif() != null ? request.actif() : true)
                .build();

        ProduitConseilCapillaire saved = repository.save(p);
        log.info("Produit conseil capillaire créé avec succès (ID: {}, Nom: {})", saved.getId(), saved.getNom());
        return mapper.apply(saved);
    }

    @Transactional
    public ProduitConseilDTOResponse modifierProduitConseil(Long id, ProduitConseilDTORequest request) {
        ProduitConseilCapillaire p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit conseil capillaire introuvable avec l'ID : " + id));

        p.setNom(request.nom());
        p.setMarque(request.marque());
        p.setDescription(request.description());
        p.setModeUtilisation(request.modeUtilisation());
        p.setImageUrl(request.imageUrl());
        p.setEtapeRoutine(request.etapeRoutine());
        p.setAlternativeNaturelleNom(request.alternativeNaturelleNom());
        p.setAlternativeNaturelleIngredients(request.alternativeNaturelleIngredients());
        p.setAlternativeNaturelleRecette(request.alternativeNaturelleRecette());

        if (request.cuirsChevelusCibles() != null) {
            p.setCuirsChevelusCibles(new HashSet<>(request.cuirsChevelusCibles()));
        }
        if (request.etatsCheveuxCibles() != null) {
            p.setEtatsCheveuxCibles(new HashSet<>(request.etatsCheveuxCibles()));
        }
        if (request.porositesCibles() != null) {
            p.setPorositesCibles(new HashSet<>(request.porositesCibles()));
        }
        if (request.typesCheveuxCibles() != null) {
            p.setTypesCheveuxCibles(new HashSet<>(request.typesCheveuxCibles()));
        }
        if (request.texturesCibles() != null) {
            p.setTexturesCibles(new HashSet<>(request.texturesCibles()));
        }
        if (request.actif() != null) {
            p.setActif(request.actif());
        }

        ProduitConseilCapillaire saved = repository.save(p);
        log.info("Produit conseil capillaire modifié avec succès (ID: {})", saved.getId());
        return mapper.apply(saved);
    }

    @Transactional(readOnly = true)
    public ProduitConseilDTOResponse obtenirProduitConseil(Long id) {
        ProduitConseilCapillaire p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit conseil capillaire introuvable avec l'ID : " + id));
        return mapper.apply(p);
    }

    @Transactional(readOnly = true)
    public List<ProduitConseilDTOResponse> listerProduitsConseils(Boolean actif, EtapeRoutine etapeRoutine) {
        return listerProduitsConseils(actif, etapeRoutine, null);
    }

    @Transactional(readOnly = true)
    public List<ProduitConseilDTOResponse> listerProduitsConseils(Boolean actif, EtapeRoutine etapeRoutine, String recherche) {
        return repository.findAll().stream()
                .filter(p -> actif == null || p.isActif() == actif)
                .filter(p -> etapeRoutine == null || p.getEtapeRoutine() == etapeRoutine)
                .filter(p -> {
                    if (recherche == null || recherche.isBlank()) return true;
                    String q = recherche.toLowerCase().trim();
                    return (p.getNom() != null && p.getNom().toLowerCase().contains(q)) ||
                           (p.getMarque() != null && p.getMarque().toLowerCase().contains(q)) ||
                           (p.getDescription() != null && p.getDescription().toLowerCase().contains(q));
                })
                .map(mapper)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProduitConseilStatsDTOResponse obtenirStatistiques() {
        List<ProduitConseilCapillaire> tous = repository.findAll();
        long total = tous.size();
        long actifs = tous.stream().filter(ProduitConseilCapillaire::isActif).count();
        long inactifs = total - actifs;

        Map<EtapeRoutine, Long> parEtape = Arrays.stream(EtapeRoutine.values())
                .collect(Collectors.toMap(
                        etape -> etape,
                        etape -> tous.stream().filter(p -> p.getEtapeRoutine() == etape).count()
                ));

        return new ProduitConseilStatsDTOResponse(total, actifs, inactifs, parEtape);
    }

    @Transactional
    public ProduitConseilDTOResponse uploadImageProduitConseil(Long id, MultipartFile file) {
        ProduitConseilCapillaire p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit conseil capillaire introuvable avec l'ID : " + id));

        try {
            if (p.getImageUrl() != null && !p.getImageUrl().isBlank()) {
                cloudinaryService.deleteMediaByUrl(p.getImageUrl(), "image");
            }
            String url = cloudinaryService.uploadImage(file, "produits-conseils");
            p.setImageUrl(url);
            ProduitConseilCapillaire saved = repository.save(p);
            log.info("Image uploadée avec succès pour le produit conseil capillaire {}", id);
            return mapper.apply(saved);
        } catch (IOException e) {
            log.error("Erreur lors de l'upload de l'image pour le produit conseil capillaire {}", id, e);
            throw new RuntimeException("Erreur lors du téléversement de l'image : " + e.getMessage(), e);
        }
    }

    @Transactional
    public ProduitConseilDTOResponse basculerStatut(Long id) {
        ProduitConseilCapillaire p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit conseil capillaire introuvable avec l'ID : " + id));

        p.setActif(!p.isActif());
        ProduitConseilCapillaire saved = repository.save(p);
        log.info("Statut du produit conseil capillaire {} basculé à {}", id, saved.isActif());
        return mapper.apply(saved);
    }

    @Transactional
    public void supprimerProduitConseil(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Produit conseil capillaire introuvable avec l'ID : " + id);
        }
        ProduitConseilCapillaire p = repository.findById(id).orElse(null);
        if (p != null && p.getImageUrl() != null && !p.getImageUrl().isBlank()) {
            cloudinaryService.deleteMediaByUrl(p.getImageUrl(), "image");
        }
        repository.deleteById(id);
        log.info("Produit conseil capillaire supprimé (ID: {})", id);
    }
}
