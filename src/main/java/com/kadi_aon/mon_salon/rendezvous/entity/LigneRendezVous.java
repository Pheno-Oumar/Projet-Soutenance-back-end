package com.kadi_aon.mon_salon.rendezvous.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.salon.entity.VarianteService;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lignes_rendez_vous")
@Getter
@Setter
public class LigneRendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rendez_vous_id", nullable = false)
    private RendezVous rendezVous;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variante_service_id", nullable = false)
    private VarianteService varianteService;

    @Column(name = "date_heure_debut", nullable = false)
    private LocalDateTime dateHeureDebut;

    @Column(name = "date_heure_fin", nullable = false)
    private LocalDateTime dateHeureFin;

    @Column(name = "duree_minutes", nullable = false)
    private Integer duree;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(nullable = false)
    private Integer ordre = 1;

    public LigneRendezVous() {
        super();
    }

    public LigneRendezVous(Long id, RendezVous rendezVous, VarianteService varianteService,
                          LocalDateTime dateHeureDebut, LocalDateTime dateHeureFin,
                          Integer duree, BigDecimal prix, Integer ordre) {
        super();
        this.id = id;
        this.rendezVous = rendezVous;
        this.varianteService = varianteService;
        this.dateHeureDebut = dateHeureDebut;
        this.dateHeureFin = dateHeureFin;
        this.duree = duree;
        this.prix = prix;
        this.ordre = ordre != null ? ordre : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private RendezVous rendezVous;
        private VarianteService varianteService;
        private LocalDateTime dateHeureDebut;
        private LocalDateTime dateHeureFin;
        private Integer duree;
        private BigDecimal prix;
        private Integer ordre = 1;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder rendezVous(RendezVous rendezVous) {
            this.rendezVous = rendezVous;
            return this;
        }

        public Builder varianteService(VarianteService varianteService) {
            this.varianteService = varianteService;
            return this;
        }

        public Builder dateHeureDebut(LocalDateTime dateHeureDebut) {
            this.dateHeureDebut = dateHeureDebut;
            return this;
        }

        public Builder dateHeureFin(LocalDateTime dateHeureFin) {
            this.dateHeureFin = dateHeureFin;
            return this;
        }

        public Builder duree(Integer duree) {
            this.duree = duree;
            return this;
        }

        public Builder prix(BigDecimal prix) {
            this.prix = prix;
            return this;
        }

        public Builder ordre(Integer ordre) {
            this.ordre = ordre;
            return this;
        }

        public LigneRendezVous build() {
            return new LigneRendezVous(id, rendezVous, varianteService, dateHeureDebut, dateHeureFin, duree, prix, ordre);
        }
    }
}
