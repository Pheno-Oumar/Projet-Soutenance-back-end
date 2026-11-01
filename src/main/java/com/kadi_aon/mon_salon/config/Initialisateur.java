package com.kadi_aon.mon_salon.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.account.entity.RolePlateforme;
import com.kadi_aon.mon_salon.account.enums.TypeRolePlateforme;
import com.kadi_aon.mon_salon.account.repository.CompteRepository;
import com.kadi_aon.mon_salon.account.repository.RolePlateformeRepository;
import com.kadi_aon.mon_salon.salon.entity.RoleSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;
import com.kadi_aon.mon_salon.salon.repository.RoleSalonRepository;
import com.kadi_aon.mon_salon.salon.service.HoraireSalonService;

// import java.util.List;
// import com.kadi_aon.mon_salon.profilcapillaire.entity.ProduitConseilCapillaire;
// import com.kadi_aon.mon_salon.profilcapillaire.enums.EtapeRoutine;
// import com.kadi_aon.mon_salon.profilcapillaire.repository.ProduitConseilCapillaireRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Initialisateur implements CommandLineRunner {

    private final RolePlateformeRepository rolePlateformeRepository;
    private final RoleSalonRepository roleSalonRepository;
    private final CompteRepository compteRepository;
    private final com.kadi_aon.mon_salon.salon.repository.SalonRepository salonRepository;
    // private final ProduitConseilCapillaireRepository produitConseilRepository;
    private final PasswordEncoder passwordEncoder;
    private final HoraireSalonService horaireSalonService;

    public Initialisateur(
            RolePlateformeRepository rolePlateformeRepository,
            RoleSalonRepository roleSalonRepository,
            CompteRepository compteRepository,
            com.kadi_aon.mon_salon.salon.repository.SalonRepository salonRepository,
            PasswordEncoder passwordEncoder,
            HoraireSalonService horaireSalonService) {
        super();
        this.rolePlateformeRepository = rolePlateformeRepository;
        this.roleSalonRepository = roleSalonRepository;
        this.compteRepository = compteRepository;
        this.salonRepository = salonRepository;
        this.passwordEncoder = passwordEncoder;
        this.horaireSalonService = horaireSalonService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initRoles();
        initAdminSysteme();
        initSalonsLogos();
        initHorairesSalons();
        // initProduitsConseils();
    }

    private void initRoles() {
        // Initialisation des rôles plateforme
        for (TypeRolePlateforme typeRole : TypeRolePlateforme.values()) {
            if (!rolePlateformeRepository.existsByRole(typeRole)) {
                RolePlateforme rolePlateforme = new RolePlateforme(typeRole);
                rolePlateformeRepository.save(rolePlateforme);
                log.info("Rôle plateforme initialisé : {}", typeRole);
            }
        }

        // Initialisation des rôles salon
        for (TypeRoleSalon typeRole : TypeRoleSalon.values()) {
            if (!roleSalonRepository.existsByRole(typeRole)) {
                RoleSalon roleSalon = new RoleSalon(typeRole);
                roleSalonRepository.save(roleSalon);
                log.info("Rôle salon initialisé : {}", typeRole);
            }
        }
    }

    private void initAdminSysteme() {
        String email = "amadou14112004@gmail.com";

        if (!compteRepository.existsByEmail(email)) {
            RolePlateforme roleAdmin = rolePlateformeRepository.findByRole(TypeRolePlateforme.ADMIN_SYSTEME)
                    .orElseThrow(() -> new IllegalStateException("Rôle ADMIN_SYSTEME non trouvé"));

            Compte admin = Compte.builder()
                    .email(email)
                    .nom("AON")
                    .prenom("Amadou")
                    .telephone("+22376662725")
                    .pays("Mali")
                    .password(passwordEncoder.encode("123456"))
                    .statut(true)
                    .rolePlateforme(roleAdmin)
                    .build();

            compteRepository.save(admin);
            log.info("Compte administrateur système créé avec succès (email: {})", email);
        } else {
            log.info("Le compte administrateur système existe déjà.");
        }
    }

    private void initSalonsLogos() {
        var salons = salonRepository.findAll();
        String[] defaultLogos = {
                "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=500&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=500&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=500&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?w=500&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1562322140-8baeececf3df?w=500&auto=format&fit=crop&q=80"
        };
        String[] defaultCovers = {
                "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?w=1000&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=1000&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=1000&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1562322140-8baeececf3df?w=1000&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=1000&auto=format&fit=crop&q=80"
        };

        int idx = 0;
        for (var s : salons) {
            boolean updated = false;
            if (s.getLogoUrl() == null || s.getLogoUrl().trim().isBlank()) {
                String assignedLogo = defaultLogos[idx % defaultLogos.length];
                s.setLogoUrl(assignedLogo);
                updated = true;
                log.info("Logo automatique assigné au salon '{}' : {}", s.getNom(), assignedLogo);
            }
            if (s.getCoverUrl() == null || s.getCoverUrl().trim().isBlank()) {
                String assignedCover = defaultCovers[idx % defaultCovers.length];
                s.setCoverUrl(assignedCover);
                updated = true;
                log.info("Cover automatique assignée au salon '{}' : {}", s.getNom(), assignedCover);
            }
            if (s.getLatitude() == null || s.getLongitude() == null) {
                double[][] dakarCoords = {
                    {14.6937, -17.4441},
                    {14.7167, -17.4677},
                    {14.7200, -17.4500},
                    {14.7450, -17.5150},
                    {14.7600, -17.3700}
                };
                double[] coords = dakarCoords[idx % dakarCoords.length];
                s.setCoordonnees(coords[0], coords[1]);
                updated = true;
                log.info("Coordonnées automatiques assignées au salon '{}' : {}, {}", s.getNom(), coords[0], coords[1]);
            }
            if (updated) {
                salonRepository.save(s);
            }
            idx++;
        }
    }

    private void initHorairesSalons() {
        for (Salon s : salonRepository.findAll()) {
            horaireSalonService.initialiserHorairesParDefaut(s);
        }
    }

    // private void initProduitsConseils() {
    // if (produitConseilRepository.count() == 0) {
    // List<ProduitConseilCapillaire> produits = List.of(
    // ProduitConseilCapillaire.builder()
    // .nom("Shampoing Doux Hydratant à l'Aloe Vera")
    // .marque("Kérastase")
    // .description("Nettoie délicatement le cuir chevelu sans altérer le film
    // lipidique protecteur.")
    // .modeUtilisation("Appliquer sur cuir chevelu mouillé, masser délicatement du
    // bout des doigts, rincer à l'eau tiède.")
    // .imageUrl("https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=500&auto=format&fit=crop&q=80")
    // .etapeRoutine(EtapeRoutine.LAVAGE)
    // .actif(true)
    // .build(),
    // ProduitConseilCapillaire.builder()
    // .nom("Masque Réparateur au Beurre de Karité")
    // .marque("Shea Moisture")
    // .description("Pénètre en profondeur pour nourrir la fibre capillaire et
    // restaurer l'élasticité naturelle.")
    // .modeUtilisation("Appliquer généreusement mèche par mèche. Laisser poser 20 à
    // 30 minutes sous bonnet chaud, puis rincer.")
    // .imageUrl("https://images.unsplash.com/photo-1608248597359-2696b996e386?w=500&auto=format&fit=crop&q=80")
    // .etapeRoutine(EtapeRoutine.SOIN_PROFOND)
    // .actif(true)
    // .build(),
    // ProduitConseilCapillaire.builder()
    // .nom("Crème Hydratante Sans Rinçage (Leave-in)")
    // .marque("Cantu")
    // .description("Gorge la cuticule d'eau et facilite le coiffage tout au long de
    // la semaine.")
    // .modeUtilisation("Répartir uniformément sur cheveux humides avant le
    // coiffage. Ne pas rincer.")
    // .imageUrl("https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=500&auto=format&fit=crop&q=80")
    // .etapeRoutine(EtapeRoutine.HYDRATATION)
    // .actif(true)
    // .build(),
    // ProduitConseilCapillaire.builder()
    // .nom("Sérum Scellant Protecteur Argan & Jojoba")
    // .marque("Moroccanoil")
    // .description("Scelle l'hydratation et crée un bouclier protecteur anti-casse
    // et anti-frisottis.")
    // .modeUtilisation("Chauffer 3 à 4 gouttes au creux des mains et lisser sur les
    // pointes et longueurs.")
    // .imageUrl("https://images.unsplash.com/photo-1608248597379-91307bdfa67b?w=500&auto=format&fit=crop&q=80")
    // .etapeRoutine(EtapeRoutine.FINITION)
    // .actif(true)
    // .build()
    // );
    // produitConseilRepository.saveAll(produits);
    // log.info("Catalogue de base des produits conseils capillaires initialisé ({}
    // produits)", produits.size());
    // }
    // }
}
