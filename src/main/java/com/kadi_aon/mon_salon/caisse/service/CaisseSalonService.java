package com.kadi_aon.mon_salon.caisse.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.caisse.dto.OperationCaisseDTOResponse;
import com.kadi_aon.mon_salon.caisse.dto.SessionCaisseClotureDTORequest;
import com.kadi_aon.mon_salon.caisse.dto.SessionCaisseDTOResponse;
import com.kadi_aon.mon_salon.caisse.dto.SessionCaisseOuvertureDTORequest;
import com.kadi_aon.mon_salon.caisse.entity.OperationCaisse;
import com.kadi_aon.mon_salon.caisse.entity.SessionCaisse;
import com.kadi_aon.mon_salon.caisse.enums.StatutSessionCaisse;
import com.kadi_aon.mon_salon.caisse.enums.TypeOperationCaisse;
import com.kadi_aon.mon_salon.caisse.exception.SessionCaisseFermeeException;
import com.kadi_aon.mon_salon.caisse.repository.OperationCaisseRepository;
import com.kadi_aon.mon_salon.caisse.repository.SessionCaisseRepository;
import com.kadi_aon.mon_salon.facturation.entity.Facture;
import com.kadi_aon.mon_salon.facturation.entity.Paiement;
import com.kadi_aon.mon_salon.facturation.enums.MoyenPaiement;
import com.kadi_aon.mon_salon.facturation.enums.StatutFacture;
import com.kadi_aon.mon_salon.facturation.enums.StatutPaiement;
import com.kadi_aon.mon_salon.facturation.enums.TypePaiement;
import com.kadi_aon.mon_salon.facturation.repository.FactureRepository;
import com.kadi_aon.mon_salon.facturation.repository.PaiementRepository;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;
import com.kadi_aon.mon_salon.stock.entity.Commande;
import com.kadi_aon.mon_salon.stock.enums.StatutCommande;
import com.kadi_aon.mon_salon.stock.repository.CommandeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CaisseSalonService {

    private final SessionCaisseRepository sessionCaisseRepository;
    private final OperationCaisseRepository operationCaisseRepository;
    private final AffectationSalonRepository affectationSalonRepository;
    private final SalonRepository salonRepository;
    private final AuditLogService auditLogService;
    private final CommandeRepository commandeRepository;
    private final PaiementRepository paiementRepository;
    private final FactureRepository factureRepository;

    public CaisseSalonService(
            SessionCaisseRepository sessionCaisseRepository,
            OperationCaisseRepository operationCaisseRepository,
            AffectationSalonRepository affectationSalonRepository,
            SalonRepository salonRepository,
            AuditLogService auditLogService,
            CommandeRepository commandeRepository,
            PaiementRepository paiementRepository,
            FactureRepository factureRepository) {
        super();
        this.sessionCaisseRepository = sessionCaisseRepository;
        this.operationCaisseRepository = operationCaisseRepository;
        this.affectationSalonRepository = affectationSalonRepository;
        this.salonRepository = salonRepository;
        this.auditLogService = auditLogService;
        this.commandeRepository = commandeRepository;
        this.paiementRepository = paiementRepository;
        this.factureRepository = factureRepository;
    }

    @Transactional
    public SessionCaisseDTOResponse ouvrirSessionCaisse(String slugSalon, String employeEmail, SessionCaisseOuvertureDTORequest request) {
        AffectationSalon affectation = validerComptableOuReceptionniste(slugSalon, employeEmail);

        if (sessionCaisseRepository.existsByAffectationSalonSlugAndStatut(slugSalon, StatutSessionCaisse.EN_COURS)) {
            throw new IllegalStateException("Une session de caisse est déjà en cours pour le salon " + slugSalon);
        }

        BigDecimal soldeOuv = request.soldeOuverture() != null ? request.soldeOuverture() : BigDecimal.ZERO;
        SessionCaisse session = SessionCaisse.builder()
                .affectation(affectation)
                .salon(affectation.getSalon())
                .dateOuverture(LocalDateTime.now())
                .soldeOuverture(soldeOuv)
                .soldeTheorique(soldeOuv)
                .statut(StatutSessionCaisse.EN_COURS)
                .build();

        SessionCaisse saved = sessionCaisseRepository.save(session);

        String roleName = extraireRole(affectation);
        auditLogService.logActionSalon(
                TypeActionAudit.CREATION,
                "SessionCaisse",
                saved.getId().toString(),
                null,
                "Solde ouverture: " + request.soldeOuverture(),
                affectation,
                roleName
        );

        regulariserCommandesEnAttente(saved, slugSalon, affectation);

        log.info("Session de caisse {} ouverte pour le salon {} par {}", saved.getId(), slugSalon, employeEmail);
        return mapToResponse(saved);
    }

    @Transactional
    public SessionCaisseDTOResponse cloturerSessionCaisse(String slugSalon, String employeEmail, SessionCaisseClotureDTORequest request) {
        AffectationSalon affectation = validerComptableOuReceptionniste(slugSalon, employeEmail);

        SessionCaisse session = sessionCaisseRepository.findByAffectationSalonSlugAndStatut(slugSalon, StatutSessionCaisse.EN_COURS)
                .orElseThrow(() -> new IllegalStateException("Aucune session de caisse active trouvée à clôturer pour le salon " + slugSalon));

        BigDecimal soldeTheorique = calculerSoldeTheorique(session);
        BigDecimal soldeFermeture = (request != null && request.soldeFermeture() != null)
                ? request.soldeFermeture()
                : soldeTheorique;

        session.setDateCloture(LocalDateTime.now());
        session.setSoldeTheorique(soldeTheorique);
        session.setSoldeFermeture(soldeFermeture);
        session.setStatut(StatutSessionCaisse.CLOTURE);

        SessionCaisse saved = sessionCaisseRepository.save(session);

        String roleName = extraireRole(affectation);
        auditLogService.logActionSalon(
                TypeActionAudit.MODIFICATION,
                "SessionCaisse",
                saved.getId().toString(),
                "EN_COURS",
                "Solde cloture: " + soldeFermeture,
                affectation,
                roleName
        );

        log.info("Session de caisse {} clôturée pour le salon {} par {}", saved.getId(), slugSalon, employeEmail);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public SessionCaisseDTOResponse obtenirSessionCourante(String slugSalon) {
        SessionCaisse session = sessionCaisseRepository.findByAffectationSalonSlugAndStatut(slugSalon, StatutSessionCaisse.EN_COURS)
                .orElseThrow(() -> new SessionCaisseFermeeException("Aucune session de caisse n'est actuellement ouverte pour le salon " + slugSalon));

        return mapToResponse(session);
    }

    @Transactional(readOnly = true)
    public List<SessionCaisseDTOResponse> listerHistoriqueSessions(String slugSalon) {
        if (!salonRepository.existsBySlug(slugSalon)) {
            throw new EntityNotFoundException("Salon introuvable pour le slug : " + slugSalon);
        }
        return sessionCaisseRepository.findByAffectationSalonSlugOrderByDateOuvertureDesc(slugSalon).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OperationCaisseDTOResponse> listerOperationsSession(String slugSalon, Long sessionId) {
        if (!salonRepository.existsBySlug(slugSalon)) {
            throw new EntityNotFoundException("Salon introuvable pour le slug : " + slugSalon);
        }
        SessionCaisse session = sessionCaisseRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session de caisse introuvable ID : " + sessionId));

        if (!session.getAffectation().getSalon().getSlug().equals(slugSalon)) {
            throw new IllegalArgumentException("La session de caisse #" + sessionId + " n'appartient pas au salon " + slugSalon);
        }

        return operationCaisseRepository.findBySessionCaisseIdOrderByDateOperationDesc(sessionId).stream()
                .map(o -> new OperationCaisseDTOResponse(
                        o.getId(),
                        o.getMontant(),
                        o.getDateOperation(),
                        o.getLibelle(),
                        o.getType().name(),
                        o.getStatut(),
                        (o.getPaiement() != null) ? o.getPaiement().getNumeroPaiement() : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public SessionCaisse obtenirSessionActive(String slugSalon) {
        return sessionCaisseRepository.findByAffectationSalonSlugAndStatut(slugSalon, StatutSessionCaisse.EN_COURS)
                .orElseThrow(() -> new SessionCaisseFermeeException("Impossible d'effectuer l'opération financière : aucune session de caisse n'est ouverte pour le salon " + slugSalon));
    }

    @Transactional(readOnly = true)
    public Optional<SessionCaisse> trouverSessionActive(String slugSalon) {
        return sessionCaisseRepository.findByAffectationSalonSlugAndStatut(slugSalon, StatutSessionCaisse.EN_COURS);
    }


    @Transactional
    public OperationCaisse enregistrerOperationEntree(SessionCaisse session, BigDecimal montant, String libelle, Paiement paiement) {
        OperationCaisse op = OperationCaisse.builder()
                .sessionCaisse(session)
                .montant(montant)
                .dateOperation(LocalDateTime.now())
                .libelle(libelle)
                .type(TypeOperationCaisse.ENTREE)
                .statut(true)
                .paiement(paiement)
                .build();
        OperationCaisse savedOp = operationCaisseRepository.save(op);

        BigDecimal ancienTheorique = session.getSoldeTheorique() != null
                ? session.getSoldeTheorique()
                : (session.getSoldeOuverture() != null ? session.getSoldeOuverture() : BigDecimal.ZERO);
        session.setSoldeTheorique(ancienTheorique.add(montant));
        sessionCaisseRepository.save(session);

        return savedOp;
    }

    @Transactional
    public OperationCaisse enregistrerOperationSortie(SessionCaisse session, BigDecimal montant, String libelle, Paiement paiement) {
        OperationCaisse op = OperationCaisse.builder()
                .sessionCaisse(session)
                .montant(montant)
                .dateOperation(LocalDateTime.now())
                .libelle(libelle)
                .type(TypeOperationCaisse.SORTIE)
                .statut(true)
                .paiement(paiement)
                .build();
        OperationCaisse savedOp = operationCaisseRepository.save(op);

        BigDecimal ancienTheorique = session.getSoldeTheorique() != null
                ? session.getSoldeTheorique()
                : (session.getSoldeOuverture() != null ? session.getSoldeOuverture() : BigDecimal.ZERO);
        session.setSoldeTheorique(ancienTheorique.subtract(montant));
        sessionCaisseRepository.save(session);

        return savedOp;
    }

    public BigDecimal calculerSoldeTheorique(SessionCaisse s) {
        List<OperationCaisse> ops = s.getOperations() != null ? s.getOperations() : List.of();
        BigDecimal totalEntrees = ops.stream()
                .filter(o -> o.getType() == TypeOperationCaisse.ENTREE && Boolean.TRUE.equals(o.getStatut()))
                .map(OperationCaisse::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSorties = ops.stream()
                .filter(o -> o.getType() == TypeOperationCaisse.SORTIE && Boolean.TRUE.equals(o.getStatut()))
                .map(OperationCaisse::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ouverture = s.getSoldeOuverture() != null ? s.getSoldeOuverture() : BigDecimal.ZERO;
        return ouverture.add(totalEntrees).subtract(totalSorties);
    }

    @Transactional
    public int regulariserCommandesEnAttente(SessionCaisse session, String slugSalon, AffectationSalon responsable) {
        List<Commande> enAttente = commandeRepository.findCommandesEnAttenteEncaissement(slugSalon, StatutCommande.RECUPEREE);
        if (enAttente == null || enAttente.isEmpty()) {
            return 0;
        }
        log.info("Régularisation de {} commande(s) en attente d'encaissement pour la session de caisse #{} ({})",
                enAttente.size(), session.getId(), slugSalon);
        for (Commande cmd : enAttente) {
            MoyenPaiement moyen = (cmd.getMoyenPaiement() != null) ? cmd.getMoyenPaiement() : MoyenPaiement.ESPECES;
            enregistrerEncaissementCommande(cmd, session, responsable, moyen, true);
        }
        return enAttente.size();
    }

    @Transactional
    public Paiement enregistrerEncaissementCommande(Commande commande, SessionCaisse session, AffectationSalon encaisseur, MoyenPaiement moyenPaiement, boolean isRegularisation) {
        if (Boolean.TRUE.equals(commande.getEncaisseeEnCaisse())) {
            log.warn("La commande {} a déjà été encaissée en caisse.", commande.getNumeroCommande());
            return null;
        }

        Facture facture = commande.getFacture();
        if (facture == null) {
            log.warn("Aucune facture associée à la commande {}. Impossible d'enregistrer le paiement en caisse.", commande.getNumeroCommande());
            return null;
        }

        MoyenPaiement moyen = (moyenPaiement != null) ? moyenPaiement :
                (commande.getMoyenPaiement() != null ? commande.getMoyenPaiement() : MoyenPaiement.ESPECES);

        String slugSalon = session.getSalon() != null ? session.getSalon().getSlug() :
                (session.getAffectation() != null && session.getAffectation().getSalon() != null ? session.getAffectation().getSalon().getSlug() : "SALON");

        String month = DateTimeFormatter.ofPattern("yyyyMM").format(LocalDateTime.now());
        String code = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String numeroPaiement = "PAY-" + slugSalon.toUpperCase() + "-" + month + "-" + code;

        Paiement paiement = Paiement.builder()
                .numeroPaiement(numeroPaiement)
                .montant(commande.getMontantTotal())
                .datePaiement(LocalDateTime.now())
                .type(TypePaiement.SOLDE)
                .statut(StatutPaiement.PAYE)
                .moyenPaiement(moyen)
                .reference("Commande " + commande.getNumeroCommande())
                .encaissePar(encaisseur)
                .facture(facture)
                .client(commande.getClientAffectation() != null ? commande.getClientAffectation().getCompte() : null)
                .salon(session.getSalon())
                .build();

        String libelle = (isRegularisation ? "Régularisation encaissement " : "Encaissement ") +
                "commande " + commande.getNumeroCommande() + " [" + numeroPaiement + "] (" + moyen.name() + ")";

        OperationCaisse opEntree = enregistrerOperationEntree(session, commande.getMontantTotal(), libelle, paiement);
        paiement.setOperationCaisse(opEntree);

        Paiement savedPaiement = paiementRepository.save(paiement);

        // Mettre à jour la facture (soldée)
        facture.getPaiements().add(savedPaiement);
        facture.setMontantPaye(facture.getMontantTotal());
        facture.setResteAPayer(BigDecimal.ZERO);
        facture.setStatut(StatutFacture.PAYEE);
        factureRepository.save(facture);

        // Mettre à jour la commande
        commande.setEncaisseeEnCaisse(true);
        commande.setMoyenPaiement(moyen);
        commande.setDateEncaissementCaisse(LocalDateTime.now());
        commandeRepository.save(commande);

        String roleName = extraireRole(encaisseur);
        auditLogService.logActionSalon(
                TypeActionAudit.CREATION,
                "Paiement",
                savedPaiement.getNumeroPaiement(),
                null,
                (isRegularisation ? "Régularisation encaissement " : "Encaissement ") +
                        "commande " + commande.getNumeroCommande() + " (" + commande.getMontantTotal() + " FCFA via " + moyen.name() + ")",
                encaisseur,
                roleName
        );

        log.info("Encaissement enregistré pour la commande {} dans la session de caisse #{} (Paiement {})",
                commande.getNumeroCommande(), session.getId(), savedPaiement.getNumeroPaiement());

        return savedPaiement;
    }

    private AffectationSalon validerComptableOuReceptionniste(String slugSalon, String email) {
        AffectationSalon affectation = affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue(email, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Affectation active introuvable pour l'utilisateur " + email + " sur le salon " + slugSalon));

        boolean hasRole = affectation.getRoles().stream()
                .anyMatch(r -> r.getRole() == TypeRoleSalon.COMPTABLE || r.getRole() == TypeRoleSalon.RECEPTIONNISTE);

        if (!hasRole) {
            throw new IllegalArgumentException("L'utilisateur " + email + " ne possède ni le rôle COMPTABLE ni le rôle RECEPTIONNISTE sur le salon " + slugSalon);
        }
        return affectation;
    }

    private String extraireRole(AffectationSalon affectation) {
        if (affectation == null || affectation.getRoles() == null) {
            return "EMPLOYE";
        }
        return affectation.getRoles().stream()
                .map(r -> r.getRole().name())
                .filter(r -> r.equals("COMPTABLE") || r.equals("RECEPTIONNISTE") || r.equals("RESPONSABLE_STOCK") || r.equals("GERANT"))
                .findFirst()
                .orElse("EMPLOYE");
    }

    private SessionCaisseDTOResponse mapToResponse(SessionCaisse s) {
        List<OperationCaisse> ops = s.getOperations() != null ? s.getOperations() : List.of();
        BigDecimal totalEntrees = ops.stream()
                .filter(o -> o.getType() == TypeOperationCaisse.ENTREE && Boolean.TRUE.equals(o.getStatut()))
                .map(OperationCaisse::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSorties = ops.stream()
                .filter(o -> o.getType() == TypeOperationCaisse.SORTIE && Boolean.TRUE.equals(o.getStatut()))
                .map(OperationCaisse::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ouverture = s.getSoldeOuverture() != null ? s.getSoldeOuverture() : BigDecimal.ZERO;
        BigDecimal soldeTheorique = s.getSoldeTheorique() != null ? s.getSoldeTheorique() : ouverture.add(totalEntrees).subtract(totalSorties);

        List<OperationCaisseDTOResponse> opsDTO = ops.stream()
                .map(o -> new OperationCaisseDTOResponse(
                        o.getId(),
                        o.getMontant(),
                        o.getDateOperation(),
                        o.getLibelle(),
                        o.getType().name(),
                        o.getStatut(),
                        (o.getPaiement() != null) ? o.getPaiement().getNumeroPaiement() : null
                ))
                .toList();

        return new SessionCaisseDTOResponse(
                s.getId(),
                s.getAffectation().getId(),
                s.getAffectation().getCompte().getNom(),
                s.getAffectation().getCompte().getPrenom(),
                s.getAffectation().getCompte().getEmail(),
                s.getDateOuverture(),
                s.getDateCloture(),
                s.getSoldeOuverture(),
                s.getSoldeFermeture(),
                totalEntrees,
                totalSorties,
                soldeTheorique,
                s.getStatut().name(),
                opsDTO
        );
    }
}
