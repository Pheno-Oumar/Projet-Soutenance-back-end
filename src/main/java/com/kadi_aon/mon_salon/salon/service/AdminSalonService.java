package com.kadi_aon.mon_salon.salon.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.notification.service.NotificationEmailService;
import com.kadi_aon.mon_salon.salon.dto.SalonCreateDTORequest;
import com.kadi_aon.mon_salon.salon.dto.SalonDTOResponse;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.RoleSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.mapper.SalonDTOResponseMapper;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.RoleSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminSalonService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

    private final SalonRepository salonRepository;
    private final CompteRepository compteRepository;
    private final RoleSalonRepository roleSalonRepository;
    private final AffectationSalonRepository affectationSalonRepository;
    private final SalonDTOResponseMapper salonDTOResponseMapper;
    private final NotificationEmailService notificationEmailService;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.kadi_aon.mon_salon.notification.service.NotificationSmsService notificationSmsService;

    public AdminSalonService(
            SalonRepository salonRepository,
            CompteRepository compteRepository,
            RoleSalonRepository roleSalonRepository,
            AffectationSalonRepository affectationSalonRepository,
            SalonDTOResponseMapper salonDTOResponseMapper,
            NotificationEmailService notificationEmailService,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {
        super();
        this.salonRepository = salonRepository;
        this.compteRepository = compteRepository;
        this.roleSalonRepository = roleSalonRepository;
        this.affectationSalonRepository = affectationSalonRepository;
        this.salonDTOResponseMapper = salonDTOResponseMapper;
        this.notificationEmailService = notificationEmailService;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    private Compte resolveAdminCompte(String adminEmail) {
        if (adminEmail == null || adminEmail.isBlank()) {
            return null;
        }
        return compteRepository.findByIdentifiant(adminEmail)
                .or(() -> adminEmail.contains("@")
                        ? compteRepository.findByEmail(adminEmail.toLowerCase(Locale.ROOT))
                        : compteRepository.findByTelephone(adminEmail))
                .orElse(null);
    }

    @Transactional
    public SalonDTOResponse creerSalon(SalonCreateDTORequest request, String adminEmail) {
        Compte adminCompte = resolveAdminCompte(adminEmail);

        // 1. Génération automatique du slug sécurisé et unique
        String slug = generateUniqueSlug(request.nom());

        // 2. Création et sauvegarde du Salon (initialisation minimale)
        Salon salon = Salon.builder()
                .nom(request.nom().trim())
                .slug(slug)
                .statut(true)
                .build();

        salon = salonRepository.save(salon);

        // 3. Gestion du propriétaire (existant ou création nouveau compte)
        String emailProprio = (request.emailProprietaire() != null && !request.emailProprietaire().isBlank())
                ? request.emailProprietaire().trim().toLowerCase(Locale.ROOT)
                : null;
        String telephoneProprio = request.telephoneProprietaire().trim();
        String paysProprio = (request.pays() != null && !request.pays().isBlank())
                ? request.pays().trim()
                : null;

        Compte proprioCompte = compteRepository.findByTelephone(telephoneProprio).orElse(null);
        if (proprioCompte == null && emailProprio != null) {
            proprioCompte = compteRepository.findByEmail(emailProprio).orElse(null);
        }

        if (proprioCompte != null && emailProprio != null && proprioCompte.getEmail() != null
                && !proprioCompte.getEmail().equalsIgnoreCase(emailProprio)) {
            throw new IllegalArgumentException("Le numéro de téléphone '" + telephoneProprio + "' est déjà associé à un compte avec une adresse email différente.");
        }

        if (proprioCompte == null) {
            if (emailProprio != null && compteRepository.existsByEmail(emailProprio)) {
                throw new IllegalArgumentException("L'adresse email '" + emailProprio + "' est déjà associée à un autre compte.");
            }
            String initialPassword = "12345";
            proprioCompte = Compte.builder()
                    .email(emailProprio)
                    .nom("Propriétaire")
                    .prenom(salon.getNom())
                    .telephone(telephoneProprio)
                    .pays(paysProprio)
                    .password(passwordEncoder.encode(initialPassword))
                    .doitChangerMotDePasse(true)
                    .statut(true)
                    .build();
            proprioCompte = compteRepository.save(proprioCompte);

            if (emailProprio != null) {
                notificationEmailService.queueNouveauProprietaireEmail(emailProprio, salon.getNom(), initialPassword);
            }

            if (notificationSmsService != null && telephoneProprio != null && !telephoneProprio.isBlank()) {
                try {
                    notificationSmsService.queueBienvenueCompte(
                            telephoneProprio,
                            salon.getNom(),
                            initialPassword
                    );
                } catch (Exception e) {
                    log.warn("Impossible d'enfiler le SMS/WhatsApp de bienvenue : {}", e.getMessage());
                }
            }

            log.info("Nouveau compte propriétaire créé pour tel: {} (email: {}) avec mot de passe initial prédéfini (changement requis à la 1ère connexion).", telephoneProprio, emailProprio);
        } else {
            log.info("Le compte propriétaire (tel: {}) existait déjà. Rattachement au nouveau salon.", proprioCompte.getTelephone());
        }

        // 4. Attribution de l'affectation active avec le rôle PROPRIETAIRE
        RoleSalon roleProprio = roleSalonRepository.findByRole(TypeRoleSalon.PROPRIETAIRE)
                .orElseThrow(() -> new IllegalStateException("Le rôle PROPRIETAIRE n'est pas initialisé en base."));

        AffectationSalon affectation = AffectationSalon.builder()
                .compte(proprioCompte)
                .salon(salon)
                .roles(new HashSet<>(Collections.singletonList(roleProprio)))
                .statut(true)
                .dateDebut(LocalDate.now())
                .build();
        affectationSalonRepository.save(affectation);

        // 5. Audit de l'action
        String identifiantAudit = emailProprio != null ? emailProprio : telephoneProprio;
        auditLogService.logActionPlateforme(
                TypeActionAudit.CREATION,
                "Salon",
                String.valueOf(salon.getId()),
                null,
                String.format("Création du salon '%s' (slug: %s) rattaché au propriétaire '%s'", salon.getNom(), slug, identifiantAudit),
                adminCompte,
                "ADMIN_SYSTEME"
        );

        return salonDTOResponseMapper.apply(salon);
    }

    @Transactional(readOnly = true)
    public List<SalonDTOResponse> listerSalons() {
        return salonRepository.findAll().stream()
                .map(salonDTOResponseMapper)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalonDTOResponse getSalonBySlug(String slug) {
        Salon salon = salonRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Salon introuvable avec le slug : " + slug));
        return salonDTOResponseMapper.apply(salon);
    }

    @Transactional
    public SalonDTOResponse desactiverSalon(String slug, String adminEmail) {
        Salon salon = salonRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Salon introuvable avec le slug : " + slug));

        Compte adminCompte = resolveAdminCompte(adminEmail);

        salon.setStatut(false);
        Salon updated = salonRepository.save(salon);

        auditLogService.logActionPlateforme(
                TypeActionAudit.DESACTIVATION,
                "Salon",
                String.valueOf(updated.getId()),
                "statut=true",
                "statut=false",
                adminCompte,
                "ADMIN_SYSTEME"
        );

        log.info("Salon désactivé : {} (slug: {})", updated.getNom(), slug);
        return salonDTOResponseMapper.apply(updated);
    }

    @Transactional
    public SalonDTOResponse reactiverSalon(String slug, String adminEmail) {
        Salon salon = salonRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Salon introuvable avec le slug : " + slug));

        Compte adminCompte = resolveAdminCompte(adminEmail);

        salon.setStatut(true);
        Salon updated = salonRepository.save(salon);

        auditLogService.logActionPlateforme(
                TypeActionAudit.REACTIVATION,
                "Salon",
                String.valueOf(updated.getId()),
                "statut=false",
                "statut=true",
                adminCompte,
                "ADMIN_SYSTEME"
        );

        log.info("Salon réactivé : {} (slug: {})", updated.getNom(), slug);
        return salonDTOResponseMapper.apply(updated);
    }

    public String generateUniqueSlug(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom du salon est obligatoire pour générer le slug.");
        }

        // Remplacement des accents
        String normalized = Normalizer.normalize(nom, Normalizer.Form.NFD);
        String slugBase = normalized.replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        if (slugBase.isBlank()) {
            slugBase = "salon";
        }

        String candidateSlug = slugBase;
        int counter = 1;
        while (salonRepository.existsBySlug(candidateSlug)) {
            candidateSlug = slugBase + "-" + counter;
            counter++;
        }
        return candidateSlug;
    }

    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
