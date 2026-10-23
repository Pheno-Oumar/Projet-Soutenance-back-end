package com.kadi_aon.mon_salon.controller.public_api;

import java.util.List;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kadi_aon.mon_salon.avis.dto.AvisProduitDTOResponse;
import com.kadi_aon.mon_salon.avis.dto.AvisSalonDTOResponse;
import com.kadi_aon.mon_salon.avis.service.AvisSalonService;
import com.kadi_aon.mon_salon.coiffeur.dto.ProfilCoiffeurDTOResponse;
import com.kadi_aon.mon_salon.coiffeur.service.CoiffeurSalonService;
import com.kadi_aon.mon_salon.common.dto.response.APIResponse;
import com.kadi_aon.mon_salon.realisation.dto.RealisationDTOResponse;
import com.kadi_aon.mon_salon.realisation.service.RealisationSalonService;
import com.kadi_aon.mon_salon.rendezvous.dto.CreneauDisponibleDTOResponse;
import com.kadi_aon.mon_salon.rendezvous.dto.VitrineDisponibiliteDTORequest;
import com.kadi_aon.mon_salon.rendezvous.service.DisponibiliteService;
import com.kadi_aon.mon_salon.salon.dto.HoraireOuvertureDTOResponse;
import com.kadi_aon.mon_salon.salon.dto.SalonDTOResponse;
import com.kadi_aon.mon_salon.salon.dto.ServiceSalonDTOResponse;
import com.kadi_aon.mon_salon.salon.dto.VarianteServiceDTOResponse;
import com.kadi_aon.mon_salon.salon.service.ExploreSalonService;
import com.kadi_aon.mon_salon.salon.service.HoraireSalonService;
import com.kadi_aon.mon_salon.salon.service.ServiceSalonService;
import com.kadi_aon.mon_salon.stock.dto.CategorieProduitDTOResponse;
import com.kadi_aon.mon_salon.stock.dto.ProduitDTOResponse;
import com.kadi_aon.mon_salon.stock.service.StockSalonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/explore")
@Tag(name = "Exploration Plateforme (Visiteur Public)", description = "Endpoints publics permettant de découvrir et rechercher les salons, services, produits et réalisations de la plateforme")
public class ExploreController {

    private final ExploreSalonService exploreSalonService;
    private final ServiceSalonService serviceSalonService;
    private final StockSalonService stockSalonService;
    private final AvisSalonService avisSalonService;
    private final RealisationSalonService realisationSalonService;
    private final DisponibiliteService disponibiliteService;
    private final CoiffeurSalonService coiffeurSalonService;
    private final HoraireSalonService horaireSalonService;

    public ExploreController(
            ExploreSalonService exploreSalonService,
            ServiceSalonService serviceSalonService,
            StockSalonService stockSalonService,
            AvisSalonService avisSalonService,
            RealisationSalonService realisationSalonService,
            DisponibiliteService disponibiliteService,
            CoiffeurSalonService coiffeurSalonService,
            HoraireSalonService horaireSalonService) {
        super();
        this.exploreSalonService = exploreSalonService;
        this.serviceSalonService = serviceSalonService;
        this.stockSalonService = stockSalonService;
        this.avisSalonService = avisSalonService;
        this.realisationSalonService = realisationSalonService;
        this.disponibiliteService = disponibiliteService;
        this.coiffeurSalonService = coiffeurSalonService;
        this.horaireSalonService = horaireSalonService;
    }

