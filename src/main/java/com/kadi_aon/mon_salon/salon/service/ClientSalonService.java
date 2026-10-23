package com.kadi_aon.mon_salon.salon.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.dto.CompteDTOResponse;
import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.mapper.CompteDTOResponseMapper;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.rendezvous.dto.RendezVousCreateDTORequest;
import com.kadi_aon.mon_salon.rendezvous.dto.RendezVousDTOResponse;
import com.kadi_aon.mon_salon.rendezvous.service.RendezVousService;
import com.kadi_aon.mon_salon.salon.dto.ClientRegisterDTORequest;
import com.kadi_aon.mon_salon.salon.dto.VitrineRendezVousDTORequest;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.RoleSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.RoleSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClientSalonService {

    private final SalonRepository salonRepository;
    private final CompteRepository compteRepository;
    private final RoleSalonRepository roleSalonRepository;
    private final AffectationSalonRepository affectationSalonRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompteDTOResponseMapper compteDTOResponseMapper;
    private final AuditLogService auditLogService;
    private final RendezVousService rendezVousService;

    public ClientSalonService(
            SalonRepository salonRepository,
            CompteRepository compteRepository,
            RoleSalonRepository roleSalonRepository,
            AffectationSalonRepository affectationSalonRepository,
            PasswordEncoder passwordEncoder,
            CompteDTOResponseMapper compteDTOResponseMapper,
            AuditLogService auditLogService,
            RendezVousService rendezVousService) {
        super();
        this.salonRepository = salonRepository;
        this.compteRepository = compteRepository;
        this.roleSalonRepository = roleSalonRepository;
        this.affectationSalonRepository = affectationSalonRepository;
        this.passwordEncoder = passwordEncoder;
        this.compteDTOResponseMapper = compteDTOResponseMapper;
        this.auditLogService = auditLogService;
        this.rendezVousService = rendezVousService;
    }

    @Transactional
    public CompteDTOResponse enregistrerClientDansSalon(String slugSalon, ClientRegisterDTORequest request) {
        Salon salon = salonRepository.findBySlug(slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Salon introuvable avec le slug : " + slugSalon));

        if (!Boolean.TRUE.equals(salon.getStatut())) {
            throw new IllegalArgumentException("Impossible de s'enregistrer : ce salon est actuellement inactif.");
        }

        String telephone = request.telephone().trim();
        String email = (request.email() != null && !request.email().isBlank())
                ? request.email().trim().toLowerCase(Locale.ROOT)
                : null;

        Compte compte = null;
        if (email != null) {
            try {
                compte = compteRepository.findByEmail(email).orElse(null);
            } catch (Exception ignored) {}
        }
        if (compte == null) {
            try {
                compte = compteRepository.findByTelephone(telephone).orElse(null);
            } catch (Exception ignored) {}
        }

        if (compte != null) {
            String identifiantRecherche = (compte.getEmail() != null && !compte.getEmail().isBlank())
                    ? compte.getEmail()
                    : compte.getTelephone();
            boolean aDejaAffectation = affectationSalonRepository
                    .findByCompteEmailAndSalonSlugAndStatutTrue(identifiantRecherche, slugSalon).isPresent();
            if (aDejaAffectation) {
                throw new IllegalArgumentException("Vous êtes déjà enregistré comme client dans ce salon.");
            }
            AffectationSalon affectationExistante = affectationSalonRepository.findByCompteAndSalon(compte, salon).orElse(null);
            if (affectationExistante != null && Boolean.FALSE.equals(affectationExistante.getStatut())) {
                throw new IllegalStateException("Votre compte client a été désactivé par ce salon. Pour réactiver votre accès, veuillez vous rendre directement au salon.");
            }
        } else {
            compte = Compte.builder()
                    .nom(request.nom().trim())
                    .prenom(request.prenom().trim())
                    .email(email)
                    .telephone(telephone)
                    .pays(request.pays() != null && !request.pays().isBlank() ? request.pays().trim() : null)
                    .password(passwordEncoder.encode(request.password()))
                    .dateNaissance(request.dateNaissance())
                    .statut(true)
                    .build();
            compte = compteRepository.save(compte);
        }

        RoleSalon roleClient = roleSalonRepository.findByRole(TypeRoleSalon.CLIENT)
                .orElseThrow(() -> new IllegalStateException("Rôle CLIENT non initialisé"));

        Set<RoleSalon> roles = new HashSet<>();
        roles.add(roleClient);

        AffectationSalon affectation = AffectationSalon.builder()
                .compte(compte)
                .salon(salon)
                .roles(roles)
                .statut(true)
                .dateDebut(LocalDate.now())
                .build();

        AffectationSalon saved = affectationSalonRepository.save(affectation);

        auditLogService.logActionSalon(
                TypeActionAudit.CREATION,
                "AffectationSalon (Client)",
                String.valueOf(saved.getId()),
                null,
                String.format("Inscription client '%s' dans le salon '%s'", compte.getTelephone(), slugSalon),
                saved,
                "CLIENT"
        );

        log.info("Client {} inscrit avec succès dans le salon {}", email, slugSalon);
        return compteDTOResponseMapper.apply(compte);
    }

    @Transactional
    public RendezVousDTOResponse reserverRendezVousDepuisVitrine(String slugSalon, VitrineRendezVousDTORequest request) {
        Salon salon = salonRepository.findBySlug(slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Salon introuvable avec le slug : " + slugSalon));

        if (!Boolean.TRUE.equals(salon.getStatut())) {
            throw new IllegalArgumentException("Impossible de réserver : ce salon est actuellement inactif.");
        }

        String telephone = request.telephone() != null ? request.telephone().trim() : null;
        if (telephone == null || telephone.isBlank()) {
            throw new IllegalArgumentException("Le numéro de téléphone est obligatoire pour réserver.");
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

            compte = Compte.builder()
                    .nom(request.nom().trim())
                    .prenom(request.prenom().trim())
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
        affectationSalonRepository
                .findByCompteAndSalon(clientCompte, salon)
                .filter(AffectationSalon::getStatut)
                .orElseGet(() -> {
                    RoleSalon roleClient = roleSalonRepository.findByRole(TypeRoleSalon.CLIENT)
                            .orElseThrow(() -> new IllegalStateException("Rôle CLIENT non initialisé"));
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

        RendezVousCreateDTORequest rdvReq = new RendezVousCreateDTORequest(
                request.dateHeurePrevue(),
                request.varianteIds(),
                request.coiffeurId()
        );

        String identifiantClient = clientCompte.getTelephone();
        return rendezVousService.creerRendezVousClient(slugSalon, rdvReq, identifiantClient);
    }
}
