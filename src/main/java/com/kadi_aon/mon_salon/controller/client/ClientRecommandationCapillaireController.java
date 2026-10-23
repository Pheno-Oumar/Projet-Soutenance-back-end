package com.kadi_aon.mon_salon.controller.client;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.kadi_aon.mon_salon.common.dto.response.APIResponse;
import com.kadi_aon.mon_salon.profilcapillaire.dto.RecommandationCapillaireDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.service.RecommandationCapillaireService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/client/recommandations-capillaires")
@PreAuthorize("@salonSecurity.isClient()")
@Tag(name = "Client - Recommandations Capillaires", description = "Génération et consultation de la routine capillaire personnalisée 4 étapes avec cooldown de 30 jours")
public class ClientRecommandationCapillaireController {

    private final RecommandationCapillaireService recommandationCapillaireService;

    public ClientRecommandationCapillaireController(RecommandationCapillaireService recommandationCapillaireService) {
        super();
        this.recommandationCapillaireService = recommandationCapillaireService;
    }

    @PostMapping("/generer")
    @Operation(summary = "Générer une routine capillaire personnalisée 4 étapes (Lavage, Soin profond, Hydratation, Finition)")
    public ResponseEntity<APIResponse<RecommandationCapillaireDTOResponse>> genererRecommandation(Principal principal) {
        RecommandationCapillaireDTOResponse response = recommandationCapillaireService.genererRecommandation(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Votre routine capillaire personnalisée a été générée avec succès", response));
    }

    @GetMapping("/derniere")
    @Operation(summary = "Consulter sa dernière routine capillaire générée")
    public ResponseEntity<APIResponse<RecommandationCapillaireDTOResponse>> getDerniereRecommandation(Principal principal) {
        RecommandationCapillaireDTOResponse response = recommandationCapillaireService.obtenirDerniereRecommandation(principal.getName());
        return ResponseEntity.ok(new APIResponse<>(true, "Dernière routine capillaire récupérée avec succès", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter les détails d'une recommandation capillaire spécifique")
    public ResponseEntity<APIResponse<RecommandationCapillaireDTOResponse>> getDetailRecommandation(
            @PathVariable Long id,
            Principal principal) {
        RecommandationCapillaireDTOResponse response = recommandationCapillaireService.obtenirRecommandation(id, principal.getName());
        return ResponseEntity.ok(new APIResponse<>(true, "Détails de la recommandation capillaire récupérés avec succès", response));
    }

    @GetMapping("/historique")
    @Operation(summary = "Consulter l'historique de ses diagnostics et recommandations capillaires (avec filtre optionnel par mois)")
    public ResponseEntity<APIResponse<List<RecommandationCapillaireDTOResponse>>> listerHistorique(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            Principal principal) {
        List<RecommandationCapillaireDTOResponse> response = recommandationCapillaireService.listerHistorique(principal.getName(), annee, mois);
        return ResponseEntity.ok(new APIResponse<>(true, "Historique des recommandations capillaires récupéré", response));
    }
}
