package com.kadi_aon.mon_salon.salon.entity;

import java.time.LocalTime;

import com.kadi_aon.mon_salon.salon.enums.JourSemaine;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "horaires_ouverture", uniqueConstraints = {
        @UniqueConstraint(name = "uk_horaire_salon_jour", columnNames = {"salon_id", "jour_semaine"})
})
@Getter
@Setter
public class HoraireOuverture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine", nullable = false, length = 20)
    private JourSemaine jourSemaine;

    @Column(name = "heure_ouverture", nullable = false)
    private LocalTime heureOuverture;

    @Column(name = "heure_fermeture", nullable = false)
    private LocalTime heureFermeture;

    @Column(name = "pause_debut")
    private LocalTime pauseDebut;

    @Column(name = "pause_fin")
    private LocalTime pauseFin;

    @Column(nullable = false)
    private Boolean actif = true;

    public HoraireOuverture() {
        super();
    }

    public HoraireOuverture(Long id, Salon salon, JourSemaine jourSemaine, LocalTime heureOuverture,
                            LocalTime heureFermeture, LocalTime pauseDebut, LocalTime pauseFin, Boolean actif) {
        super();
        this.id = id;
        this.salon = salon;
        this.jourSemaine = jourSemaine;
        this.heureOuverture = heureOuverture;
        this.heureFermeture = heureFermeture;
        this.pauseDebut = pauseDebut;
        this.pauseFin = pauseFin;
        this.actif = actif != null ? actif : true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Salon salon;
        private JourSemaine jourSemaine;
        private LocalTime heureOuverture;
        private LocalTime heureFermeture;
        private LocalTime pauseDebut;
        private LocalTime pauseFin;
        private Boolean actif = true;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder jourSemaine(JourSemaine jourSemaine) {
            this.jourSemaine = jourSemaine;
            return this;
        }

        public Builder heureOuverture(LocalTime heureOuverture) {
            this.heureOuverture = heureOuverture;
            return this;
        }

        public Builder heureFermeture(LocalTime heureFermeture) {
            this.heureFermeture = heureFermeture;
            return this;
        }

        public Builder pauseDebut(LocalTime pauseDebut) {
            this.pauseDebut = pauseDebut;
            return this;
        }

        public Builder pauseFin(LocalTime pauseFin) {
            this.pauseFin = pauseFin;
            return this;
        }

        public Builder actif(Boolean actif) {
            this.actif = actif;
            return this;
        }

        public HoraireOuverture build() {
            return new HoraireOuverture(id, salon, jourSemaine, heureOuverture, heureFermeture, pauseDebut, pauseFin, actif);
        }
    }
}