    @GetMapping("/salons")
    @Operation(summary = "Lister les salons actifs avec pagination")
    public ResponseEntity<APIResponse<Page<SalonDTOResponse>>> listerSalons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SalonDTOResponse> salons = exploreSalonService.listerSalonsActifs(pageable);
        return ResponseEntity.ok(new APIResponse<>(true, "Liste des salons récupérée avec succès", salons));
    }

    @GetMapping("/salons/search")
    @Operation(summary = "Rechercher des salons par nom, ville ou description")
    public ResponseEntity<APIResponse<List<SalonDTOResponse>>> rechercherSalons(
            @RequestParam(required = false, defaultValue = "") String q) {

        List<SalonDTOResponse> resultats = exploreSalonService.rechercherSalons(q);
        return ResponseEntity.ok(new APIResponse<>(true, "Recherche de salons effectuée", resultats));
    }

    @GetMapping("/salons/nearby")
    @Operation(summary = "Recherche géolocalisée de salons dans un rayon donné (en km) via formule Haversine")
    public ResponseEntity<APIResponse<List<SalonDTOResponse>>> rechercherSalonsProches(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5.0") double rayonKm) {

        List<SalonDTOResponse> resultats = exploreSalonService.rechercherSalonsNearby(lat, lng, rayonKm);
        return ResponseEntity.ok(new APIResponse<>(true, "Salons à proximité récupérés avec succès", resultats));
    }

    @GetMapping("/salons/par-service")
    @Operation(summary = "Filtrer les salons proposant un service spécifique")
    public ResponseEntity<APIResponse<List<SalonDTOResponse>>> filtrerParService(
            @RequestParam Long serviceId) {

        List<SalonDTOResponse> resultats = exploreSalonService.rechercherSalonsParService(serviceId);
        return ResponseEntity.ok(new APIResponse<>(true, "Salons filtrés par prestation", resultats));
    }

    @GetMapping("/salons/{slugSalon}")
    @Operation(summary = "Consulter les informations publiques d'un salon")
    public ResponseEntity<APIResponse<SalonDTOResponse>> getSalonDetail(@PathVariable String slugSalon) {
        SalonDTOResponse salon = exploreSalonService.getSalonDetail(slugSalon);
        return ResponseEntity.ok(new APIResponse<>(true, "Détail du salon récupéré", salon));
    }

    @GetMapping("/salons/{slugSalon}/services")
    @Operation(summary = "Consulter le catalogue des services et variantes d'un salon")
    public ResponseEntity<APIResponse<List<ServiceSalonDTOResponse>>> getServicesSalon(@PathVariable String slugSalon) {
        List<ServiceSalonDTOResponse> services = serviceSalonService.listerServices(slugSalon, true);
        return ResponseEntity.ok(new APIResponse<>(true, "Services du salon récupérés avec succès", services));
    }

    @GetMapping("/salons/{slugSalon}/services/{serviceId}")
    @Operation(summary = "Consulter les détails d'un service précis d'un salon")
    public ResponseEntity<APIResponse<ServiceSalonDTOResponse>> getServiceDetail(
            @PathVariable String slugSalon,
            @PathVariable Long serviceId) {
        ServiceSalonDTOResponse service = serviceSalonService.getService(slugSalon, serviceId);
        return ResponseEntity.ok(new APIResponse<>(true, "Détails du service récupérés avec succès", service));
    }

    @GetMapping("/salons/{slugSalon}/services/{serviceId}/variantes")
    @Operation(summary = "Consulter la liste des variantes disponibles d'un service d'un salon")
    public ResponseEntity<APIResponse<List<VarianteServiceDTOResponse>>> getVariantesService(
            @PathVariable String slugSalon,
            @PathVariable Long serviceId) {
        List<VarianteServiceDTOResponse> variantes = serviceSalonService.listerVariantesService(slugSalon, serviceId, true);
        return ResponseEntity.ok(new APIResponse<>(true, "Variantes du service récupérées avec succès", variantes));
    }

    @GetMapping("/salons/{slugSalon}/categories-produits")
    @Operation(summary = "Consulter la liste des catégories de produits d'un salon précis")
    public ResponseEntity<APIResponse<List<CategorieProduitDTOResponse>>> getCategoriesProduitsSalon(@PathVariable String slugSalon) {
        List<CategorieProduitDTOResponse> categories = stockSalonService.listerCategories(slugSalon, true);
        return ResponseEntity.ok(new APIResponse<>(true, "Catégories de produits du salon récupérées avec succès", categories));
    }

    @GetMapping("/salons/{slugSalon}/categories-produits/{categorieId}")
    @Operation(summary = "Consulter les détails d'une catégorie de produit d'un salon avec ses produits")
    public ResponseEntity<APIResponse<CategorieProduitDTOResponse>> getCategorieProduitDetail(
            @PathVariable String slugSalon,
            @PathVariable Long categorieId) {
        CategorieProduitDTOResponse categorie = stockSalonService.obtenirCategorie(slugSalon, categorieId);
        return ResponseEntity.ok(new APIResponse<>(true, "Détail de la catégorie de produit récupéré avec succès", categorie));
    }

    @GetMapping("/salons/{slugSalon}/produits")
    @Operation(summary = "Consulter les produits en vente d'un salon précis (avec filtre optionnel par catégorie)")
    public ResponseEntity<APIResponse<List<ProduitDTOResponse>>> getProduitsSalon(
            @PathVariable String slugSalon,
            @RequestParam(required = false) Long categorieId) {
        List<ProduitDTOResponse> produits = stockSalonService.listerProduits(slugSalon, categorieId, true);
        return ResponseEntity.ok(new APIResponse<>(true, "Produits du salon récupérés avec succès", produits));
    }

    @GetMapping("/salons/{slugSalon}/produits/{produitId}")
    @Operation(summary = "Consulter les détails d'un produit précis d'un salon")
    public ResponseEntity<APIResponse<ProduitDTOResponse>> getProduitSalonDetail(
            @PathVariable String slugSalon,
            @PathVariable Long produitId) {
        ProduitDTOResponse produit = stockSalonService.obtenirProduit(slugSalon, produitId);
        return ResponseEntity.ok(new APIResponse<>(true, "Détails du produit récupérés avec succès", produit));
    }

    @GetMapping("/salons/{slugSalon}/avis")
    @Operation(summary = "Consulter les avis publiés sur un salon")
    public ResponseEntity<APIResponse<List<AvisSalonDTOResponse>>> getAvisSalon(@PathVariable String slugSalon) {
        List<AvisSalonDTOResponse> avis = avisSalonService.listerAvisPubliesSalon(slugSalon);
        return ResponseEntity.ok(new APIResponse<>(true, "Avis publiés du salon récupérés", avis));
    }

    @GetMapping("/salons/{slugSalon}/realisations")
    @Operation(summary = "Consulter les réalisations publiées d'un salon spécifique")
    public ResponseEntity<APIResponse<List<RealisationDTOResponse>>> getRealisationsSalon(@PathVariable String slugSalon) {
        List<RealisationDTOResponse> realisations = realisationSalonService.listerRealisationsPubliees(slugSalon);
        return ResponseEntity.ok(new APIResponse<>(true, "Réalisations du salon récupérées", realisations));
    }

    @GetMapping("/salons/{slugSalon}/coiffeurs")
    @Operation(summary = "Consulter l'équipe des coiffeurs du salon et leurs profils publics")
    public ResponseEntity<APIResponse<List<ProfilCoiffeurDTOResponse>>> getCoiffeursSalon(@PathVariable String slugSalon) {
        List<ProfilCoiffeurDTOResponse> coiffeurs = coiffeurSalonService.listerCoiffeursVitrine(slugSalon);
        return ResponseEntity.ok(new APIResponse<>(true, "Équipe des coiffeurs du salon récupérée", coiffeurs));
    }

    @GetMapping("/salons/{slugSalon}/horaires")
    @Operation(summary = "Consulter les horaires d'ouverture réguliers du salon")
    public ResponseEntity<APIResponse<List<HoraireOuvertureDTOResponse>>> getHorairesSalon(@PathVariable String slugSalon) {
        List<HoraireOuvertureDTOResponse> horaires = horaireSalonService.listerHoraires(slugSalon);
        return ResponseEntity.ok(new APIResponse<>(true, "Horaires d'ouverture du salon récupérés", horaires));
    }

    @PostMapping("/salons/{slugSalon}/disponibilites")
    @Operation(summary = "Consulter les créneaux disponibles d'un salon pour des variantes choisies (visiteur sans choix de coiffeur)")
    public ResponseEntity<APIResponse<List<CreneauDisponibleDTOResponse>>> getDisponibilitesSalon(
            @PathVariable String slugSalon,
            @Valid @RequestBody VitrineDisponibiliteDTORequest request) {

        List<CreneauDisponibleDTOResponse> creneaux = disponibiliteService.calculerDisponibilites(slugSalon, request.toDisponibiliteSearch());
        return ResponseEntity.ok(new APIResponse<>(true, "Créneaux disponibles récupérés", creneaux));
    }

    @GetMapping("/services")
    @Operation(summary = "Consulter le catalogue transversal des services proposés (tous salons confondus)")
    public ResponseEntity<APIResponse<List<ServiceSalonDTOResponse>>> listerServicesTransversal(
            @RequestParam(required = false) String q) {
        List<ServiceSalonDTOResponse> services = serviceSalonService.listerServicesTransversal(q);
        return ResponseEntity.ok(new APIResponse<>(true, "Catalogue des prestations récupéré avec succès", services));
    }

    @GetMapping("/produits")
    @Operation(summary = "Consulter le catalogue transversal des produits en vente (tous salons confondus)")
    public ResponseEntity<APIResponse<List<ProduitDTOResponse>>> listerProduitsTransversal(
            @RequestParam(required = false) Long categorieId) {

        List<ProduitDTOResponse> produits = stockSalonService.listerProduitsTransversal(categorieId);
        return ResponseEntity.ok(new APIResponse<>(true, "Catalogue des produits récupéré avec succès", produits));
    }

    @GetMapping("/produits/{produitId}")
    @Operation(summary = "Consulter les détails d'un produit spécifique (catalogue transversal)")
    public ResponseEntity<APIResponse<ProduitDTOResponse>> getProduitDetail(@PathVariable Long produitId) {
        ProduitDTOResponse produit = stockSalonService.obtenirProduitPublic(produitId);
        return ResponseEntity.ok(new APIResponse<>(true, "Détails du produit récupérés avec succès", produit));
    }

    @GetMapping("/produits/{produitId}/avis")
    @Operation(summary = "Consulter les avis publiés sur un produit")
    public ResponseEntity<APIResponse<List<AvisProduitDTOResponse>>> getAvisProduit(@PathVariable Long produitId) {
        List<AvisProduitDTOResponse> avis = avisSalonService.listerAvisPubliesProduit(produitId);
        return ResponseEntity.ok(new APIResponse<>(true, "Avis publiés du produit récupérés", avis));
    }

    @GetMapping("/realisations")
    @Operation(summary = "Consulter la galerie globale des réalisations publiées (tous salons confondus)")
    public ResponseEntity<APIResponse<List<RealisationDTOResponse>>> listerToutesRealisations() {
        List<RealisationDTOResponse> realisations = realisationSalonService.listerToutesLesRealisationsPubliees();
        return ResponseEntity.ok(new APIResponse<>(true, "Galerie transversale des réalisations récupérée", realisations));
    }

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @GetMapping("/media/proxy")
    @Operation(summary = "Proxy d'images haute performance (contourne les restrictions de pistage/stockage cross-origin des navigateurs)")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            URI uri = URI.create(url.trim());
            String host = uri.getHost();
            if (host == null || (!host.contains("cloudinary.com") && !host.contains("unsplash.com") && !host.contains("images.unsplash.com"))) {
                return ResponseEntity.badRequest().build();
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(12))
                    .header("User-Agent", "HairStyle-Proxy/1.0")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                String contentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse("image/webp");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=604800, immutable")
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                        .body(response.body());
            } else {
                return ResponseEntity.status(response.statusCode()).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
