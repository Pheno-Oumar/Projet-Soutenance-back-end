package com.kadi_aon.mon_salon.security.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.enums.TypeRolePlateforme;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("salonSecurity")
public class SalonSecurityService {

    private final SalonRepository salonRepository;
    private final AffectationSalonRepository affectationSalonRepository;
    private final CompteRepository compteRepository;

    public SalonSecurityService(
            SalonRepository salonRepository,
            AffectationSalonRepository affectationSalonRepository,
            CompteRepository compteRepository) {
        super();
        this.salonRepository = salonRepository;
        this.affectationSalonRepository = affectationSalonRepository;
        this.compteRepository = compteRepository;
    }

    @Transactional(readOnly = true)
    public boolean hasRoleInSalon(String slugSalon, String requiredRoleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return false;
        }

        // Vérification 1 : Si c'est l'administrateur système global, il a tous les droits
        boolean isAdminSysteme = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + TypeRolePlateforme.ADMIN_SYSTEME.name()));
        if (isAdminSysteme) {
            return true;
        }

        // Pipeline Étape 3 : Le salon existe-t-il et est-il actif ?
        Optional<Salon> salonOpt = salonRepository.findBySlug(slugSalon);
        if (salonOpt.isEmpty() || !Boolean.TRUE.equals(salonOpt.get().getStatut())) {
            log.warn("Salon introuvable ou inactif pour le slug : {}", slugSalon);
            return false;
        }

        String identifiant = auth.getName();
        Optional<Compte> compteOpt = findCompteByIdentifiant(identifiant);
        if (compteOpt.isEmpty() || Boolean.FALSE.equals(compteOpt.get().getStatut())) {
            log.warn("Compte inexistant ou inactif pour : {}", identifiant);
            return false;
        }

        Compte compte = compteOpt.get();

        // Si le rôle requis est CLIENT :
        // Tout utilisateur authentifié de la plateforme a le droit d'être client d'un salon actif,
        // à condition que son compte ne soit pas désactivé et qu'il ne soit pas banni par ce salon.
        if (TypeRoleSalon.CLIENT.name().equalsIgnoreCase(requiredRoleName)) {
            java.util.List<AffectationSalon> affList = affectationSalonRepository.findByCompteAndSalonOrderByStatutDescIdDesc(compte, salonOpt.get());
            if (!affList.isEmpty() && Boolean.FALSE.equals(affList.get(0).getStatut())) {
                log.warn("Accès client expressément désactivé par le salon {} pour l'utilisateur {}", slugSalon, identifiant);
                return false;
            }
            return true;
        }

        // Pipeline Étape 4 & 5 : Pour les rôles employés/membres, l'utilisateur a-t-il une affectation active sur ce salon ?
        Optional<AffectationSalon> affectationOpt =
                affectationSalonRepository.findByCompteIdAndSalonSlugAndStatutTrue(compte.getId(), slugSalon);

        if (affectationOpt.isEmpty()) {
            log.warn("Aucune affectation active trouvée pour l'utilisateur {} (ID: {}) sur le salon {}", identifiant, compte.getId(), slugSalon);
            return false;
        }

        // Pipeline Étape 4 : L'affectation contient-elle le rôle requis ? (Cumul fluide)
        try {
            TypeRoleSalon targetRole = TypeRoleSalon.valueOf(requiredRoleName.toUpperCase());
            boolean hasRole = affectationOpt.get().getRoles().stream()
                    .anyMatch(r -> r.getRole() == targetRole);

            if (!hasRole) {
                log.warn("Utilisateur {} n'a pas le rôle requis {} sur le salon {}", identifiant, requiredRoleName, slugSalon);
            }
            return hasRole;
        } catch (IllegalArgumentException e) {
            log.error("Rôle salon inconnu : {}", requiredRoleName);
            return false;
        }
    }

    private Optional<Compte> findCompteByIdentifiant(String identifiant) {
        if (identifiant == null || identifiant.isBlank()) {
            return Optional.empty();
        }
        String trimmed = identifiant.trim();
        Optional<Compte> opt = compteRepository.findByIdentifiant(trimmed);
        if (opt.isPresent()) return opt;

        String compact = trimmed.replaceAll("\\s+", "");
        opt = compteRepository.findByIdentifiant(compact);
        if (opt.isPresent()) return opt;

        if (compact.startsWith("+")) {
            opt = compteRepository.findByIdentifiant(compact.substring(1));
        } else {
            opt = compteRepository.findByIdentifiant("+" + compact);
        }
        return opt;
    }

    @Transactional(readOnly = true)
    public boolean isMemberOfSalon(String slugSalon) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return false;
        }

        Optional<Salon> salonOpt = salonRepository.findBySlug(slugSalon);
        if (salonOpt.isEmpty() || !Boolean.TRUE.equals(salonOpt.get().getStatut())) {
            return false;
        }

        Optional<Compte> compteOpt = findCompteByIdentifiant(auth.getName());
        if (compteOpt.isEmpty() || Boolean.FALSE.equals(compteOpt.get().getStatut())) {
            return false;
        }
        return affectationSalonRepository.findByCompteIdAndSalonSlugAndStatutTrue(compteOpt.get().getId(), slugSalon).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isClient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return false;
        }

        boolean isAdminSysteme = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + TypeRolePlateforme.ADMIN_SYSTEME.name()));
        if (isAdminSysteme) {
            return true;
        }

        String email = auth.getName();
        Optional<Compte> compteOpt = compteRepository.findByIdentifiant(email);
        return compteOpt.isPresent() && Boolean.TRUE.equals(compteOpt.get().getStatut());
    }
}

