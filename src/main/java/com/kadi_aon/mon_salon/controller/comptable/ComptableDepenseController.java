package com.kadi_aon.mon_salon.controller.comptable;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kadi_aon.mon_salon.common.dto.response.APIResponse;
import com.kadi_aon.mon_salon.depense.dto.DepenseCreateDTORequest;
import com.kadi_aon.mon_salon.depense.dto.DepenseDTOResponse;
import com.kadi_aon.mon_salon.depense.enums.CategorieDepense;
import com.kadi_aon.mon_salon.depense.service.DepenseSalonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/{slugSalon}/comptable/depenses")
@PreAuthorize("@salonSecurity.hasRoleInSalon(#slugSalon, 'COMPTABLE')")
@Tag(name = "Espace Comptable - Dépenses", description = "Enregistrement, consultation et annulation des dépenses financières du salon")
public class ComptableDepenseController {

    private final DepenseSalonService depenseSalonService;

    public ComptableDepenseController(DepenseSalonService depenseSalonService) {
        super();
        this.depenseSalonService = depenseSalonService;
    }

    @PostMapping
    @Operation(summary = "Enregistrer une dépense financière (avec sortie de caisse)")
    public ResponseEntity<APIResponse<DepenseDTOResponse>> creerDepense(
            @PathVariable String slugSalon,
            @Valid @RequestBody DepenseCreateDTORequest request,
            Principal principal) {
        DepenseDTOResponse response = depenseSalonService.creerDepense(slugSalon, principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Dépense enregistrée avec succès", response));
    }

    @GetMapping
    @Operation(summary = "Lister les dépenses financières du salon")
    public ResponseEntity<APIResponse<List<DepenseDTOResponse>>> listerDepenses(
            @PathVariable String slugSalon,
            @RequestParam(required = false) CategorieDepense categorie,
            @RequestParam(required = false) Boolean statut) {
        List<DepenseDTOResponse> response = depenseSalonService.listerDepenses(slugSalon, categorie, statut);
        return ResponseEntity.ok(new APIResponse<>(true, "Dépenses récupérées avec succès", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter le détail d'une dépense")
    public ResponseEntity<APIResponse<DepenseDTOResponse>> obtenirDepense(
            @PathVariable String slugSalon,
            @PathVariable Long id) {
        DepenseDTOResponse response = depenseSalonService.obtenirDepense(slugSalon, id);
        return ResponseEntity.ok(new APIResponse<>(true, "Dépense récupérée avec succès", response));
    }

    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler une dépense financière (désactivation logique)")
    public ResponseEntity<APIResponse<DepenseDTOResponse>> annulerDepense(
            @PathVariable String slugSalon,
            @PathVariable Long id,
            Principal principal) {
        DepenseDTOResponse response = depenseSalonService.annulerDepense(slugSalon, id, principal.getName());
        return ResponseEntity.ok(new APIResponse<>(true, "Dépense annulée avec succès", response));
    }

    @GetMapping("/en-attente-caisse")
    @Operation(summary = "Lister les dépenses en attente de décaissement en caisse")
    public ResponseEntity<APIResponse<List<DepenseDTOResponse>>> listerDepensesEnAttenteCaisse(
            @PathVariable String slugSalon) {
        List<DepenseDTOResponse> response = depenseSalonService.listerDepensesEnAttenteCaisse(slugSalon);
        return ResponseEntity.ok(new APIResponse<>(true, "Dépenses en attente de caisse récupérées avec succès", response));
    }

    @PostMapping("/{id}/valider-decaissement")
    @Operation(summary = "Valider le décaissement d'une dépense sur la session de caisse active")
    public ResponseEntity<APIResponse<DepenseDTOResponse>> validerDecaissement(
            @PathVariable String slugSalon,
            @PathVariable Long id,
            Principal principal) {
        DepenseDTOResponse response = depenseSalonService.validerDecaissementDepense(slugSalon, id, principal.getName());
        return ResponseEntity.ok(new APIResponse<>(true, "Décaissement validé sur la caisse active avec succès", response));
    }
}
