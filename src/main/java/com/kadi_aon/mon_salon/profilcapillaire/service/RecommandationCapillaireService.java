package com.kadi_aon.mon_salon.profilcapillaire.service;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.notification.enums.TypeNotification;
import com.kadi_aon.mon_salon.notification.service.NotificationAppService;
import com.kadi_aon.mon_salon.profilcapillaire.dto.RecommandationCapillaireDTOResponse;
import com.kadi_aon.mon_salon.profilcapillaire.entity.ProfilCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.entity.ProduitConseilCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.entity.RecommandationCapillaire;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
import com.kadi_aon.mon_salon.profilcapillaire.mapper.RecommandationCapillaireDTOResponseMapper;
import com.kadi_aon.mon_salon.profilcapillaire.repository.ProfilCapillaireRepository;
import com.kadi_aon.mon_salon.profilcapillaire.repository.ProduitConseilCapillaireRepository;
import com.kadi_aon.mon_salon.profilcapillaire.repository.RecommandationCapillaireRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecommandationCapillaireService {

    private final RecommandationCapillaireRepository recommandationRepository;
    private final ProfilCapillaireRepository profilCapillaireRepository;
    private final ProduitConseilCapillaireRepository produitConseilRepository;
    private final CompteRepository compteRepository;
    private final RecommandationCapillaireDTOResponseMapper mapper;
    private final NotificationAppService notificationAppService;

    public RecommandationCapillaireService(
            RecommandationCapillaireRepository recommandationRepository,
            ProfilCapillaireRepository profilCapillaireRepository,
            ProduitConseilCapillaireRepository produitConseilRepository,
            CompteRepository compteRepository,
            RecommandationCapillaireDTOResponseMapper mapper,
            NotificationAppService notificationAppService) {
        super();
        this.recommandationRepository = recommandationRepository;
        this.profilCapillaireRepository = profilCapillaireRepository;
        this.produitConseilRepository = produitConseilRepository;
        this.compteRepository = compteRepository;
        this.mapper = mapper;
        this.notificationAppService = notificationAppService;
    }

    private Compte getCompteByIdentifiant(String identifiant) {
        return compteRepository.findByIdentifiant(identifiant)
                .or(() -> compteRepository.findByEmail(identifiant))
                .or(() -> compteRepository.findByTelephone(identifiant))
                .orElseThrow(() -> new EntityNotFoundException("Compte non trouvé pour l'identifiant : " + identifiant));
    }

    @Transactional
    public RecommandationCapillaireDTOResponse genererRecommandation(String clientIdentifiant) {
        Compte compte = getCompteByIdentifiant(clientIdentifiant);

        ProfilCapillaire profil = profilCapillaireRepository.findByCompteId(compte.getId())
                .orElseThrow(() -> new IllegalStateException("Veuillez d'abord compléter votre diagnostic capillaire avant de générer une recommandation."));

        // Vérification stricte du cooldown de 30 jours
        recommandationRepository.findFirstByClientIdOrderByDateRecommandationDesc(compte.getId())
                .ifPresent(derniere -> {
                    LocalDateTime dateAutorisee = derniere.getDateRecommandation().plusDays(30);
                    if (LocalDateTime.now().isBefore(dateAutorisee)) {
                        long joursRestants = Math.max(1, ChronoUnit.DAYS.between(LocalDateTime.now(), dateAutorisee));
                        throw new IllegalStateException("Un délai de 30 jours est requis entre deux diagnostics capillaires. Votre prochain diagnostic sera disponible dans " + joursRestants + " jour(s).");
                    }
                });

        List<ProduitConseilCapillaire> produitsActifs = produitConseilRepository.findByActifTrue();
        if (produitsActifs.isEmpty()) {
            throw new IllegalStateException("Le catalogue de produits conseils capillaires est actuellement vide.");
        }

        // Calcul des scores d'affinité pour chaque étape
        ScoredProduct bestLavage = selectBestProduct(produitsActifs, EtapeRoutine.LAVAGE, profil);
        ScoredProduct bestSoin = selectBestProduct(produitsActifs, EtapeRoutine.SOIN_PROFOND, profil);
        ScoredProduct bestHydratation = selectBestProduct(produitsActifs, EtapeRoutine.HYDRATATION, profil);
        ScoredProduct bestFinition = selectBestProduct(produitsActifs, EtapeRoutine.FINITION, profil);

        int scoreGlobal = (bestLavage.score + bestSoin.score + bestHydratation.score + bestFinition.score) / 4;

        String reference = "REC-" + Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        RecommandationCapillaire reco = RecommandationCapillaire.builder()
                .reference(reference)
                .client(compte)
                .dateRecommandation(LocalDateTime.now())
                .typeCheveux(profil.getTypeCheveux())
                .texture(profil.getTexture())
                .porosite(profil.getPorosite())
                .cuirChevelu(profil.getCuirChevelu())
                .etatCheveux(profil.getEtatCheveux())
                .produitLavage(bestLavage.produit)
                .produitSoinProfond(bestSoin.produit)
                .produitHydratation(bestHydratation.produit)
                .produitFinition(bestFinition.produit)
                .scoreLavage(bestLavage.score)
                .scoreSoinProfond(bestSoin.score)
                .scoreHydratation(bestHydratation.score)
                .scoreFinition(bestFinition.score)
                .scoreGlobal(scoreGlobal)
                .build();

        RecommandationCapillaire saved = recommandationRepository.save(reco);

        // Notification In-App
        notificationAppService.creerNotification(
                compte,
                "Votre routine capillaire personnalisée est prête !",
                "Découvrez votre routine en 4 étapes adaptée à votre profil capillaire (Score d'affinité : " + scoreGlobal + "%).",
                TypeNotification.RECOMMANDATION_CAPILLAIRE,
                reference
        );

        log.info("Recommandation capillaire générée avec succès pour le client {} (Ref: {}, Score: {}%)",
                compte.getIdentifiant(), reference, scoreGlobal);

        return mapper.apply(saved);
    }

    @Transactional(readOnly = true)
    public RecommandationCapillaireDTOResponse obtenirDerniereRecommandation(String clientIdentifiant) {
        Compte compte = getCompteByIdentifiant(clientIdentifiant);
        RecommandationCapillaire derniere = recommandationRepository.findFirstByClientIdOrderByDateRecommandationDesc(compte.getId())
                .orElseThrow(() -> new EntityNotFoundException("Aucune recommandation capillaire trouvée pour ce compte."));
        return mapper.apply(derniere);
    }

    @Transactional(readOnly = true)
    public RecommandationCapillaireDTOResponse obtenirRecommandation(Long id, String clientIdentifiant) {
        Compte compte = getCompteByIdentifiant(clientIdentifiant);
        RecommandationCapillaire reco = recommandationRepository.findByIdAndClientId(id, compte.getId())
                .orElseThrow(() -> new EntityNotFoundException("Recommandation capillaire introuvable avec l'ID : " + id));
        return mapper.apply(reco);
    }

    @Transactional(readOnly = true)
    public List<RecommandationCapillaireDTOResponse> listerHistorique(String clientIdentifiant) {
        return listerHistorique(clientIdentifiant, null, null);
    }

    @Transactional(readOnly = true)
    public List<RecommandationCapillaireDTOResponse> listerHistorique(String clientIdentifiant, Integer annee, Integer mois) {
        Compte compte = getCompteByIdentifiant(clientIdentifiant);
        List<RecommandationCapillaire> list;
        if (annee != null && mois != null) {
            list = recommandationRepository.findByClientIdAndAnneeAndMoisOrderByDateRecommandationDesc(compte.getId(), annee, mois);
        } else {
            list = recommandationRepository.findByClientIdOrderByDateRecommandationDesc(compte.getId());
        }
        return list.stream()
                .map(mapper)
                .toList();
    }

    public int calculerScoreAffinite(ProduitConseilCapillaire p, ProfilCapillaire profil) {
        int score = 0;

        // 1. Cuir chevelu (30 points)
        if (profil.getCuirChevelu() != null) {
            if (p.getCuirsChevelusCibles().contains(profil.getCuirChevelu())) {
                score += 30;
            } else if (p.getCuirsChevelusCibles().isEmpty()) {
                score += 15; // Produit universel
            }
        } else {
            score += 15;
        }

        // 2. État des cheveux (30 points)
        if (profil.getEtatCheveux() != null) {
            if (p.getEtatsCheveuxCibles().contains(profil.getEtatCheveux())) {
                score += 30;
            } else if (p.getEtatsCheveuxCibles().isEmpty()) {
                score += 15;
            }
        } else {
            score += 15;
        }

        // 3. Porosité (20 points)
        if (profil.getPorosite() != null) {
            if (p.getPorositesCibles().contains(profil.getPorosite())) {
                score += 20;
            } else if (p.getPorositesCibles().isEmpty()) {
                score += 10;
            }
        } else {
            score += 10;
        }

        // 4. Type de cheveux (10 points)
        if (profil.getTypeCheveux() != null) {
            if (p.getTypesCheveuxCibles().contains(profil.getTypeCheveux())) {
                score += 10;
            } else if (p.getTypesCheveuxCibles().isEmpty()) {
                score += 5;
            }
        } else {
            score += 5;
        }

        // 5. Texture (10 points)
        if (profil.getTexture() != null) {
            if (p.getTexturesCibles().contains(profil.getTexture())) {
                score += 10;
            } else if (p.getTexturesCibles().isEmpty()) {
                score += 5;
            }
        } else {
            score += 5;
        }

        return Math.min(100, Math.max(0, score));
    }

    private ScoredProduct selectBestProduct(List<ProduitConseilCapillaire> tous, EtapeRoutine etape, ProfilCapillaire profil) {
        List<ProduitConseilCapillaire> candidatsEtape = tous.stream()
                .filter(p -> p.getEtapeRoutine() == etape)
                .toList();

        if (candidatsEtape.isEmpty()) {
            return new ScoredProduct(null, 50);
        }

        ProduitConseilCapillaire best = candidatsEtape.stream()
                .max(Comparator.comparingInt(p -> calculerScoreAffinite(p, profil)))
                .orElse(candidatsEtape.get(0));

        int score = calculerScoreAffinite(best, profil);
        return new ScoredProduct(best, score);
    }


    private record ScoredProduct(ProduitConseilCapillaire produit, int score) {}
}
