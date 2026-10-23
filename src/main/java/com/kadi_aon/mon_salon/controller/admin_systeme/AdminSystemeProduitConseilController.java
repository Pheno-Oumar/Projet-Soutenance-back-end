package com.kadi_aon.mon_salon.controller.admin_systeme;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.kadi_aon.mon_salon.common.dto.response.APIResponse;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTORequest;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.dto.ProduitConseilStatsDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.service.ProduitConseilCapillaireService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin-systeme/produits-conseils")
@PreAuthorize("hasRole('ADMIN_SYSTEME')")
@Tag(name = "Admin Système - Produits Conseils Capillaires", description = "Gestion du catalogue de produits de soins et conseils pour les recommandations capillaires")
public class AdminSystemeProduitConseilController {

    private final ProduitConseilCapillaireService produitConseilService;

    public AdminSystemeProduitConseilController(ProduitConseilCapillaireService produitConseilService) {
        super();
        this.produitConseilService = produitConseilService;
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau produit de conseil capillaire")
    public ResponseEntity<APIResponse<ProduitConseilDTOResponse>> creer(
            @Valid @RequestBody ProduitConseilDTORequest request) {
        ProduitConseilDTOResponse response = produitConseilService.creerProduitConseil(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Produit de conseil capillaire créé avec succès", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un produit de conseil capillaire")
    public ResponseEntity<APIResponse<ProduitConseilDTOResponse>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ProduitConseilDTORequest request) {
        ProduitConseilDTOResponse response = produitConseilService.modifierProduitConseil(id, request);
        return ResponseEntity.ok(new APIResponse<>(true, "Produit de conseil capillaire modifié avec succès", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un produit de conseil capillaire")
    public ResponseEntity<APIResponse<ProduitConseilDTOResponse>> obtenir(
            @PathVariable Long id) {
        ProduitConseilDTOResponse response = produitConseilService.obtenirProduitConseil(id);
        return ResponseEntity.ok(new APIResponse<>(true, "Produit récupéré avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister les produits de conseils capillaires")
    public ResponseEntity<APIResponse<List<ProduitConseilDTOResponse>>> lister(
            @RequestParam(required = false) Boolean actif,
            @RequestParam(required = false) EtapeRoutine etapeRoutine,
            @RequestParam(required = false) String recherche) {
        List<ProduitConseilDTOResponse> response = produitConseilService.listerProduitsConseils(actif, etapeRoutine, recherche);
        return ResponseEntity.ok(new APIResponse<>(true, "Liste des produits conseils capillaires récupérée avec succès", response));
    }

    @GetMapping("/statistiques")
    @Operation(summary = "Obtenir les statistiques globales des produits de conseils capillaires")
    public ResponseEntity<APIResponse<ProduitConseilStatsDTOResponse>> obtenirStatistiques() {
        ProduitConseilStatsDTOResponse response = produitConseilService.obtenirStatistiques();
        return ResponseEntity.ok(new APIResponse<>(true, "Statistiques des produits de conseils récupérées avec succès", response));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Téléverser une image pour un produit de conseil capillaire")
    public ResponseEntity<APIResponse<ProduitConseilDTOResponse>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        ProduitConseilDTOResponse response = produitConseilService.uploadImageProduitConseil(id, file);
        return ResponseEntity.ok(new APIResponse<>(true, "Image téléversée avec succès", response));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Basculer le statut actif/inactif d'un produit de conseil")
    public ResponseEntity<APIResponse<ProduitConseilDTOResponse>> basculerStatut(
            @PathVariable Long id) {
        ProduitConseilDTOResponse response = produitConseilService.basculerStatut(id);
        return ResponseEntity.ok(new APIResponse<>(true, "Statut du produit modifié avec succès", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit de conseil capillaire")
    public ResponseEntity<APIResponse<Void>> supprimer(
            @PathVariable Long id) {
        produitConseilService.supprimerProduitConseil(id);
        return ResponseEntity.ok(new APIResponse<>(true, "Produit de conseil supprimé avec succès", null));
    }
}
