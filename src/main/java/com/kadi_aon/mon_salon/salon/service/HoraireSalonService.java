package com.kadi_aon.mon_salon.salon.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kadi_aon.mon_salon.audit.enums.TypeActionAudit;
import com.kadi_aon.mon_salon.audit.service.AuditLogService;
import com.kadi_aon.mon_salon.salon.dto.FermetureExceptionnelleDTORequest;
import com.kadi_aon.mon_salon.salon.dto.FermetureExceptionnelleDTOResponse;
import com.kadi_aon.mon_salon.salon.dto.HoraireOuvertureDTORequest;
import com.kadi_aon.mon_salon.salon.dto.HoraireOuvertureDTOResponse;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.FermetureExceptionnelle;
import com.kadi_aon.mon_salon.salon.entity.HoraireOuverture;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.salon.enums.JourSemaine;
import com.kadi_aon.mon_salon.salon.mapper.FermetureExceptionnelleDTOResponseMapper;
import com.kadi_aon.mon_salon.salon.mapper.HoraireOuvertureDTOResponseMapper;
import com.kadi_aon.mon_salon.salon.repository.AffectationSalonRepository;
import com.kadi_aon.mon_salon.salon.repository.FermetureExceptionnelleRepository;
import com.kadi_aon.mon_salon.salon.repository.HoraireOuvertureRepository;
import com.kadi_aon.mon_salon.salon.repository.SalonRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HoraireSalonService {

    private final SalonRepository salonRepository;
    private final HoraireOuvertureRepository horaireOuvertureRepository;
    private final FermetureExceptionnelleRepository fermetureExceptionnelleRepository;
    private final AffectationSalonRepository affectationSalonRepository;
    private final HoraireOuvertureDTOResponseMapper horaireMapper;
    private final FermetureExceptionnelleDTOResponseMapper fermetureMapper;
    private final AuditLogService auditLogService;

    public HoraireSalonService(
            SalonRepository salonRepository,
            HoraireOuvertureRepository horaireOuvertureRepository,
            FermetureExceptionnelleRepository fermetureExceptionnelleRepository,
            AffectationSalonRepository affectationSalonRepository,
            HoraireOuvertureDTOResponseMapper horaireMapper,
            FermetureExceptionnelleDTOResponseMapper fermetureMapper,
            AuditLogService auditLogService) {
        super();
        this.salonRepository = salonRepository;
        this.horaireOuvertureRepository = horaireOuvertureRepository;
        this.fermetureExceptionnelleRepository = fermetureExceptionnelleRepository;
        this.affectationSalonRepository = affectationSalonRepository;
        this.horaireMapper = horaireMapper;
        this.fermetureMapper = fermetureMapper;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void initialiserHorairesParDefaut(Salon salon) {
        if (salon == null || salon.getId() == null) {
            return;
        }
        LocalTime ouvertureStd = LocalTime.of(8, 30);
        LocalTime fermetureStd = LocalTime.of(18, 30);
        LocalTime pauseDebutStd = LocalTime.of(12, 30);
        LocalTime pauseFinStd = LocalTime.of(13, 30);

        List<HoraireOuverture> nouveauxHoraires = new ArrayList<>();
        for (JourSemaine jour : JourSemaine.values()) {
            if (!horaireOuvertureRepository.existsBySalonIdAndJourSemaine(salon.getId(), jour)) {
                boolean isDimanche = (jour == JourSemaine.DIMANCHE);
                nouveauxHoraires.add(HoraireOuverture.builder()
                        .salon(salon)
                        .jourSemaine(jour)
                        .heureOuverture(isDimanche ? LocalTime.of(9, 0) : ouvertureStd)
                        .heureFermeture(isDimanche ? LocalTime.of(14, 0) : fermetureStd)
                        .pauseDebut(isDimanche ? null : pauseDebutStd)
                        .pauseFin(isDimanche ? null : pauseFinStd)
                        .actif(!isDimanche)
                        .build());
            }
        }

        if (!nouveauxHoraires.isEmpty()) {
            horaireOuvertureRepository.saveAll(nouveauxHoraires);
            log.info("{} horaire(s) d'ouverture initialisé(s) par défaut pour le salon '{}' (id: {})",
                    nouveauxHoraires.size(), salon.getNom(), salon.getId());
        }
    }

    @Transactional
    public HoraireOuvertureDTOResponse definirHoraire(
            String slugSalon,
            HoraireOuvertureDTORequest request,
            String userEmail) {

        Salon salon = getSalonBySlug(slugSalon);
        AffectationSalon affectation = getAffectationActive(userEmail, slugSalon);

        validerHeures(request.heureOuverture(), request.heureFermeture(), request.pauseDebut(), request.pauseFin());

        HoraireOuverture horaire = horaireOuvertureRepository
                .findBySalonIdAndJourSemaine(salon.getId(), request.jourSemaine())
                .orElse(null);

        boolean isNew = (horaire == null);
        String ancienneValeur = null;

        if (isNew) {
            horaire = HoraireOuverture.builder()
                    .salon(salon)
                    .jourSemaine(request.jourSemaine())
                    .heureOuverture(request.heureOuverture())
                    .heureFermeture(request.heureFermeture())
                    .pauseDebut(request.pauseDebut())
                    .pauseFin(request.pauseFin())
                    .actif(request.actif() != null ? request.actif() : true)
                    .build();
        } else {
            ancienneValeur = String.format("%s: %s-%s (pause %s-%s, actif=%s)",
                    horaire.getJourSemaine(), horaire.getHeureOuverture(), horaire.getHeureFermeture(),
                    horaire.getPauseDebut(), horaire.getPauseFin(), horaire.getActif());

            horaire.setHeureOuverture(request.heureOuverture());
            horaire.setHeureFermeture(request.heureFermeture());
            horaire.setPauseDebut(request.pauseDebut());
            horaire.setPauseFin(request.pauseFin());
            if (request.actif() != null) {
                horaire.setActif(request.actif());
            }
        }

        HoraireOuverture saved = horaireOuvertureRepository.save(horaire);

        String nouvelleValeur = String.format("%s: %s-%s (pause %s-%s, actif=%s)",
                saved.getJourSemaine(), saved.getHeureOuverture(), saved.getHeureFermeture(),
                saved.getPauseDebut(), saved.getPauseFin(), saved.getActif());

        auditLogService.logActionSalon(
                isNew ? TypeActionAudit.CREATION : TypeActionAudit.MODIFICATION,
                "HoraireOuverture",
                String.valueOf(saved.getId()),
                ancienneValeur,
                nouvelleValeur,
                affectation,
                "PROPRIETAIRE"
        );

        return horaireMapper.apply(saved);
    }

    @Transactional
    public List<HoraireOuvertureDTOResponse> listerHoraires(String slugSalon) {
        Salon salon = getSalonBySlug(slugSalon);
        List<HoraireOuverture> horaires = horaireOuvertureRepository.findBySalonSlugOrderByJourSemaineAsc(slugSalon);
        if (horaires.isEmpty()) {
            initialiserHorairesParDefaut(salon);
            horaires = horaireOuvertureRepository.findBySalonSlugOrderByJourSemaineAsc(slugSalon);
        }
        return horaires.stream()
                .map(horaireMapper)
                .toList();
    }

    @Transactional
    public FermetureExceptionnelleDTOResponse ajouterFermeture(
            String slugSalon,
            FermetureExceptionnelleDTORequest request,
            String userEmail) {

        Salon salon = getSalonBySlug(slugSalon);
        AffectationSalon affectation = getAffectationActive(userEmail, slugSalon);

        LocalDate today = LocalDate.now();

        if (request.dateDebut().isBefore(today)) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé.");
        }

        if (request.dateDebut().isAfter(request.dateFin())) {
            throw new IllegalArgumentException("La date de début ne peut pas être postérieure à la date de fin.");
        }

        List<FermetureExceptionnelle> chevauchements = fermetureExceptionnelleRepository
                .findChevauchements(salon.getId(), request.dateDebut(), request.dateFin());

        if (!chevauchements.isEmpty()) {
            throw new IllegalArgumentException("Une fermeture exceptionnelle chevauche déjà cette période pour ce salon.");
        }

        FermetureExceptionnelle fermeture = FermetureExceptionnelle.builder()
                .salon(salon)
                .dateDebut(request.dateDebut())
                .dateFin(request.dateFin())
                .motif(request.motif())
                .build();

        FermetureExceptionnelle saved = fermetureExceptionnelleRepository.save(fermeture);

        auditLogService.logActionSalon(
                TypeActionAudit.CREATION,
                "FermetureExceptionnelle",
                String.valueOf(saved.getId()),
                null,
                String.format("Fermeture du %s au %s : %s", saved.getDateDebut(), saved.getDateFin(), saved.getMotif()),
                affectation,
                "PROPRIETAIRE"
        );

        return fermetureMapper.apply(saved);
    }

    @Transactional(readOnly = true)
    public List<FermetureExceptionnelleDTOResponse> listerFermetures(String slugSalon) {
        getSalonBySlug(slugSalon);
        return fermetureExceptionnelleRepository.findBySalonSlugOrderByDateDebutAsc(slugSalon)
                .stream()
                .map(fermetureMapper)
                .toList();
    }

    @Transactional
    public void supprimerFermeture(String slugSalon, Long fermetureId, String userEmail) {
        getSalonBySlug(slugSalon);
        AffectationSalon affectation = getAffectationActive(userEmail, slugSalon);

        FermetureExceptionnelle fermeture = fermetureExceptionnelleRepository.findById(fermetureId)
                .orElseThrow(() -> new EntityNotFoundException("Fermeture exceptionnelle introuvable avec l'identifiant : " + fermetureId));

        if (!fermeture.getSalon().getSlug().equals(slugSalon)) {
            throw new IllegalArgumentException("Cette fermeture exceptionnelle n'appartient pas au salon spécifié.");
        }

        LocalDate today = LocalDate.now();
        if (!fermeture.getDateDebut().isAfter(today)) {
            throw new IllegalStateException("Une fermeture exceptionnelle ne peut être supprimée qu'avant sa date de début.");
        }

        String ancienneValeur = String.format("Fermeture du %s au %s : %s",
                fermeture.getDateDebut(), fermeture.getDateFin(), fermeture.getMotif());

        fermetureExceptionnelleRepository.delete(fermeture);

        auditLogService.logActionSalon(
                TypeActionAudit.SUPPRESSION,
                "FermetureExceptionnelle",
                String.valueOf(fermetureId),
                ancienneValeur,
                null,
                affectation,
                "PROPRIETAIRE"
        );
    }

    @Transactional
    public FermetureExceptionnelleDTOResponse modifierFermeture(
            String slugSalon,
            Long fermetureId,
            FermetureExceptionnelleDTORequest request,
            String userEmail) {

        getSalonBySlug(slugSalon);
        AffectationSalon affectation = getAffectationActive(userEmail, slugSalon);

        FermetureExceptionnelle fermeture = fermetureExceptionnelleRepository.findById(fermetureId)
                .orElseThrow(() -> new EntityNotFoundException("Fermeture exceptionnelle introuvable avec l'identifiant : " + fermetureId));

        if (!fermeture.getSalon().getSlug().equals(slugSalon)) {
            throw new IllegalArgumentException("Cette fermeture exceptionnelle n'appartient pas au salon spécifié.");
        }

        LocalDate today = LocalDate.now();

        if (fermeture.getDateFin().isBefore(today)) {
            throw new IllegalStateException("Impossible de modifier une fermeture exceptionnelle passée.");
        }

        if (request.dateDebut().isAfter(request.dateFin())) {
            throw new IllegalArgumentException("La date de début ne peut pas être postérieure à la date de fin.");
        }

        boolean dejaCommencee = !fermeture.getDateDebut().isAfter(today);

        if (dejaCommencee) {
            if (!request.dateDebut().isEqual(fermeture.getDateDebut())) {
                throw new IllegalStateException("Impossible de modifier la date de début d'une fermeture déjà commencée.");
            }
            if (request.dateFin().isBefore(today)) {
                throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à aujourd'hui pour une fermeture en cours.");
            }
        } else {
            if (request.dateDebut().isBefore(today)) {
                throw new IllegalArgumentException("La date de début ne peut pas être dans le passé.");
            }
        }

        List<FermetureExceptionnelle> chevauchements = fermetureExceptionnelleRepository
                .findChevauchementsExcluant(fermeture.getSalon().getId(), fermeture.getId(), request.dateDebut(), request.dateFin());

        if (!chevauchements.isEmpty()) {
            throw new IllegalArgumentException("Une fermeture exceptionnelle chevauche déjà cette période pour ce salon.");
        }

        String ancienneValeur = String.format("du %s au %s : %s",
                fermeture.getDateDebut(), fermeture.getDateFin(), fermeture.getMotif());

        fermeture.setDateDebut(request.dateDebut());
        fermeture.setDateFin(request.dateFin());
        fermeture.setMotif(request.motif());

        FermetureExceptionnelle saved = fermetureExceptionnelleRepository.save(fermeture);

        String nouvelleValeur = String.format("du %s au %s : %s",
                saved.getDateDebut(), saved.getDateFin(), saved.getMotif());

        auditLogService.logActionSalon(
                TypeActionAudit.MODIFICATION,
                "FermetureExceptionnelle",
                String.valueOf(saved.getId()),
                ancienneValeur,
                nouvelleValeur,
                affectation,
                "PROPRIETAIRE"
        );

        return fermetureMapper.apply(saved);
    }

    @Transactional
    public FermetureExceptionnelleDTOResponse mettreFinFermeture(
            String slugSalon,
            Long fermetureId,
            String userEmail) {

        getSalonBySlug(slugSalon);
        AffectationSalon affectation = getAffectationActive(userEmail, slugSalon);

        FermetureExceptionnelle fermeture = fermetureExceptionnelleRepository.findById(fermetureId)
                .orElseThrow(() -> new EntityNotFoundException("Fermeture exceptionnelle introuvable avec l'identifiant : " + fermetureId));

        if (!fermeture.getSalon().getSlug().equals(slugSalon)) {
            throw new IllegalArgumentException("Cette fermeture exceptionnelle n'appartient pas au salon spécifié.");
        }

        LocalDate today = LocalDate.now();

        if (fermeture.getDateFin().isBefore(today)) {
            throw new IllegalStateException("Cette fermeture exceptionnelle est déjà terminée.");
        }

        if (fermeture.getDateDebut().isAfter(today)) {
            throw new IllegalStateException("Cette fermeture exceptionnelle n'a pas encore commencé. Vous pouvez la supprimer ou la modifier.");
        }

        LocalDate ancienneDateFin = fermeture.getDateFin();
        fermeture.setDateFin(today);

        FermetureExceptionnelle saved = fermetureExceptionnelleRepository.save(fermeture);

        auditLogService.logActionSalon(
                TypeActionAudit.MODIFICATION,
                "FermetureExceptionnelle",
                String.valueOf(saved.getId()),
                "dateFin: " + ancienneDateFin,
                "dateFin: " + today + " (mise fin anticipée)",
                affectation,
                "PROPRIETAIRE"
        );

        return fermetureMapper.apply(saved);
    }

    private Salon getSalonBySlug(String slugSalon) {
        return salonRepository.findBySlug(slugSalon)
                .orElseThrow(() -> new EntityNotFoundException("Salon introuvable avec le slug : " + slugSalon));
    }

    private AffectationSalon getAffectationActive(String email, String slugSalon) {
        return affectationSalonRepository.findByCompteEmailAndSalonSlugAndStatutTrue(email, slugSalon)
                .orElseThrow(() -> new IllegalArgumentException("Vous n'avez pas d'affectation active dans ce salon."));
    }

    private void validerHeures(LocalTime ouverture, LocalTime fermeture, LocalTime pauseDebut, LocalTime pauseFin) {
        if (!ouverture.isBefore(fermeture)) {
            throw new IllegalArgumentException("L'heure d'ouverture (" + ouverture + ") doit précéder l'heure de fermeture (" + fermeture + ").");
        }

        if (pauseDebut != null || pauseFin != null) {
            if (pauseDebut == null || pauseFin == null) {
                throw new IllegalArgumentException("Le début et la fin de pause doivent être définis conjointement.");
            }
            if (!pauseDebut.isBefore(pauseFin)) {
                throw new IllegalArgumentException("L'heure de début de pause (" + pauseDebut + ") doit précéder l'heure de fin de pause (" + pauseFin + ").");
            }
            if (!ouverture.isBefore(pauseDebut) || !pauseFin.isBefore(fermeture)) {
                throw new IllegalArgumentException("La pause (" + pauseDebut + "-" + pauseFin + ") doit être strictement comprise entre l'ouverture (" + ouverture + ") et la fermeture (" + fermeture + ").");
            }
        }
    }
}
