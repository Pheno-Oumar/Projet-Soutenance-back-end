package com.kadi_aon.mon_salon.account.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.dto.ChangementMotDePasseDTORequest;
import com.kadi_aon.mon_salon.account.dto.CompteDTOResponse;
import com.kadi_aon.mon_salon.account.dto.CompteUpdateDTORequest;
import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.mapper.CompteDTOResponseMapper;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompteService {

    private final CompteRepository compteRepository;
    private final CompteDTOResponseMapper compteDTOResponseMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AffectationSalonRepository affectationSalonRepository;
    private final AuditLogService auditLogService;

    public CompteService(
            CompteRepository compteRepository,
            CompteDTOResponseMapper compteDTOResponseMapper,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService,
            AffectationSalonRepository affectationSalonRepository,
            AuditLogService auditLogService) {
        super();
        this.compteRepository = compteRepository;
        this.compteDTOResponseMapper = compteDTOResponseMapper;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.affectationSalonRepository = affectationSalonRepository;
        this.auditLogService = auditLogService;
    }

    private Compte findCompte(String identifiant) {
        if (identifiant == null || identifiant.isBlank()) {
            throw new EntityNotFoundException("Identifiant vide ou non renseigné");
        }
        String trimmed = identifiant.trim();
        Optional<Compte> compteOpt = compteRepository.findByEmail(trimmed.toLowerCase(java.util.Locale.ROOT));
        if (compteOpt.isEmpty()) {
            compteOpt = compteRepository.findByTelephone(trimmed);
        }
        if (compteOpt.isEmpty()) {
            compteOpt = compteRepository.findByIdentifiant(trimmed);
        }
        if (compteOpt.isEmpty()) {
            String compact = trimmed.replaceAll("\\s+", "");
            compteOpt = compteRepository.findByIdentifiant(compact)
                    .or(() -> {
                        if (compact.startsWith("+")) {
                            return compteRepository.findByIdentifiant(compact.substring(1));
                        } else {
                            return compteRepository.findByIdentifiant("+" + compact);
                        }
                    });
        }
        if (compteOpt.isEmpty()) {
            throw new EntityNotFoundException("Compte non trouvé pour l'identifiant : " + identifiant);
        }
        return compteOpt.get();
    }

    @Transactional(readOnly = true)
    public CompteDTOResponse getProfil(String identifiant) {
        Compte compte = findCompte(identifiant);
        return compteDTOResponseMapper.apply(compte);
    }

    @Transactional
    public CompteDTOResponse updateProfil(
            String email,
            CompteUpdateDTORequest request,
            String slugSalon,
            String roleActif) {

        Compte compte = findCompte(email);

        // Règle d'unicité sur le numéro de téléphone s'il a changé
        String currentTelCompact = compte.getTelephone() != null ? compte.getTelephone().replaceAll("\\s+", "") : "";
        String requestTelCompact = request.telephone() != null ? request.telephone().replaceAll("\\s+", "") : "";

        if (!requestTelCompact.isBlank() && !currentTelCompact.equals(requestTelCompact)) {
            Optional<Compte> existingTel = compteRepository.findByTelephone(request.telephone())
                    .or(() -> compteRepository.findByTelephone(requestTelCompact))
                    .or(() -> compteRepository.findByIdentifiant(requestTelCompact));
            if (existingTel.isPresent() && !existingTel.get().getId().equals(compte.getId())) {
                throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé par un autre compte.");
            }
        }

        // Gestion de l'email : ajout ou modification
        if (request.email() != null && !request.email().isBlank()) {
            String newEmail = request.email().trim().toLowerCase(java.util.Locale.ROOT);
            if (compte.getEmail() == null || !compte.getEmail().equalsIgnoreCase(newEmail)) {
                Optional<Compte> existingEmail = compteRepository.findByEmail(newEmail);
                if (existingEmail.isPresent() && !existingEmail.get().getId().equals(compte.getId())) {
                    throw new IllegalArgumentException("Cette adresse email est déjà associée à un autre compte.");
                }
                compte.setEmail(newEmail);
            }
        }

        String ancienneValeur = String.format("nom: %s, prenom: %s, telephone: %s, email: %s",
                compte.getNom(), compte.getPrenom(), compte.getTelephone(), compte.getEmail());

        if (request.nom() != null) {
            compte.setNom(request.nom().trim());
        }
        if (request.prenom() != null) {
            compte.setPrenom(request.prenom().trim());
        }
        compte.setDateNaissance(request.dateNaissance());
        if (request.telephone() != null) {
            compte.setTelephone(request.telephone().trim());
        }
        if (request.pays() != null && !request.pays().isBlank()) {
            compte.setPays(request.pays().trim());
        }

        Compte updated = compteRepository.save(compte);

        String nouvelleValeur = String.format("nom: %s, prenom: %s, telephone: %s, email: %s",
                updated.getNom(), updated.getPrenom(), updated.getTelephone(), updated.getEmail());

        // Audit de l'action selon le contexte
        logAuditForCompte(TypeActionAudit.MODIFICATION, "Compte", String.valueOf(updated.getId()),
                ancienneValeur, nouvelleValeur, updated, slugSalon, roleActif);

        return compteDTOResponseMapper.apply(updated);
    }

    @Transactional
    public void changerMotDePasse(
            String email,
            ChangementMotDePasseDTORequest request,
            String slugSalon,
            String roleActif) {

        Compte compte = findCompte(email);

        if (!passwordEncoder.matches(request.ancienMotDePasse(), compte.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe fourni est incorrect.");
        }

        if (passwordEncoder.matches(request.nouveauMotDePasse(), compte.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l'ancien mot de passe.");
        }

        compte.setPassword(passwordEncoder.encode(request.nouveauMotDePasse()));
        compte.setDoitChangerMotDePasse(false);
        compteRepository.save(compte);

        // Sécurité : Révocation des sessions Refresh Token après modification du mot de passe
        refreshTokenService.revokeAllByCompte(compte);

        logAuditForCompte(TypeActionAudit.CHANGEMENT_MDP, "Compte", String.valueOf(compte.getId()),
                "ancien_mot_de_passe", "nouveau_mot_de_passe_chiffre", compte, slugSalon, roleActif);

        log.info("Mot de passe mis à jour avec succès pour le compte {}", email);
    }

    @Transactional
    public Compte changerMotDePasseInitial(
            String identifiant,
            com.kadi_aon.mon_salon.account.dto.ChangementMotDePasseInitialDTORequest request) {

        Compte compte = findCompte(identifiant);

        if (!passwordEncoder.matches(request.ancienMotDePasse(), compte.getPassword())) {
            throw new IllegalArgumentException("Le mot de passe initial fourni est incorrect.");
        }

        if (passwordEncoder.matches(request.nouveauMotDePasse(), compte.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l'ancien mot de passe.");
        }

        compte.setPassword(passwordEncoder.encode(request.nouveauMotDePasse()));
        compte.setDoitChangerMotDePasse(false);
        compteRepository.save(compte);

        refreshTokenService.revokeAllByCompte(compte);

        logAuditForCompte(TypeActionAudit.CHANGEMENT_MDP, "Compte", String.valueOf(compte.getId()),
                "mot_de_passe_initial", "nouveau_mot_de_passe_personnalise", compte, null, null);

        log.info("Mot de passe initial personnalisé avec succès pour le compte {}", identifiant);
        return compte;
    }

    private void logAuditForCompte(
            TypeActionAudit action,
            String entite,
            String entiteId,
            String ancienneVal,
            String nouvelleVal,
            Compte compte,
            String slugSalon,
            String roleActif) {

        if (slugSalon != null && !slugSalon.isBlank() && compte != null) {
            Optional<AffectationSalon> affectation =
                    affectationSalonRepository.findByCompteIdAndSalonSlugAndStatutTrue(compte.getId(), slugSalon);
            if (affectation.isPresent()) {
                auditLogService.logActionSalon(action, entite, entiteId, ancienneVal, nouvelleVal,
                        affectation.get(), roleActif != null ? roleActif : "COIFFEUR");
                return;
            }
        }

        String rolePlateforme = (compte != null && compte.getRolePlateforme() != null)
                ? compte.getRolePlateforme().getRole().name() : (roleActif != null ? roleActif : "UTILISATEUR");

        auditLogService.logActionPlateforme(action, entite, entiteId, ancienneVal, nouvelleVal,
                compte, rolePlateforme);
    }
}
