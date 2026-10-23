package com.kadi_aon.mon_salon.favori.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.coiffeur.entity.ProfilCoiffeur;
import com.kadi_aon.mon_salon.coiffeur.repository.ProfilCoiffeurRepository;
import com.kadi_aon.mon_salon.favori.dto.FavoriCoiffeurDTOResponse;
import com.kadi_aon.mon_salon.favori.dto.FavoriProduitDTOResponse;
import com.kadi_aon.mon_salon.favori.dto.FavoriSalonDTOResponse;
import com.kadi_aon.mon_salon.favori.dto.FavoriVarianteServiceDTOResponse;
import com.kadi_aon.mon_salon.favori.entity.FavoriCoiffeur;
import com.kadi_aon.mon_salon.favori.entity.FavoriProduit;
import com.kadi_aon.mon_salon.favori.entity.FavoriSalon;
import com.kadi_aon.mon_salon.favori.entity.FavoriVarianteService;
import com.kadi_aon.mon_salon.favori.repository.FavoriCoiffeurRepository;
import com.kadi_aon.mon_salon.favori.repository.FavoriProduitRepository;
import com.kadi_aon.mon_salon.favori.repository.FavoriSalonRepository;
import com.kadi_aon.mon_salon.favori.repository.FavoriVarianteServiceRepository;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.entity.ServiceSalon;
import com.kadi_aon.mon_salon.salon.entity.VarianteService;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;
import com.kadi_aon.mon_salon.salon.repository.VarianteServiceRepository;
import com.kadi_aon.mon_salon.stock.entity.Produit;
import com.kadi_aon.mon_salon.stock.repository.ProduitRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FavoriSalonService {

    private final FavoriSalonRepository favoriSalonRepository;
    private final FavoriCoiffeurRepository favoriCoiffeurRepository;
    private final FavoriVarianteServiceRepository favoriVarianteServiceRepository;
    private final FavoriProduitRepository favoriProduitRepository;
    private final AffectationSalonRepository affectationSalonRepository;
    private final SalonRepository salonRepository;
    private final VarianteServiceRepository varianteServiceRepository;
    private final ProduitRepository produitRepository;
    private final CompteRepository compteRepository;
    private final ProfilCoiffeurRepository profilCoiffeurRepository;
    private final AuditLogService auditLogService;

    public FavoriSalonService(
            FavoriSalonRepository favoriSalonRepository,
            FavoriCoiffeurRepository favoriCoiffeurRepository,
            FavoriVarianteServiceRepository favoriVarianteServiceRepository,
            FavoriProduitRepository favoriProduitRepository,
            AffectationSalonRepository affectationSalonRepository,
            SalonRepository salonRepository,
            VarianteServiceRepository varianteServiceRepository,
            ProduitRepository produitRepository,
            CompteRepository compteRepository,
            ProfilCoiffeurRepository profilCoiffeurRepository,
            AuditLogService auditLogService) {
        super();
        this.favoriSalonRepository = favoriSalonRepository;
        this.favoriCoiffeurRepository = favoriCoiffeurRepository;
        this.favoriVarianteServiceRepository = favoriVarianteServiceRepository;
        this.favoriProduitRepository = favoriProduitRepository;
        this.affectationSalonRepository = affectationSalonRepository;
        this.salonRepository = salonRepository;
        this.varianteServiceRepository = varianteServiceRepository;
        this.produitRepository = produitRepository;
        this.compteRepository = compteRepository;
        this.profilCoiffeurRepository = profilCoiffeurRepository;
        this.auditLogService = auditLogService;
    }

    // ==========================================
    // 1. SALON FAVORI
    // ==========================================

    @Transactional
    public FavoriSalonDTOResponse ajouterSalonFavori(String slugSalon, String clientEmail) {
        AffectationSalon affectationClient = validerClient(slugSalon, clientEmail);
        Compte client = affectationClient.getCompte();
        Salon salon = affectationClient.getSalon();

        FavoriSalon favori = favoriSalonRepository.findByClientIdAndSalonId(client.getId(), salon.getId())
                .orElseGet(() -> {
                    FavoriSalon f = FavoriSalon.builder()
                            .client(client)
                            .salon(salon)
                            .dateAjout(LocalDateTime.now())
                            .build();
                    FavoriSalon saved = favoriSalonRepository.save(f);

                    auditLogService.logActionSalon(
                            TypeActionAudit.CREATION,
                            "FavoriSalon",
                            String.valueOf(saved.getId()),
                            null,
                            "Ajout salon aux favoris",
                            affectationClient,
                            TypeRoleSalon.CLIENT.name()
                    );
                    log.info("Salon {} ajouté aux favoris du client {}", slugSalon, clientEmail);
                    return saved;
                });

        return mapSalonToResponse(favori);
    }

    @Transactional
    public void retirerSalonFavori(String slugSalon, String clientEmail) {
        AffectationSalon affectationClient = validerClient(slugSalon, clientEmail);
        Compte client = affectationClient.getCompte();
        Salon salon = affectationClient.getSalon();

        if (favoriSalonRepository.existsByClientIdAndSalonId(client.getId(), salon.getId())) {
            favoriSalonRepository.deleteByClientIdAndSalonId(client.getId(), salon.getId());

            auditLogService.logActionSalon(
                    TypeActionAudit.SUPPRESSION,
                    "FavoriSalon",
                    String.valueOf(salon.getId()),
                    "FAVORI",
                    "SUPPRIME",
                    affectationClient,
                    TypeRoleSalon.CLIENT.name()
            );
            log.info("Salon {} retiré des favoris du client {}", slugSalon, clientEmail);
        }
    }

    @Transactional(readOnly = true)
    public boolean isSalonFavori(String slugSalon, String clientEmail) {
        Compte c = compteRepository.findByIdentifiant(clientEmail)
                .or(() -> compteRepository.findByEmail(clientEmail))
                .or(() -> compteRepository.findByTelephone(clientEmail))
                .orElse(null);
        if (c != null) {
            Salon s = salonRepository.findBySlug(slugSalon).orElse(null);
            if (s != null) {
                return favoriSalonRepository.existsByClientIdAndSalonId(c.getId(), s.getId());
            }
        }
        return favoriSalonRepository.findByClientEmailAndSalonSlug(clientEmail, slugSalon).isPresent();
    }

    @Transactional(readOnly = true)
    public List<FavoriSalonDTOResponse> listerMesSalonsFavoris(String clientEmail) {
        Compte c = compteRepository.findByIdentifiant(clientEmail)
                .or(() -> compteRepository.findByEmail(clientEmail))
                .or(() -> compteRepository.findByTelephone(clientEmail))
                .orElse(null);
        if (c != null) {
            return favoriSalonRepository.findByClientIdOrderByDateAjoutDesc(c.getId()).stream()
                    .map(this::mapSalonToResponse)
                    .toList();
        }
        return favoriSalonRepository.findByClientEmailOrderByDateAjoutDesc(clientEmail).stream()
                .map(this::mapSalonToResponse)
                .toList();
    }

    // ==========================================
    // 2. COIFFEUR FAVORI
    // ==========================================

    @Transactional
    public FavoriCoiffeurDTOResponse ajouterCoiffeurFavori(String slugSalon, Long coiffeurId, String clientEmail) {
        AffectationSalon affectationClient = validerClient(slugSalon, clientEmail);
        Compte client = affectationClient.getCompte();
        Salon salon = affectationClient.getSalon();

        Compte coiffeur = compteRepository.findById(coiffeurId)
                .orElseThrow(() -> new EntityNotFoundException("Compte coiffeur introuvable ID " + coiffeurId));

        // Vérifier que le coiffeur a une affectation active avec le rôle COIFFEUR sur ce salon
        boolean isCoiffeurValide = affectationSalonRepository
                .findByCompteIdAndSalonSlugAndStatutTrue(coiffeur.getId(), slugSalon)
                .map(aff -> aff.getRoles().stream().anyMatch(r -> r.getRole() == TypeRoleSalon.COIFFEUR))
                .orElse(false);

        if (!isCoiffeurValide) {
            throw new IllegalArgumentException("L'employé ID " + coiffeurId + " n'a pas le rôle COIFFEUR actif dans ce salon.");
        }

        FavoriCoiffeur favori = favoriCoiffeurRepository.findByClientIdAndCoiffeurIdAndSalonId(client.getId(), coiffeur.getId(), salon.getId())
                .orElseGet(() -> {
                    FavoriCoiffeur f = FavoriCoiffeur.builder()
                            .client(client)
                            .coiffeur(coiffeur)
                            .salon(salon)
                            .dateAjout(LocalDateTime.now())
                            .build();
                    FavoriCoiffeur saved = favoriCoiffeurRepository.save(f);

                    auditLogService.logActionSalon(
                            TypeActionAudit.CREATION,
                            "FavoriCoiffeur",
                            String.valueOf(saved.getId()),
                            null,
                            "Ajout coiffeur ID " + coiffeur.getId() + " aux favoris",
                            affectationClient,
                            TypeRoleSalon.CLIENT.name()
                    );
                    log.info("Coiffeur {} ajouté aux favoris du client {} dans le salon {}", coiffeurId, clientEmail, slugSalon);
                    return saved;
                });

        return mapCoiffeurToResponse(favori);
    }

    @Transactional
    public void retirerCoiffeurFavori(String slugSalon, Long coiffeurId, String clientEmail) {
        AffectationSalon affectationClient = validerClient(slugSalon, clientEmail);
        Compte client = affectationClient.getCompte();
        Salon salon = affectationClient.getSalon();

        if (favoriCoiffeurRepository.existsByClientIdAndCoiffeurIdAndSalonId(client.getId(), coiffeurId, salon.getId())) {
            favoriCoiffeurRepository.deleteByClientIdAndCoiffeurIdAndSalonId(client.getId(), coiffeurId, salon.getId());

            auditLogService.logActionSalon(
                    TypeActionAudit.SUPPRESSION,
                    "FavoriCoiffeur",
                    String.valueOf(coiffeurId),
                    "FAVORI",
                    "SUPPRIME",
                    affectationClient,
                    TypeRoleSalon.CLIENT.name()
            );
            log.info("Coiffeur {} retiré des favoris du client {} dans le salon {}", coiffeurId, clientEmail, slugSalon);
        }
    }

    @Transactional(readOnly = true)
    public List<FavoriCoiffeurDTOResponse> listerMesCoiffeursFavoris(String slugSalon, String clientEmail) {
        AffectationSalon affectation = validerClient(slugSalon, clientEmail);
        return favoriCoiffeurRepository.findByClientIdAndSalonSlugOrderByDateAjoutDesc(affectation.getCompte().getId(), slugSalon).stream()
                .map(this::mapCoiffeurToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FavoriCoiffeurDTOResponse> listerTousMesCoiffeursFavoris(String clientEmail) {
        Compte c = findCompte(clientEmail);
        return favoriCoiffeurRepository.findByClientIdOrderByDateAjoutDesc(c.getId()).stream()
                .map(this::mapCoiffeurToResponse)
                .toList();
    }

    // ==========================================
    // 3. COIFFURES / VARIANTES SERVICES FAVORIS
    // ==========================================

    @Transactional
    public FavoriVarianteServiceDTOResponse ajouterVarianteFavori(Long varianteId, String identifiantClient) {
        Compte client = findCompte(identifiantClient);
        VarianteService variante = varianteServiceRepository.findById(varianteId)
                .orElseThrow(() -> new EntityNotFoundException("Variante de service / coiffure introuvable ID " + varianteId));

        FavoriVarianteService favori = favoriVarianteServiceRepository.findByClientIdAndVarianteId(client.getId(), variante.getId())
                .orElseGet(() -> {
                    FavoriVarianteService f = FavoriVarianteService.builder()
                            .client(client)
                            .variante(variante)
                            .dateAjout(LocalDateTime.now())
                            .build();
                    FavoriVarianteService saved = favoriVarianteServiceRepository.save(f);
                    log.info("Coiffure/Variante ID {} ajoutée aux favoris de {}", varianteId, identifiantClient);
                    return saved;
                });

        return mapVarianteToResponse(favori);
    }

    @Transactional
    public void retirerVarianteFavori(Long varianteId, String identifiantClient) {
        Compte client = findCompte(identifiantClient);
        if (favoriVarianteServiceRepository.existsByClientIdAndVarianteId(client.getId(), varianteId)) {
            favoriVarianteServiceRepository.deleteByClientIdAndVarianteId(client.getId(), varianteId);
            log.info("Coiffure/Variante ID {} retirée des favoris de {}", varianteId, identifiantClient);
        }
    }

    @Transactional(readOnly = true)
    public boolean isVarianteFavori(Long varianteId, String identifiantClient) {
        Compte client = findCompte(identifiantClient);
        return favoriVarianteServiceRepository.existsByClientIdAndVarianteId(client.getId(), varianteId);
    }

    @Transactional(readOnly = true)
    public List<FavoriVarianteServiceDTOResponse> listerMesVariantesFavorites(String identifiantClient) {
        Compte client = findCompte(identifiantClient);
        return favoriVarianteServiceRepository.findByClientIdOrderByDateAjoutDesc(client.getId()).stream()
                .map(this::mapVarianteToResponse)
                .toList();
    }

    // ==========================================
    // 4. PRODUITS FAVORIS
    // ==========================================

    @Transactional
    public FavoriProduitDTOResponse ajouterProduitFavori(Long produitId, String identifiantClient) {
        Compte client = findCompte(identifiantClient);
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable ID " + produitId));

        FavoriProduit favori = favoriProduitRepository.findByClientIdAndProduitId(client.getId(), produit.getId())
                .orElseGet(() -> {
                    FavoriProduit f = FavoriProduit.builder()
                            .client(client)
                            .produit(produit)
                            .dateAjout(LocalDateTime.now())
                            .build();
                    FavoriProduit saved = favoriProduitRepository.save(f);
                    log.info("Produit ID {} ajouté aux favoris de {}", produitId, identifiantClient);
                    return saved;
                });

        return mapProduitToResponse(favori);
    }

    @Transactional
    public void retirerProduitFavori(Long produitId, String identifiantClient) {
        Compte client = findCompte(identifiantClient);
        if (favoriProduitRepository.existsByClientIdAndProduitId(client.getId(), produitId)) {
            favoriProduitRepository.deleteByClientIdAndProduitId(client.getId(), produitId);
            log.info("Produit ID {} retiré des favoris de {}", produitId, identifiantClient);
        }
    }

    @Transactional(readOnly = true)
    public boolean isProduitFavori(Long produitId, String identifiantClient) {
        Compte client = findCompte(identifiantClient);
        return favoriProduitRepository.existsByClientIdAndProduitId(client.getId(), produitId);
    }

    @Transactional(readOnly = true)
    public List<FavoriProduitDTOResponse> listerMesProduitsFavoris(String identifiantClient) {
        Compte client = findCompte(identifiantClient);
        return favoriProduitRepository.findByClientIdOrderByDateAjoutDesc(client.getId()).stream()
                .map(this::mapProduitToResponse)
                .toList();
    }

    // ==========================================
    // HELPERS & MAPPERS
    // ==========================================

    private AffectationSalon validerClient(String slugSalon, String clientEmail) {
        AffectationSalon affectation = affectationSalonRepository
                .findByCompteEmailAndSalonSlugAndStatutTrue(clientEmail, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Aucune affectation active trouvée pour " + clientEmail + " dans le salon " + slugSalon));

        boolean isClient = affectation.getRoles().stream()
                .anyMatch(r -> r.getRole() == TypeRoleSalon.CLIENT);

        if (!isClient) {
            throw new IllegalArgumentException("L'utilisateur " + clientEmail + " n'a pas le rôle CLIENT dans ce salon.");
        }

        return affectation;
    }

    private FavoriSalonDTOResponse mapSalonToResponse(FavoriSalon f) {
        Salon s = f.getSalon();
        return new FavoriSalonDTOResponse(
                f.getId(),
                s != null ? s.getSlug() : null,
                s != null ? s.getNom() : null,
                s != null ? s.getLogoUrl() : null,
                f.getClient() != null ? f.getClient().getId() : null,
                f.getDateAjout()
        );
    }

    private FavoriCoiffeurDTOResponse mapCoiffeurToResponse(FavoriCoiffeur f) {
        Compte c = f.getCoiffeur();
        Salon s = f.getSalon();
        String coiffeurNomComplet = c != null ? c.getPrenom() + " " + c.getNom() : "Coiffeur inconnu";

        String photoUrl = null;
        if (c != null && s != null) {
            photoUrl = profilCoiffeurRepository.findByAffectationCompteIdAndAffectationSalonSlug(c.getId(), s.getSlug())
                    .or(() -> c.getEmail() != null ? profilCoiffeurRepository.findByAffectationCompteEmailAndAffectationSalonSlug(c.getEmail(), s.getSlug()) : Optional.empty())
                    .map(ProfilCoiffeur::getPhotoProfilUrl)
                    .orElse(null);
        }

        return new FavoriCoiffeurDTOResponse(
                f.getId(),
                c != null ? c.getId() : null,
                coiffeurNomComplet,
                photoUrl,
                s != null ? s.getSlug() : null,
                s != null ? s.getNom() : null,
                f.getClient() != null ? f.getClient().getId() : null,
                f.getDateAjout()
        );
    }

    private FavoriVarianteServiceDTOResponse mapVarianteToResponse(FavoriVarianteService f) {
        VarianteService v = f.getVariante();
        ServiceSalon s = (v != null) ? v.getServiceSalon() : null;
        Salon salon = (s != null) ? s.getSalon() : null;

        return new FavoriVarianteServiceDTOResponse(
                f.getId(),
                v != null ? v.getId() : null,
                v != null ? v.getNom() : null,
                s != null ? s.getId() : null,
                s != null ? s.getNom() : null,
                salon != null ? salon.getSlug() : null,
                salon != null ? salon.getNom() : null,
                v != null ? v.getPrix() : null,
                v != null ? v.getDureeMinutes() : null,
                f.getDateAjout()
        );
    }

    private FavoriProduitDTOResponse mapProduitToResponse(FavoriProduit f) {
        Produit p = f.getProduit();
        Salon salon = (p != null) ? p.getSalon() : null;

        return new FavoriProduitDTOResponse(
                f.getId(),
                p != null ? p.getId() : null,
                p != null ? p.getNom() : null,
                p != null ? p.getDescription() : null,
                p != null ? p.getPrixVente() : null,
                p != null ? p.getImageUrl() : null,
                p != null ? p.getImagesUrls() : null,
                salon != null ? salon.getSlug() : null,
                salon != null ? salon.getNom() : null,
                f.getDateAjout()
        );
    }

    private Compte findCompte(String identifiant) {
        return compteRepository.findByIdentifiant(identifiant)
                .or(() -> compteRepository.findByEmail(identifiant))
                .or(() -> compteRepository.findByTelephone(identifiant))
                .orElseThrow(() -> new EntityNotFoundException("Compte non trouvé pour l'identifiant : " + identifiant));
    }
}
