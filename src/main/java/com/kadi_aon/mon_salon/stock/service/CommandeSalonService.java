package com.kadi_aon.mon_salon.stock.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.caisse.entity.SessionCaisse;
import com.kadi_aon.mon_salon.caisse.service.CaisseSalonService;
import com.kadi_aon.mon_salon.common.exception.ForbiddenException;
import com.kadi_aon.mon_salon.facturation.entity.Facture;
import com.kadi_aon.mon_salon.facturation.enums.MoyenPaiement;
import com.kadi_aon.mon_salon.facturation.repository.FactureRepository;
import com.kadi_aon.mon_salon.notification.enums.TypeNotification;
import com.kadi_aon.mon_salon.notification.service.NotificationAppService;
import com.kadi_aon.mon_salon.notification.service.NotificationEmailService;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.RoleSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.RoleSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;
import com.kadi_aon.mon_salon.stock.dto.CommandeDTOResponse;
import com.kadi_aon.mon_salon.stock.dto.KpiStockDTOResponse;
import com.kadi_aon.mon_salon.stock.dto.LigneCommandeDTOResponse;
import com.kadi_aon.mon_salon.stock.dto.ProduitAlerteStockDTOResponse;
import com.kadi_aon.mon_salon.stock.dto.ProduitRentabiliteDTOResponse;
import com.kadi_aon.mon_salon.stock.dto.RejetCommandeDTORequest;
import com.kadi_aon.mon_salon.stock.dto.RetraitCommandeDTORequest;
import com.kadi_aon.mon_salon.stock.dto.VitrineCommandeDTORequest;
import com.kadi_aon.mon_salon.stock.entity.Commande;
import com.kadi_aon.mon_salon.stock.entity.LigneCommande;
import com.kadi_aon.mon_salon.stock.entity.LignePanier;
import com.kadi_aon.mon_salon.stock.entity.MouvementStock;
import com.kadi_aon.mon_salon.stock.entity.Panier;
import com.kadi_aon.mon_salon.stock.entity.Produit;
import com.kadi_aon.mon_salon.stock.entity.StockProduit;
import com.kadi_aon.mon_salon.stock.enums.StatutCommande;
import com.kadi_aon.mon_salon.stock.enums.TypeMouvementStock;
import com.kadi_aon.mon_salon.stock.exception.StockInsuffisantException;
import com.kadi_aon.mon_salon.stock.repository.CommandeRepository;
import com.kadi_aon.mon_salon.stock.repository.LigneCommandeRepository;
import com.kadi_aon.mon_salon.stock.repository.MouvementStockRepository;
import com.kadi_aon.mon_salon.stock.repository.ProduitRepository;
import com.kadi_aon.mon_salon.stock.repository.StockProduitRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommandeSalonService {

    private final CommandeRepository commandeRepository;
    private final PanierService panierService;
    private final ProduitRepository produitRepository;
    private final StockProduitRepository stockProduitRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final FactureRepository factureRepository;
    private final AffectationSalonRepository affectationSalonRepository;
    private final NotificationEmailService notificationEmailService;
    private final NotificationAppService notificationAppService;
    private final CaisseSalonService caisseSalonService;
    private final AuditLogService auditLogService;
    private final SalonRepository salonRepository;
    private final CompteRepository compteRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final RoleSalonRepository roleSalonRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.kadi_aon.mon_salon.notification.service.NotificationSmsService notificationSmsService;

    public CommandeSalonService(
            CommandeRepository commandeRepository,
            PanierService panierService,
            ProduitRepository produitRepository,
            StockProduitRepository stockProduitRepository,
            MouvementStockRepository mouvementStockRepository,
            LigneCommandeRepository ligneCommandeRepository,
            FactureRepository factureRepository,
            AffectationSalonRepository affectationSalonRepository,
            NotificationEmailService notificationEmailService,
            NotificationAppService notificationAppService,
            CaisseSalonService caisseSalonService,
            AuditLogService auditLogService,
            SalonRepository salonRepository,
            CompteRepository compteRepository,
            RoleSalonRepository roleSalonRepository,
            PasswordEncoder passwordEncoder) {
        super();
        this.commandeRepository = commandeRepository;
        this.panierService = panierService;
        this.produitRepository = produitRepository;
        this.stockProduitRepository = stockProduitRepository;
        this.mouvementStockRepository = mouvementStockRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.factureRepository = factureRepository;
        this.affectationSalonRepository = affectationSalonRepository;
        this.notificationEmailService = notificationEmailService;
        this.notificationAppService = notificationAppService;
        this.caisseSalonService = caisseSalonService;
        this.auditLogService = auditLogService;
        this.salonRepository = salonRepository;
        this.compteRepository = compteRepository;
        this.roleSalonRepository = roleSalonRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final Random RANDOM = new Random();

    @Transactional
    public CommandeDTOResponse passerCommande(String slugSalon, String clientEmail) {
        AffectationSalon clientAffectation = validerClient(slugSalon, clientEmail);
        Panier panier = panierService.getOrCreatePanier(clientAffectation);

        if (panier.getLignes() == null || panier.getLignes().isEmpty()) {
            throw new IllegalStateException("Votre panier est vide. Impossible de passer une commande.");
        }

        // Vérification des stocks pour chaque produit
        for (LignePanier lp : panier.getLignes()) {
            Produit p = lp.getProduit();
            if (!p.isStatut()) {
                throw new IllegalArgumentException("Le produit '" + p.getNom() + "' n'est plus actif à la vente.");
            }
            StockProduit sp = p.getStock();
            int dispo = (sp != null) ? sp.getQuantiteDisponible() : 0;
            if (lp.getQuantite() > dispo) {
                throw new StockInsuffisantException("Stock insuffisant pour le produit '" + p.getNom() + "'. Disponible : " + dispo + ", demandé : " + lp.getQuantite());
            }
        }

        String numeroCmd = "CMD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        BigDecimal montantTotal = panier.calculerTotal();

        Commande commande = Commande.builder()
                .numeroCommande(numeroCmd)
                .dateCommande(LocalDateTime.now())
                .statut(StatutCommande.EN_ATTENTE)
                .montantTotal(montantTotal)
                .clientAffectation(clientAffectation)
                .build();

        for (LignePanier lp : panier.getLignes()) {
            LigneCommande lc = LigneCommande.builder()
                    .commande(commande)
                    .produit(lp.getProduit())
                    .quantite(lp.getQuantite())
                    .prixUnitaire(lp.getProduit().getPrixVente())
                    .sousTotal(lp.calculerSousTotal())
                    .build();
            commande.getLignes().add(lc);
        }

        Commande savedCommande = commandeRepository.save(commande);

        // Vider le panier
        panierService.viderPanier(slugSalon, clientEmail);

        // Notification email au client
        String nomSalon = clientAffectation.getSalon().getNom();
        String sujet = "Confirmation de votre commande " + savedCommande.getNumeroCommande() + " - " + nomSalon;
        String corps = String.format(
                "Bonjour %s,\n\n" +
                "Votre commande %s a été enregistrée avec succès auprès du salon %s.\n\n" +
                "Récapitulatif :\n" +
                "- Montant total : %s FCFA\n" +
                "- Statut : En attente de préparation\n\n" +
                "Notre équipe prépare votre commande. Vous recevrez un nouvel email dès qu'elle sera validée avec votre facture et votre code de retrait.\n\n" +
                "Cordialement,\n%s",
                clientAffectation.getCompte().getPrenom(),
                savedCommande.getNumeroCommande(),
                nomSalon,
                savedCommande.getMontantTotal(),
                nomSalon
        );
        notificationEmailService.queueNotificationEmail(clientEmail, sujet, corps);

        auditLogService.logActionSalon(
                TypeActionAudit.CREATION,
                "Commande",
                savedCommande.getId().toString(),
                null,
                "EN_ATTENTE (N° " + savedCommande.getNumeroCommande() + ", Montant : " + savedCommande.getMontantTotal() + ")",
                clientAffectation,
                "CLIENT"
        );

        log.info("Commande {} créée avec succès par {} pour le salon {}", savedCommande.getNumeroCommande(), clientEmail, slugSalon);
        return mapCommandeToResponse(savedCommande);
    }

    @Transactional
    public CommandeDTOResponse passerCommandeDepuisVitrine(String slugSalon, VitrineCommandeDTORequest request) {
        Salon salon = salonRepository.findBySlug(slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Salon introuvable avec le slug : " + slugSalon));

        if (!Boolean.TRUE.equals(salon.getStatut())) {
            throw new IllegalArgumentException("Impossible de passer commande : ce salon est actuellement inactif.");
        }

        if (request.lignes() == null || request.lignes().isEmpty()) {
            throw new IllegalStateException("Votre panier est vide. Impossible de passer une commande.");
        }

        String telephone = (request.telephone() != null) ? request.telephone().trim() : null;
        if (telephone == null || telephone.isBlank()) {
            throw new IllegalArgumentException("Le numéro de téléphone est obligatoire pour passer une commande.");
        }

        String rawEmail = (request.email() != null && !request.email().isBlank())
                ? request.email().trim().toLowerCase(Locale.ROOT)
                : null;

        Compte compte = compteRepository.findByTelephone(telephone).orElse(null);
        if (compte == null && rawEmail != null) {
            compte = compteRepository.findByEmail(rawEmail).orElse(null);
        }

        if (compte == null) {
            String rawPassword = (request.password() != null && !request.password().isBlank())
                    ? request.password()
                    : UUID.randomUUID().toString().substring(0, 12);

            String nom = (request.nom() != null) ? request.nom().trim() : "Client";
            String prenom = (request.prenom() != null && !request.prenom().isBlank())
                    ? request.prenom().trim()
                    : "";
            if (prenom.isBlank() && nom.contains(" ")) {
                String[] parts = nom.split(" ", 2);
                prenom = parts[0];
                nom = parts[1];
            } else if (prenom.isBlank()) {
                prenom = nom;
            }

            compte = Compte.builder()
                    .nom(nom)
                    .prenom(prenom)
                    .email(rawEmail)
                    .telephone(telephone)
                    .password(passwordEncoder.encode(rawPassword))
                    .statut(true)
                    .build();
            compte = compteRepository.save(compte);
        } else if (compte.getEmail() == null && rawEmail != null) {
            compte.setEmail(rawEmail);
            compte = compteRepository.save(compte);
        }

        final Compte clientCompte = compte;
        AffectationSalon clientAffectation = affectationSalonRepository
                .findByCompteEmailAndSalonSlugAndStatutTrue(clientCompte.getEmail(), salon.getSlug())
                .or(() -> affectationSalonRepository.findByCompteAndSalon(clientCompte, salon))
                .orElseGet(() -> {
                    RoleSalon roleClient = roleSalonRepository.findByRole(TypeRoleSalon.CLIENT)
                            .orElseGet(() -> RoleSalon.builder().role(TypeRoleSalon.CLIENT).build());
                    Set<RoleSalon> roles = new HashSet<>();
                    roles.add(roleClient);

                    AffectationSalon nouvelle = AffectationSalon.builder()
                            .compte(clientCompte)
                            .salon(salon)
                            .roles(roles)
                            .statut(true)
                            .dateDebut(LocalDate.now())
                            .build();
                    return affectationSalonRepository.save(nouvelle);
                });

        BigDecimal montantTotal = BigDecimal.ZERO;
        List<LigneCommande> lignesCommande = new ArrayList<>();

        String numeroCmd = "CMD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Commande commande = Commande.builder()
                .numeroCommande(numeroCmd)
                .dateCommande(LocalDateTime.now())
                .statut(StatutCommande.EN_ATTENTE)
                .clientAffectation(clientAffectation)
                .build();

        for (VitrineCommandeDTORequest.LigneVitrineCommandeDTORequest item : request.lignes()) {
            if (item.quantite() <= 0) continue;
            Produit p = produitRepository.findByIdAndCategorieSalonSlug(item.produitId(), slugSalon)
                    .orElseThrow(() -> new EntityNotFoundException("Produit ID " + item.produitId() + " introuvable dans ce salon."));

            if (!p.isStatut()) {
                throw new IllegalArgumentException("Le produit '" + p.getNom() + "' n'est plus actif à la vente.");
            }
            StockProduit sp = p.getStock();
            int dispo = (sp != null) ? sp.getQuantiteDisponible() : 0;
            if (item.quantite() > dispo) {
                throw new StockInsuffisantException("Stock insuffisant pour le produit '" + p.getNom() + "'. Disponible : " + dispo + ", demandé : " + item.quantite());
            }

            BigDecimal sousTotal = p.getPrixVente().multiply(BigDecimal.valueOf(item.quantite()));
            montantTotal = montantTotal.add(sousTotal);

            LigneCommande lc = LigneCommande.builder()
                    .commande(commande)
                    .produit(p)
                    .quantite(item.quantite())
                    .prixUnitaire(p.getPrixVente())
                    .sousTotal(sousTotal)
                    .build();
            lignesCommande.add(lc);
        }

        if (lignesCommande.isEmpty()) {
            throw new IllegalStateException("Aucun article valide dans la commande.");
        }

        commande.setLignes(lignesCommande);
        commande.setMontantTotal(montantTotal);

        Commande savedCommande = commandeRepository.save(commande);

        // Vider le panier éventuel du client
        try {
            panierService.viderPanier(clientAffectation);
        } catch (Exception ignored) {}

        String nomSalon = salon.getNom();

        // Notification email
        if (rawEmail != null && rawEmail.contains("@")) {
            String sujet = "Confirmation de votre commande " + savedCommande.getNumeroCommande() + " - " + nomSalon;
            String corps = String.format(
                    "Bonjour %s,\n\n" +
                    "Votre commande Click & Collect %s a été enregistrée avec succès auprès du salon %s.\n\n" +
                    "Récapitulatif :\n" +
                    "- Montant total : %s FCFA\n" +
                    "- Mode de retrait : Click & Collect au comptoir du salon\n" +
                    "- Statut : En attente de préparation\n\n" +
                    "Vous recevrez une notification dès que votre commande sera prête au salon.\n\n" +
                    "Cordialement,\n%s",
                    clientCompte.getPrenom(),
                    savedCommande.getNumeroCommande(),
                    nomSalon,
                    savedCommande.getMontantTotal(),
                    nomSalon
            );
            notificationEmailService.queueNotificationEmail(rawEmail, sujet, corps);
        }

        if (notificationSmsService != null && telephone != null && !telephone.isBlank()) {
            try {
                notificationSmsService.queueStatutCommande(
                        telephone,
                        savedCommande.getNumeroCommande(),
                        "En attente de préparation",
                        nomSalon
                );
            } catch (Exception e) {
                log.warn("Impossible d'enfiler la notification SMS/WhatsApp de la commande : {}", e.getMessage());
            }
        }

        auditLogService.logActionSalon(
                TypeActionAudit.CREATION,
                "Commande",
                savedCommande.getId().toString(),
                null,
                "EN_ATTENTE Click & Collect (N° " + savedCommande.getNumeroCommande() + ", Montant : " + savedCommande.getMontantTotal() + ")",
                clientAffectation,
                "CLIENT"
        );

        log.info("Commande vitrine {} créée avec succès pour {} (salon {})", savedCommande.getNumeroCommande(), telephone, slugSalon);
        return mapCommandeToResponse(savedCommande);
    }

    @Transactional
    public CommandeDTOResponse validerCommande(String slugSalon, String responsableEmail, Long commandeId) {
        AffectationSalon responsable = validerResponsableStock(slugSalon, responsableEmail);

        Commande commande = commandeRepository.findByIdAndClientAffectationSalonSlug(commandeId, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Commande ID " + commandeId + " introuvable dans ce salon."));

        if (commande.getStatut() == StatutCommande.REJETEE) {
            throw new IllegalStateException("Une commande rejetée ne peut plus être validée.");
        }
        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new IllegalStateException("Seule une commande en attente peut être validée. Statut actuel : " + commande.getStatut());
        }

        // Décrémenter le stock et créer les mouvements VENTE
        for (LigneCommande lc : commande.getLignes()) {
            Produit p = lc.getProduit();
            StockProduit stock = p.getStock();
            if (stock == null || stock.getQuantiteDisponible() < lc.getQuantite()) {
                throw new StockInsuffisantException("Stock insuffisant pour valider la commande sur le produit " + p.getNom());
            }

            stock.setQuantiteDisponible(stock.getQuantiteDisponible() - lc.getQuantite());
            stock.setDateDerniereMiseAJour(LocalDateTime.now());
            stockProduitRepository.save(stock);

            MouvementStock mvt = MouvementStock.builder()
                    .produit(p)
                    .quantite(lc.getQuantite())
                    .type(TypeMouvementStock.VENTE)
                    .prixUnitaire(lc.getPrixUnitaire())
                    .dateMouvement(LocalDateTime.now())
                    .motif("Vente commande " + commande.getNumeroCommande())
                    .auteur(responsable)
                    .build();
            mouvementStockRepository.save(mvt);
        }

        // Code de retrait sécurisé (6 chiffres)
        String codeRetrait = "RET-" + (100000 + RANDOM.nextInt(900000));

        // Génération automatique de la Facture
        String numFacture = "FAC-CMD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Facture facture = Facture.builder()
                .numeroFacture(numFacture)
                .dateEmission(LocalDateTime.now())
                .montantTotal(commande.getMontantTotal())
                .commande(commande)
                .clientAffectation(commande.getClientAffectation())
                .build();
        Facture savedFacture = factureRepository.save(facture);

        commande.setStatut(StatutCommande.VALIDEE);
        commande.setTraiteParAffectation(responsable);
        commande.setDateTraitement(LocalDateTime.now());
        commande.setCodeRetrait(codeRetrait);
        commande.setFacture(savedFacture);
        Commande updated = commandeRepository.save(commande);

        // Notification email au client avec facture et code de retrait
        String emailClient = commande.getClientAffectation().getCompte().getEmail();
        String nomSalon = commande.getClientAffectation().getSalon().getNom();
        String sujet = "Votre commande " + commande.getNumeroCommande() + " est prête ! - " + nomSalon;
        String corps = String.format(
                "Bonjour %s,\n\n" +
                "Bonne nouvelle ! Votre commande %s a été validée et est prête pour votre retrait au salon %s.\n\n" +
                "Informations de retrait (Click & Collect) :\n" +
                "- Code de retrait sécurisé : %s\n" +
                "- Facture n° : %s\n" +
                "- Montant total : %s FCFA\n\n" +
                "Veuillez présenter ce code lors de votre passage au salon pour récupérer vos produits.\n\n" +
                "À bientôt,\n%s",
                commande.getClientAffectation().getCompte().getPrenom(),
                commande.getNumeroCommande(),
                nomSalon,
                codeRetrait,
                savedFacture.getNumeroFacture(),
                commande.getMontantTotal(),
                nomSalon
        );
        if (emailClient != null && emailClient.contains("@") && !emailClient.endsWith(".client")) {
            notificationEmailService.queueNotificationEmail(emailClient, sujet, corps);
        }

        if (notificationSmsService != null && commande.getClientAffectation() != null && commande.getClientAffectation().getCompte() != null) {
            String tel = commande.getClientAffectation().getCompte().getTelephone();
            if (tel != null && !tel.isBlank()) {
                try {
                    notificationSmsService.queueCommandePrete(
                            tel,
                            commande.getNumeroCommande(),
                            codeRetrait,
                            nomSalon
                    );
                } catch (Exception e) {
                    log.warn("Impossible d'enfiler le SMS/WhatsApp de commande prête : {}", e.getMessage());
                }
            }
        }

        // Notification In-App au client
        if (commande.getClientAffectation() != null && commande.getClientAffectation().getCompte() != null) {
            String titreNotif = "Votre commande est prête !";
            String contenuNotif = String.format("Votre commande %s au salon %s a été validée. Code de retrait : %s. Montant : %s FCFA.",
                    commande.getNumeroCommande(), nomSalon, codeRetrait, commande.getMontantTotal());
            notificationAppService.creerNotification(
                    commande.getClientAffectation().getCompte(),
                    titreNotif,
                    contenuNotif,
                    TypeNotification.COMMANDE,
                    commande.getNumeroCommande()
            );
        }

        auditLogService.logActionSalon(
                TypeActionAudit.MODIFICATION,
                "Commande",
                updated.getId().toString(),
                "EN_ATTENTE",
                "VALIDEE (Code retrait: " + codeRetrait + ", Facture: " + savedFacture.getNumeroFacture() + ")",
                responsable,
                "RESPONSABLE_STOCK"
        );

        log.info("Commande {} validée par {}. Facture {} générée.", updated.getNumeroCommande(), responsableEmail, savedFacture.getNumeroFacture());
        return mapCommandeToResponse(updated, true);
    }

    @Transactional
    public CommandeDTOResponse rejeterCommande(String slugSalon, String responsableEmail, Long commandeId, RejetCommandeDTORequest request) {
        AffectationSalon responsable = validerResponsableStock(slugSalon, responsableEmail);

        Commande commande = commandeRepository.findByIdAndClientAffectationSalonSlug(commandeId, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Commande ID " + commandeId + " introuvable dans ce salon."));

        if (commande.getStatut() == StatutCommande.VALIDEE) {
            throw new IllegalStateException("Une commande validée ne peut pas être rejetée.");
        }
        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new IllegalStateException("Seule une commande en attente peut être rejetée. Statut actuel : " + commande.getStatut());
        }

        commande.setStatut(StatutCommande.REJETEE);
        commande.setMotifRejet(request.motif().trim());
        commande.setTraiteParAffectation(responsable);
        commande.setDateTraitement(LocalDateTime.now());
        Commande updated = commandeRepository.save(commande);

        // Notification email au client avec motif de rejet
        String emailClient = commande.getClientAffectation().getCompte().getEmail();
        String nomSalon = commande.getClientAffectation().getSalon().getNom();
        String sujet = "Information concernant votre commande " + commande.getNumeroCommande() + " - " + nomSalon;
        String corps = String.format(
                "Bonjour %s,\n\n" +
                "Nous vous informons que votre commande %s auprès du salon %s n'a pas pu être validée.\n\n" +
                "Motif du refus :\n\"%s\"\n\n" +
                "Aucun montant n'a été facturé. Nous vous prions de nous excuser pour ce désagrément et restons à votre disposition au salon.\n\n" +
                "Cordialement,\n%s",
                commande.getClientAffectation().getCompte().getPrenom(),
                commande.getNumeroCommande(),
                nomSalon,
                request.motif().trim(),
                nomSalon
        );
        if (emailClient != null && emailClient.contains("@") && !emailClient.endsWith(".client")) {
            notificationEmailService.queueNotificationEmail(emailClient, sujet, corps);
        }

        if (notificationSmsService != null && commande.getClientAffectation() != null && commande.getClientAffectation().getCompte() != null) {
            String tel = commande.getClientAffectation().getCompte().getTelephone();
            if (tel != null && !tel.isBlank()) {
                try {
                    notificationSmsService.queueStatutCommande(
                            tel,
                            commande.getNumeroCommande(),
                            "Refusée (" + request.motif().trim() + ")",
                            nomSalon
                    );
                } catch (Exception e) {
                    log.warn("Impossible d'enfiler le SMS/WhatsApp de refus de commande : {}", e.getMessage());
                }
            }
        }

        // Notification In-App au client
        if (commande.getClientAffectation() != null && commande.getClientAffectation().getCompte() != null) {
            String titreNotif = "Commande non validée";
            String contenuNotif = String.format("Votre commande %s au salon %s n'a pas pu être validée. Motif : %s",
                    commande.getNumeroCommande(), nomSalon, request.motif().trim());
            notificationAppService.creerNotification(
                    commande.getClientAffectation().getCompte(),
                    titreNotif,
                    contenuNotif,
                    TypeNotification.COMMANDE,
                    commande.getNumeroCommande()
            );
        }

        auditLogService.logActionSalon(
                TypeActionAudit.MODIFICATION,
                "Commande",
                updated.getId().toString(),
                "EN_ATTENTE",
                "REJETEE (Motif: " + request.motif().trim() + ")",
                responsable,
                "RESPONSABLE_STOCK"
        );

        log.info("Commande {} rejetée par {}. Motif : {}", updated.getNumeroCommande(), responsableEmail, request.motif());
        return mapCommandeToResponse(updated, true);
    }

    @Transactional
    public CommandeDTOResponse confirmerRetrait(String slugSalon, String employeEmail, Long commandeId, RetraitCommandeDTORequest request) {
        AffectationSalon affectation = validerPersonnelSalon(slugSalon, employeEmail);

        Commande commande = commandeRepository.findByIdAndClientAffectationSalonSlug(commandeId, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Commande ID " + commandeId + " introuvable dans ce salon."));

        if (commande.getStatut() != StatutCommande.VALIDEE) {
            throw new IllegalStateException("Seule une commande validée peut être marquée comme retirée. Statut actuel : " + commande.getStatut());
        }

        if (request != null && request.codeRetrait() != null && !request.codeRetrait().isBlank()) {
            if (!request.codeRetrait().trim().equalsIgnoreCase(commande.getCodeRetrait())) {
                throw new IllegalArgumentException("Code de retrait invalide pour cette commande.");
            }
        }

        MoyenPaiement moyenPaiement = (request != null && request.moyenPaiement() != null)
                ? request.moyenPaiement()
                : MoyenPaiement.ESPECES;

        commande.setMoyenPaiement(moyenPaiement);
        commande.setStatut(StatutCommande.RECUPEREE);
        commande.setDateTraitement(LocalDateTime.now());

        Optional<SessionCaisse> sessionActiveOpt = caisseSalonService.trouverSessionActive(slugSalon);
        if (sessionActiveOpt.isPresent()) {
            SessionCaisse sessionActive = sessionActiveOpt.get();
            caisseSalonService.enregistrerEncaissementCommande(commande, sessionActive, affectation, moyenPaiement, false);
            log.info("Commande {} encaissée en direct dans la session de caisse #{}", commande.getNumeroCommande(), sessionActive.getId());
        } else {
            commande.setEncaisseeEnCaisse(false);
            commandeRepository.save(commande);
            log.warn("Aucune session de caisse active pour le salon {}. La commande {} est marquée RECUPEREE et sera régularisée à l'ouverture de la prochaine session.", slugSalon, commande.getNumeroCommande());
        }

        Commande updated = commandeRepository.findById(commande.getId()).orElse(commande);

        // Notification In-App au client
        if (updated.getClientAffectation() != null && updated.getClientAffectation().getCompte() != null) {
            String nomSalon = updated.getClientAffectation().getSalon() != null ? updated.getClientAffectation().getSalon().getNom() : slugSalon;
            String titreNotif = "Commande récupérée avec succès";
            String contenuNotif = String.format("Votre commande %s a bien été retirée au salon %s. Merci pour vos achats !",
                    updated.getNumeroCommande(), nomSalon);
            notificationAppService.creerNotification(
                    updated.getClientAffectation().getCompte(),
                    titreNotif,
                    contenuNotif,
                    TypeNotification.COMMANDE,
                    updated.getNumeroCommande()
            );
        }

        auditLogService.logActionSalon(
                TypeActionAudit.MODIFICATION,
                "Commande",
                updated.getId().toString(),
                "VALIDEE",
                "RECUPEREE (Retrait confirmé, Paiement: " + moyenPaiement.name() + ")",
                affectation,
                "RESPONSABLE_STOCK"
        );

        log.info("Commande {} marquée comme RECUPEREE par {}", updated.getNumeroCommande(), employeEmail);
        return mapCommandeToResponse(updated, true);
    }

    // --- Consultations Client ---

    @Transactional(readOnly = true)
    public List<CommandeDTOResponse> listerCommandesClient(String slugSalon, String clientEmail) {
        validerClient(slugSalon, clientEmail);
        return commandeRepository.findByClientAffectationCompteEmailAndClientAffectationSalonSlugOrderByDateCommandeDesc(clientEmail, slugSalon).stream()
                .map(this::mapCommandeToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommandeDTOResponse obtenirCommandeClient(String slugSalon, String clientEmail, Long commandeId) {
        validerClient(slugSalon, clientEmail);
        Commande c = commandeRepository.findByIdAndClientAffectationCompteEmailAndClientAffectationSalonSlug(commandeId, clientEmail, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Commande ID " + commandeId + " introuvable pour votre compte sur ce salon."));
        return mapCommandeToResponse(c);
    }

    @Transactional(readOnly = true)
    public List<CommandeDTOResponse> listerToutesCommandesClient(String clientEmail) {
        return listerCommandesClientFiltrees(clientEmail, null, null);
    }

    @Transactional(readOnly = true)
    public List<CommandeDTOResponse> listerCommandesClientFiltrees(String identifiant, String slugSalon, StatutCommande statut) {
        Compte client = compteRepository.findByIdentifiant(identifiant)
                .or(() -> compteRepository.findByEmail(identifiant))
                .or(() -> compteRepository.findByTelephone(identifiant))
                .orElse(null);

        Long clientId = client != null ? client.getId() : null;
        String slug = (slugSalon != null && !slugSalon.isBlank()) ? slugSalon.trim() : null;

        return commandeRepository.findCommandesClientFiltre(clientId, identifiant, slug, statut).stream()
                .map(this::mapCommandeToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommandeDTOResponse obtenirCommandeClientGlobal(String clientEmail, Long commandeId) {
        Commande c = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande ID " + commandeId + " introuvable."));

        Compte compte = (c.getClientAffectation() != null) ? c.getClientAffectation().getCompte() : null;
        boolean matchEmail = compte != null && compte.getEmail() != null && compte.getEmail().equalsIgnoreCase(clientEmail);
        boolean matchTel = compte != null && compte.getTelephone() != null && compte.getTelephone().equals(clientEmail);

        if (!matchEmail && !matchTel) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à consulter cette commande.");
        }
        return mapCommandeToResponse(c);
    }


    // --- Consultations Responsable Stock ---

    @Transactional(readOnly = true)
    public List<CommandeDTOResponse> listerCommandesSalon(String slugSalon, String responsableEmail, StatutCommande statut, LocalDateTime debut, LocalDateTime fin) {
        validerResponsableStock(slugSalon, responsableEmail);

        List<Commande> liste;
        if (statut != null && debut != null && fin != null) {
            liste = commandeRepository.findByClientAffectationSalonSlugAndStatutAndDateCommandeBetweenOrderByDateCommandeDesc(slugSalon, statut, debut, fin);
        } else if (statut != null) {
            liste = commandeRepository.findByClientAffectationSalonSlugAndStatutOrderByDateCommandeDesc(slugSalon, statut);
        } else if (debut != null && fin != null) {
            liste = commandeRepository.findByClientAffectationSalonSlugAndDateCommandeBetweenOrderByDateCommandeDesc(slugSalon, debut, fin);
        } else {
            liste = commandeRepository.findByClientAffectationSalonSlugOrderByDateCommandeDesc(slugSalon);
        }

        return liste.stream().map(c -> mapCommandeToResponse(c, true)).toList();
    }

    @Transactional(readOnly = true)
    public CommandeDTOResponse obtenirCommandeSalon(String slugSalon, String responsableEmail, Long commandeId) {
        validerResponsableStock(slugSalon, responsableEmail);
        Commande c = commandeRepository.findByIdAndClientAffectationSalonSlug(commandeId, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Commande ID " + commandeId + " introuvable dans ce salon."));
        return mapCommandeToResponse(c, true);
    }

    @Transactional(readOnly = true)
    public List<ProduitAlerteStockDTOResponse> obtenirProduitsBientotEnRupture(String slugSalon, String responsableEmail) {
        validerResponsableStock(slugSalon, responsableEmail);
        List<Produit> alertes = produitRepository.findProduitsEnAlerteStock(slugSalon);

        return alertes.stream().map(p -> {
            StockProduit s = p.getStock();
            int dispo = (s != null) ? s.getQuantiteDisponible() : 0;
            int seuilMin = (s != null) ? s.getSeuilMinimum() : 0;
            Integer seuilMax = (s != null) ? s.getSeuilMaximum() : null;
            return new ProduitAlerteStockDTOResponse(
                    p.getId(),
                    p.getNom(),
                    p.getCategorie().getId(),
                    p.getCategorie().getNom(),
                    p.getPrixVente(),
                    dispo,
                    seuilMin,
                    seuilMax,
                    dispo == 0
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public KpiStockDTOResponse obtenirKpiStock(String slugSalon, String responsableEmail) {
        validerResponsableStock(slugSalon, responsableEmail);

        long totalProduitsActifs = produitRepository.countByCategorieSalonSlugAndStatut(slugSalon, true);
        long totalProduitsEnAlerte = produitRepository.countProduitsEnAlerte(slugSalon);
        long totalProduitsEnRupture = produitRepository.countProduitsEnRupture(slugSalon);
        BigDecimal valeurTotaleStock = produitRepository.calculateValeurTotaleStock(slugSalon);

        long totalCommandes = commandeRepository.countByClientAffectationSalonSlug(slugSalon);
        long commandesEnAttente = commandeRepository.countByClientAffectationSalonSlugAndStatut(slugSalon, StatutCommande.EN_ATTENTE);
        long commandesValidees = commandeRepository.countByClientAffectationSalonSlugAndStatut(slugSalon, StatutCommande.VALIDEE);
        long commandesRejetees = commandeRepository.countByClientAffectationSalonSlugAndStatut(slugSalon, StatutCommande.REJETEE);
        long commandesRecuperees = commandeRepository.countByClientAffectationSalonSlugAndStatut(slugSalon, StatutCommande.RECUPEREE);
        BigDecimal caCommandes = commandeRepository.totalChiffreAffairesCommandesValidees(slugSalon);
        if (caCommandes == null) {
            caCommandes = BigDecimal.ZERO;
        }

        BigDecimal caVentesDirectes = BigDecimal.ZERO;
        List<MouvementStock> mouvementsKpi = mouvementStockRepository.findByProduitCategorieSalonSlugOrderByDateMouvementDesc(slugSalon);
        for (MouvementStock mvt : mouvementsKpi) {
            if (mvt.getType() == TypeMouvementStock.VENTE
                    && (mvt.getMotif() == null || !mvt.getMotif().startsWith("Vente commande"))) {
                BigDecimal pu = (mvt.getPrixUnitaire() != null && mvt.getPrixUnitaire().compareTo(BigDecimal.ZERO) > 0)
                        ? mvt.getPrixUnitaire()
                        : (mvt.getProduit() != null && mvt.getProduit().getPrixVente() != null ? mvt.getProduit().getPrixVente() : BigDecimal.ZERO);
                int qte = mvt.getQuantite() != null ? mvt.getQuantite() : 0;
                caVentesDirectes = caVentesDirectes.add(pu.multiply(BigDecimal.valueOf(qte)));
            }
        }
        BigDecimal caVentes = caCommandes.add(caVentesDirectes);

        return new KpiStockDTOResponse(
                totalProduitsActifs,
                totalProduitsEnAlerte,
                totalProduitsEnRupture,
                valeurTotaleStock != null ? valeurTotaleStock : BigDecimal.ZERO,
                totalCommandes,
                commandesEnAttente,
                commandesValidees,
                commandesRejetees,
                commandesRecuperees,
                caVentes
        );
    }

    @Transactional(readOnly = true)
    public List<ProduitRentabiliteDTOResponse> obtenirProduitsRentables(String slugSalon, String responsableEmail, int limit) {
        validerResponsableStock(slugSalon, responsableEmail);

        List<Produit> produits = produitRepository.findByCategorieSalonSlugAndStatut(slugSalon, true);
        if (produits.isEmpty()) {
            return List.of();
        }

        Map<Long, VenteStat> statsVentes = new HashMap<>();
        List<Object[]> rawVentes = ligneCommandeRepository.agregationVentesParProduit(slugSalon);
        for (Object[] row : rawVentes) {
            Long prodId = (Long) row[0];
            long qte = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            BigDecimal ca = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
            statsVentes.put(prodId, new VenteStat(qte, ca));
        }

        List<MouvementStock> mouvements = mouvementStockRepository.findByProduitCategorieSalonSlugOrderByDateMouvementDesc(slugSalon);
        for (MouvementStock mvt : mouvements) {
            if (mvt.getType() == TypeMouvementStock.VENTE
                    && (mvt.getMotif() == null || !mvt.getMotif().startsWith("Vente commande"))) {
                Long prodId = mvt.getProduit().getId();
                long qte = mvt.getQuantite() != null ? mvt.getQuantite().longValue() : 0L;
                BigDecimal pu = (mvt.getPrixUnitaire() != null && mvt.getPrixUnitaire().compareTo(BigDecimal.ZERO) > 0)
                        ? mvt.getPrixUnitaire()
                        : (mvt.getProduit() != null && mvt.getProduit().getPrixVente() != null ? mvt.getProduit().getPrixVente() : BigDecimal.ZERO);
                BigDecimal ca = pu.multiply(BigDecimal.valueOf(qte));
                VenteStat existant = statsVentes.getOrDefault(prodId, new VenteStat(0L, BigDecimal.ZERO));
                statsVentes.put(prodId, new VenteStat(existant.qte() + qte, existant.ca().add(ca)));
            }
        }
        Map<Long, BigDecimal> dernierPrixAchatParProduit = new HashMap<>();
        for (MouvementStock mvt : mouvements) {
            if (mvt.getType() == TypeMouvementStock.ENTREE
                    && mvt.getPrixUnitaire() != null
                    && mvt.getPrixUnitaire().compareTo(BigDecimal.ZERO) > 0) {
                dernierPrixAchatParProduit.putIfAbsent(mvt.getProduit().getId(), mvt.getPrixUnitaire());
            }
        }

        List<ProduitRentabiliteDTOResponse> resultats = new ArrayList<>();
        for (Produit prod : produits) {
            BigDecimal pv = prod.getPrixVente() != null ? prod.getPrixVente() : BigDecimal.ZERO;
            BigDecimal pa = dernierPrixAchatParProduit.get(prod.getId());

            BigDecimal margeUnitaire;
            BigDecimal tauxMarge;
            BigDecimal tauxMarque;

            if (pa != null && pa.compareTo(BigDecimal.ZERO) > 0) {
                margeUnitaire = pv.subtract(pa);
                tauxMarge = margeUnitaire.divide(pa, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(1, RoundingMode.HALF_UP);
                if (pv.compareTo(BigDecimal.ZERO) > 0) {
                    tauxMarque = margeUnitaire.divide(pv, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(1, RoundingMode.HALF_UP);
                } else {
                    tauxMarque = BigDecimal.ZERO;
                }
            } else {
                margeUnitaire = pv;
                tauxMarge = BigDecimal.valueOf(100.0);
                tauxMarque = BigDecimal.valueOf(100.0);
            }

            VenteStat vStat = statsVentes.getOrDefault(prod.getId(), new VenteStat(0L, BigDecimal.ZERO));
            BigDecimal beneficeTotal = margeUnitaire.multiply(BigDecimal.valueOf(vStat.qte()));

            String niveau;
            double tm = tauxMarge.doubleValue();
            if (tm >= 60.0) {
                niveau = "TRES_RENTABLE";
            } else if (tm >= 35.0) {
                niveau = "RENTABLE";
            } else if (tm >= 15.0) {
                niveau = "MOYEN";
            } else {
                niveau = "FAIBLE";
            }

            long stockDispo = (prod.getStock() != null && prod.getStock().getQuantiteDisponible() != null)
                    ? prod.getStock().getQuantiteDisponible().longValue()
                    : 0L;

            resultats.add(new ProduitRentabiliteDTOResponse(
                    prod.getId(),
                    prod.getNom(),
                    prod.getImageUrl(),
                    prod.getCategorie() != null ? prod.getCategorie().getId() : null,
                    prod.getCategorie() != null ? prod.getCategorie().getNom() : "Général",
                    pv,
                    pa,
                    margeUnitaire,
                    tauxMarge,
                    tauxMarque,
                    vStat.qte(),
                    stockDispo,
                    vStat.ca(),
                    beneficeTotal,
                    niveau
            ));
        }

        resultats.sort((a, b) -> {
            int compBenefice = b.beneficeTotal().compareTo(a.beneficeTotal());
            if (compBenefice != 0) return compBenefice;
            int compVentes = Long.compare(b.quantiteVendue(), a.quantiteVendue());
            if (compVentes != 0) return compVentes;
            return b.margeUnitaire().compareTo(a.margeUnitaire());
        });

        if (limit > 0 && resultats.size() > limit) {
            return resultats.subList(0, limit);
        }
        return resultats;
    }

    private record VenteStat(long qte, BigDecimal ca) {}

    // --- Helpers de validation d'affectation ---

    private AffectationSalon validerClient(String slugSalon, String email) {
        // 1. Vérification rapide : l'utilisateur a-t-il déjà une affectation active sur ce salon ?
        Optional<AffectationSalon> existingOpt =
                affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue(email, slugSalon);

        if (existingOpt.isPresent()) {
            AffectationSalon affectation = existingOpt.get();
            boolean isClient = affectation.getRoles().stream()
                    .anyMatch(r -> r.getRole() == TypeRoleSalon.CLIENT);
            if (!isClient && roleSalonRepository != null) {
                RoleSalon roleClient = roleSalonRepository.findByRole(TypeRoleSalon.CLIENT).orElse(null);
                if (roleClient != null) {
                    affectation.getRoles().add(roleClient);
                    affectation = affectationSalonRepository.save(affectation);
                }
            }
            return affectation;
        }

        // 2. Auto-création si inexistante
        if (salonRepository == null || compteRepository == null || roleSalonRepository == null) {
            throw new EntityNotFoundException("Affectation active introuvable pour le client " + email + " sur le salon " + slugSalon);
        }

        Salon salon = salonRepository.findBySlug(slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Salon introuvable avec le slug : " + slugSalon));

        Compte compte = compteRepository.findByIdentifiant(email)
                .orElseThrow(() -> new EntityNotFoundException("Compte client introuvable : " + email));

        if (!Boolean.TRUE.equals(compte.getStatut())) {
            throw new ForbiddenException("Votre compte est actuellement désactivé.");
        }

        RoleSalon roleClient = roleSalonRepository.findByRole(TypeRoleSalon.CLIENT)
                .orElseGet(() -> RoleSalon.builder().role(TypeRoleSalon.CLIENT).build());
        Set<RoleSalon> roles = new HashSet<>();
        roles.add(roleClient);

        AffectationSalon nouvelle = AffectationSalon.builder()
                .compte(compte)
                .salon(salon)
                .roles(roles)
                .statut(true)
                .dateDebut(LocalDate.now())
                .build();
        AffectationSalon saved = affectationSalonRepository.save(nouvelle);
        log.info("Auto-création de l'affectation CLIENT pour {} sur le salon {}", email, slugSalon);
        return saved;
    }

    private AffectationSalon validerResponsableStock(String slugSalon, String email) {
        AffectationSalon affectation = affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue(email, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Affectation active introuvable pour l'utilisateur " + email + " sur le salon " + slugSalon));

        boolean hasRole = affectation.getRoles().stream()
                .anyMatch(r -> r.getRole() == TypeRoleSalon.RESPONSABLE_STOCK || r.getRole() == TypeRoleSalon.MANAGER);

        if (!hasRole) {
            throw new IllegalArgumentException("L'utilisateur " + email + " ne possède pas le rôle RESPONSABLE_STOCK sur le salon " + slugSalon);
        }
        return affectation;
    }

    private AffectationSalon validerPersonnelSalon(String slugSalon, String email) {
        AffectationSalon affectation = affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue(email, slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Affectation active introuvable pour l'utilisateur " + email + " sur le salon " + slugSalon));

        boolean hasRole = affectation.getRoles().stream()
                .anyMatch(r -> r.getRole() == TypeRoleSalon.RESPONSABLE_STOCK || r.getRole() == TypeRoleSalon.RECEPTIONNISTE || r.getRole() == TypeRoleSalon.MANAGER);

        if (!hasRole) {
            throw new IllegalArgumentException("L'utilisateur " + email + " n'est pas autorisé à confirmer les retraits sur le salon " + slugSalon);
        }
        return affectation;
    }

    public CommandeDTOResponse mapCommandeToResponse(Commande c) {
        return mapCommandeToResponse(c, false);
    }

    public CommandeDTOResponse mapCommandeToResponse(Commande c, boolean masquerCodeRetrait) {
        List<LigneCommandeDTOResponse> lignesDTO = (c.getLignes() == null) ? List.of() :
                c.getLignes().stream()
                .map(l -> new LigneCommandeDTOResponse(
                        l.getId(),
                        l.getProduit().getId(),
                        l.getProduit().getNom(),
                        l.getQuantite(),
                        l.getPrixUnitaire(),
                        l.getSousTotal()
                ))
                .toList();

        String clientNom = (c.getClient() != null) ? c.getClient().getPrenom() + " " + c.getClient().getNom() : "Client";
        String clientEmail = (c.getClient() != null) ? c.getClient().getEmail() : "N/A";
        String traiteParNom = (c.getTraiteParAffectation() != null && c.getTraiteParAffectation().getCompte() != null)
                ? c.getTraiteParAffectation().getCompte().getPrenom() + " " + c.getTraiteParAffectation().getCompte().getNom()
                : null;

        Long factureId = (c.getFacture() != null) ? c.getFacture().getId() : null;
        String numFacture = (c.getFacture() != null) ? c.getFacture().getNumeroFacture() : null;
        String codeRetrait = masquerCodeRetrait ? null : c.getCodeRetrait();

        return new CommandeDTOResponse(
                c.getId(),
                c.getNumeroCommande(),
                c.getDateCommande(),
                c.getStatut().name(),
                c.getMontantTotal(),
                clientNom,
                clientEmail,
                codeRetrait,
                c.getMotifRejet(),
                c.getDateTraitement(),
                traiteParNom,
                factureId,
                numFacture,
                lignesDTO,
                c.getEncaisseeEnCaisse(),
                (c.getMoyenPaiement() != null) ? c.getMoyenPaiement().name() : null
        );
    }
}
