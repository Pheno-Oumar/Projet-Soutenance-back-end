package com.kadi_aon.mon_salon.reclamation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.facturation.repository.PaiementRepository;
import com.kadi_aon.mon_salon.notification.service.NotificationEmailService;
import com.kadi_aon.mon_salon.reclamation.dto.ReclamationCreateDTORequest;
import com.kadi_aon.mon_salon.reclamation.dto.ReclamationDTOResponse;
import com.kadi_aon.mon_salon.reclamation.dto.ReclamationTraiterDTORequest;
import com.kadi_aon.mon_salon.reclamation.dto.TypeReclamationEligibiliteDTO;
import com.kadi_aon.mon_salon.reclamation.entity.Reclamation;
import com.kadi_aon.mon_salon.reclamation.enums.StatutReclamation;
import com.kadi_aon.mon_salon.reclamation.enums.TypeReclamation;
import com.kadi_aon.mon_salon.reclamation.repository.ReclamationRepository;
import com.kadi_aon.mon_salon.rendezvous.enums.StatutRendezVous;
import com.kadi_aon.mon_salon.rendezvous.repository.RendezVousRepository;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;
import com.kadi_aon.mon_salon.stock.enums.StatutCommande;
import com.kadi_aon.mon_salon.stock.repository.CommandeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReclamationSalonService {

    private final ReclamationRepository reclamationRepository;
    private final AffectationSalonRepository affectationSalonRepository;
    private final SalonRepository salonRepository;
    private final NotificationEmailService notificationEmailService;
    private final AuditLogService auditLogService;
    private final CommandeRepository commandeRepository;
    private final RendezVousRepository rendezVousRepository;
    private final PaiementRepository paiementRepository;

    public ReclamationSalonService(
            ReclamationRepository reclamationRepository,
            AffectationSalonRepository affectationSalonRepository,
            SalonRepository salonRepository,
            NotificationEmailService notificationEmailService,
            AuditLogService auditLogService,
            CommandeRepository commandeRepository,
            RendezVousRepository rendezVousRepository,
            PaiementRepository paiementRepository) {
        super();
        this.reclamationRepository = reclamationRepository;
        this.affectationSalonRepository = affectationSalonRepository;
        this.salonRepository = salonRepository;
        this.notificationEmailService = notificationEmailService;
        this.auditLogService = auditLogService;
        this.commandeRepository = commandeRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.paiementRepository = paiementRepository;
    }

    @Transactional
    public ReclamationDTOResponse deposerReclamation(String slugSalon, String clientEmail, ReclamationCreateDTORequest request) {
        AffectationSalon affectation = affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue(clientEmail, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Affectation client active introuvable pour " + clientEmail + " sur le salon " + slugSalon));

        validerEligibilite(slugSalon, clientEmail, request.typeSafe());

        Reclamation reclamation = Reclamation.builder()
                .affectationClient(affectation)
                .salon(affectation.getSalon())
                .objet(request.objet())
                .description(request.description())
                .type(request.typeSafe())
                .statut(StatutReclamation.EN_ATTENTE)
                .dateCreation(LocalDateTime.now())
                .build();

        Reclamation saved = reclamationRepository.save(reclamation);

        auditLogService.logActionSalon(
                TypeActionAudit.CREATION,
                "Reclamation",
                String.valueOf(saved.getId()),
                null,
                "Dépôt réclamation: " + saved.getObjet(),
                affectation,
                TypeRoleSalon.CLIENT.name()
        );

        log.info("Réclamation {} déposée par le client {} sur le salon {}", saved.getId(), clientEmail, slugSalon);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReclamationDTOResponse> listerReclamationsClient(String slugSalon, String clientEmail) {
        if (!salonRepository.existsBySlug(slugSalon)) {
            throw new EntityNotFoundException("Salon introuvable pour le slug : " + slugSalon);
        }
        return reclamationRepository.findByAffectationClientCompteEmailAndSalonSlugOrderByDateCreationDesc(clientEmail, slugSalon).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReclamationDTOResponse obtenirReclamationClient(String slugSalon, Long reclamationId, String clientEmail) {
        Reclamation reclamation = reclamationRepository.findByIdAndAffectationClientCompteEmailAndSalonSlug(reclamationId, clientEmail, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation introuvable avec l'ID " + reclamationId + " pour ce client sur ce salon"));
        return mapToResponse(reclamation);
    }

    @Transactional(readOnly = true)
    public List<ReclamationDTOResponse> listerToutesReclamationsClient(String clientEmail) {
        return listerReclamationsClientFiltrees(clientEmail, null, null);
    }

    @Transactional(readOnly = true)
    public List<ReclamationDTOResponse> listerReclamationsClientFiltrees(String clientIdentifiant, String slugSalonOpt, StatutReclamation statutOpt) {
        String slug = (slugSalonOpt != null && !slugSalonOpt.isBlank()) ? slugSalonOpt.trim() : null;
        return reclamationRepository.findByClientFiltre(clientIdentifiant, slug, statutOpt).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReclamationDTOResponse obtenirReclamationClientGlobal(Long reclamationId, String clientEmail) {
        Reclamation reclamation = reclamationRepository.findByIdAndAffectationClientCompteEmail(reclamationId, clientEmail)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation introuvable avec l'ID " + reclamationId));
        return mapToResponse(reclamation);
    }

    @Transactional(readOnly = true)
    public List<TypeReclamationEligibiliteDTO> obtenirTypesEligiblesClient(String slugSalon, String clientEmail) {
        if (!salonRepository.existsBySlug(slugSalon)) {
            throw new EntityNotFoundException("Salon introuvable pour le slug : " + slugSalon);
        }

        // 1. Achat produit validé ou récupéré
        boolean hasAcheteProduit = commandeRepository.existsByClientEmailAndSalonSlugAndStatutIn(
                clientEmail, slugSalon, List.of(StatutCommande.RECUPEREE, StatutCommande.VALIDEE)
        );

        // 2. Prestation terminée
        boolean hasPrestationTerminee = rendezVousRepository.existsByClientEmailAndSalonSlugAndStatut(
                clientEmail, slugSalon, StatutRendezVous.TERMINE
        );

        // 3. Paiement effectué
        boolean hasPaiement = !paiementRepository.findByClientEmailAndSalonSlugOrderByDatePaiementDesc(clientEmail, slugSalon).isEmpty()
                || commandeRepository.findCommandesClientFiltre(null, clientEmail, slugSalon, null).stream()
                        .anyMatch(c -> Boolean.TRUE.equals(c.getEncaisseeEnCaisse()) || c.getStatut() == StatutCommande.RECUPEREE);

        // 4. Au moins un rendez-vous (quel que soit le statut)
        boolean hasRendezVous = rendezVousRepository.existsByClientEmailAndSalonSlug(clientEmail, slugSalon);

        // 5. Au moins une interaction avec le salon (au moins 1 commande ou 1 rendez-vous)
        boolean hasInteraction = hasRendezVous || commandeRepository.existsByClientEmailAndSalonSlug(clientEmail, slugSalon);

        List<TypeReclamationEligibiliteDTO> liste = new ArrayList<>();

        liste.add(new TypeReclamationEligibiliteDTO(
                TypeReclamation.SERVICE,
                "Service",
                hasPrestationTerminee,
                hasPrestationTerminee ? null : "Vous devez avoir au moins une prestation terminée dans ce salon pour déposer une réclamation de service."
        ));

        liste.add(new TypeReclamationEligibiliteDTO(
                TypeReclamation.PRODUIT,
                "Produit",
                hasAcheteProduit,
                hasAcheteProduit ? null : "Vous devez avoir acheté un produit (commande validée ou récupérée) dans ce salon pour déposer une réclamation produit."
        ));

        liste.add(new TypeReclamationEligibiliteDTO(
                TypeReclamation.SALON,
                "Salon",
                hasInteraction,
                hasInteraction ? null : "Vous devez avoir au moins une interaction (rendez-vous ou commande) avec ce salon pour déposer une réclamation."
        ));

        liste.add(new TypeReclamationEligibiliteDTO(
                TypeReclamation.PAIEMENT,
                "Paiement",
                hasPaiement,
                hasPaiement ? null : "Vous devez avoir effectué au moins un paiement dans ce salon pour déposer une réclamation de paiement."
        ));

        liste.add(new TypeReclamationEligibiliteDTO(
                TypeReclamation.RENDEZ_VOUS,
                "Rendez-vous",
                hasRendezVous,
                hasRendezVous ? null : "Vous devez avoir au moins un rendez-vous dans ce salon pour déposer une réclamation sur un rendez-vous."
        ));

        liste.add(new TypeReclamationEligibiliteDTO(
                TypeReclamation.AUTRE,
                "Autre",
                hasInteraction,
                hasInteraction ? null : "Vous devez avoir au moins une interaction avec ce salon pour déposer une réclamation."
        ));

        return liste;
    }

    public void validerEligibilite(String slugSalon, String clientEmail, TypeReclamation type) {
        if (type == null) {
            return;
        }
        switch (type) {
            case PRODUIT -> {
                boolean hasAcheteProduit = commandeRepository.existsByClientEmailAndSalonSlugAndStatutIn(
                        clientEmail, slugSalon, List.of(StatutCommande.RECUPEREE, StatutCommande.VALIDEE)
                );
                if (!hasAcheteProduit) {
                    throw new IllegalArgumentException("Vous ne pouvez déposer une réclamation produit que si vous avez déjà acheté un produit (commande validée ou récupérée) dans ce salon.");
                }
            }
            case SERVICE -> {
                boolean hasPrestationTerminee = rendezVousRepository.existsByClientEmailAndSalonSlugAndStatut(
                        clientEmail, slugSalon, StatutRendezVous.TERMINE
                );
                if (!hasPrestationTerminee) {
                    throw new IllegalArgumentException("Vous ne pouvez déposer une réclamation de type service que si vous avez déjà une prestation terminée dans ce salon.");
                }
            }
            case PAIEMENT -> {
                boolean hasPaiement = !paiementRepository.findByClientEmailAndSalonSlugOrderByDatePaiementDesc(clientEmail, slugSalon).isEmpty()
                        || commandeRepository.findCommandesClientFiltre(null, clientEmail, slugSalon, null).stream()
                                .anyMatch(c -> Boolean.TRUE.equals(c.getEncaisseeEnCaisse()) || c.getStatut() == StatutCommande.RECUPEREE);
                if (!hasPaiement) {
                    throw new IllegalArgumentException("Vous ne pouvez déposer une réclamation de paiement que si vous avez déjà effectué un paiement dans ce salon.");
                }
            }
            case RENDEZ_VOUS -> {
                boolean hasRendezVous = rendezVousRepository.existsByClientEmailAndSalonSlug(clientEmail, slugSalon);
                if (!hasRendezVous) {
                    throw new IllegalArgumentException("Vous ne pouvez déposer une réclamation de rendez-vous que si vous avez déjà un rendez-vous dans ce salon.");
                }
            }
            case SALON, AUTRE -> {
                boolean hasInteraction = rendezVousRepository.existsByClientEmailAndSalonSlug(clientEmail, slugSalon)
                        || commandeRepository.existsByClientEmailAndSalonSlug(clientEmail, slugSalon);
                if (!hasInteraction) {
                    throw new IllegalArgumentException("Vous devez avoir au moins une interaction (rendez-vous ou commande) avec ce salon pour déposer une réclamation.");
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ReclamationDTOResponse> listerReclamationsSalon(String slugSalon, StatutReclamation statut) {
        if (!salonRepository.existsBySlug(slugSalon)) {
            throw new EntityNotFoundException("Salon introuvable pour le slug : " + slugSalon);
        }

        List<Reclamation> reclamations = (statut != null)
                ? reclamationRepository.findBySalonSlugAndStatutOrderByDateCreationDesc(slugSalon, statut)
                : reclamationRepository.findBySalonSlugOrderByDateCreationDesc(slugSalon);

        return reclamations.stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReclamationDTOResponse obtenirReclamationSalon(String slugSalon, Long reclamationId) {
        Reclamation reclamation = reclamationRepository.findByIdAndSalonSlug(reclamationId, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation introuvable avec l'ID " + reclamationId + " pour le salon " + slugSalon));
        return mapToResponse(reclamation);
    }

    @Transactional
    public ReclamationDTOResponse traiterReclamation(String slugSalon, Long reclamationId, String staffEmail, ReclamationTraiterDTORequest request) {
        AffectationSalon staffAffectation = validerManagerOuProprietaire(slugSalon, staffEmail);

        Reclamation reclamation = reclamationRepository.findByIdAndSalonSlug(reclamationId, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation introuvable avec l'ID " + reclamationId + " pour le salon " + slugSalon));

        if (reclamation.getStatut() == StatutReclamation.RESOLUE || reclamation.getStatut() == StatutReclamation.REJETEE) {
            throw new IllegalStateException("Cette réclamation a déjà été clôturée (statut: " + reclamation.getStatut() + ").");
        }

        String ancienStatut = reclamation.getStatut().name();
        reclamation.setStatut(request.nouveauStatut());
        reclamation.setReponseTraitement(request.reponse());
        reclamation.setDateTraitement(LocalDateTime.now());
        reclamation.setTraitePar(staffAffectation);

        Reclamation updated = reclamationRepository.save(reclamation);

        // Notification envoyée au client
        String clientEmail = reclamation.getAffectationClient().getCompte().getEmail();
        String clientPrenom = reclamation.getAffectationClient().getCompte().getPrenom();
        String sujet = "Mise à jour de votre réclamation - " + reclamation.getSalon().getNom();
        String contenu = String.format(
                "Bonjour %s,\n\n" +
                "Votre réclamation concernant : \"%s\" a été traitée par la direction du salon.\n\n" +
                "Statut : %s\n" +
                "Réponse : %s\n\n" +
                "Cordialement,\n" +
                "L'équipe %s.",
                clientPrenom,
                reclamation.getObjet(),
                request.nouveauStatut().name(),
                request.reponse(),
                reclamation.getSalon().getNom()
        );

        notificationEmailService.queueNotificationEmail(clientEmail, sujet, contenu);

        auditLogService.logActionSalon(
                TypeActionAudit.MODIFICATION,
                "Reclamation",
                String.valueOf(updated.getId()),
                ancienStatut,
                "Traitement réclamation passé à " + request.nouveauStatut().name(),
                staffAffectation,
                staffAffectation.getRoles().stream().findFirst().map(r -> r.getRole().name()).orElse("STAFF")
        );

        log.info("Réclamation {} traitée par {} avec statut {}", updated.getId(), staffEmail, request.nouveauStatut());
        return mapToResponse(updated);
    }

    private AffectationSalon validerManagerOuProprietaire(String slugSalon, String email) {
        AffectationSalon affectation = affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue(email, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Affectation active introuvable pour l'utilisateur " + email + " sur le salon " + slugSalon));

        boolean hasRole = affectation.getRoles().stream()
                .anyMatch(r -> r.getRole() == TypeRoleSalon.MANAGER || r.getRole() == TypeRoleSalon.PROPRIETAIRE);

        if (!hasRole) {
            throw new IllegalArgumentException("L'utilisateur " + email + " n'a pas les droits pour traiter les réclamations sur ce salon (MANAGER ou PROPRIETAIRE requis).");
        }
        return affectation;
    }

    private ReclamationDTOResponse mapToResponse(Reclamation r) {
        String clientNom = r.getAffectationClient() != null && r.getAffectationClient().getCompte() != null
                ? r.getAffectationClient().getCompte().getPrenom() + " " + r.getAffectationClient().getCompte().getNom()
                : "N/A";
        String clientEmail = r.getAffectationClient() != null && r.getAffectationClient().getCompte() != null
                ? r.getAffectationClient().getCompte().getEmail()
                : "N/A";

        String traiteParNom = r.getTraitePar() != null && r.getTraitePar().getCompte() != null
                ? r.getTraitePar().getCompte().getPrenom() + " " + r.getTraitePar().getCompte().getNom()
                : null;

        return new ReclamationDTOResponse(
                r.getId(),
                r.getObjet(),
                r.getDescription(),
                r.getType(),
                r.getStatut().name(),
                r.getReponseTraitement(),
                r.getDateCreation(),
                r.getDateTraitement(),
                clientNom,
                clientEmail,
                traiteParNom,
                (r.getSalon() != null) ? r.getSalon().getSlug() : null,
                (r.getSalon() != null) ? r.getSalon().getNom() : null
        );
    }
}
