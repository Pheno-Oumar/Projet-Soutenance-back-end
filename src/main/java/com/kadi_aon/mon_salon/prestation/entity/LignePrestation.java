package com.kadi_aon.mon_salon.prestation.entity;

import java.math.BigDecimal;

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
@Table(name = "lignes_prestation")
@Getter
@Setter
public class LignePrestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestation_id", nullable = false)
    private Prestation prestation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variante_service_id", nullable = false)
    private VarianteService varianteService;

    @Column(name = "prix_reel", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixReel;

    public LignePrestation() {
        super();
    }

    public LignePrestation(Long id, Prestation prestation, VarianteService varianteService, BigDecimal prixReel) {
        super();
        this.id = id;
        this.prestation = prestation;
        this.varianteService = varianteService;
        this.prixReel = prixReel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Prestation prestation;
        private VarianteService varianteService;
        private BigDecimal prixReel;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder prestation(Prestation prestation) {
            this.prestation = prestation;
            return this;
        }

        public Builder varianteService(VarianteService varianteService) {
            this.varianteService = varianteService;
            return this;
        }

        public Builder prixReel(BigDecimal prixReel) {
            this.prixReel = prixReel;
            return this;
        }

        public LignePrestation build() {
            return new LignePrestation(id, prestation, varianteService, prixReel);
        }
    }
}
