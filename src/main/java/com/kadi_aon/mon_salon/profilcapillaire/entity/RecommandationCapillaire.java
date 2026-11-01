package com.kadi_aon.mon_salon.profilcapillaire.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.profilcapillaire.enums.CuirChevelu;
import com.kadi_aon.mon_salon.profilcapillaire.enums.EtatCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.PorositeCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TextureCheveux;
import com.kadi_aon.mon_salon.profilcapillaire.enums.TypeCheveux;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recommandations_capillaires")
@Getter
@Setter
public class RecommandationCapillaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Compte client;

    @Column(name = "date_recommandation", nullable = false)
    private LocalDateTime dateRecommandation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_cheveux", length = 50)
    private TypeCheveux typeCheveux;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TextureCheveux texture;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PorositeCheveux porosite;


    @Enumerated(EnumType.STRING)
    @Column(name = "cuir_chevelu", length = 50)
    private CuirChevelu cuirChevelu;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_cheveux", length = 50)
    private EtatCheveux etatCheveux;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_lavage_id")
    private ProduitConseilCapillaire produitLavage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_soin_profond_id")
    private ProduitConseilCapillaire produitSoinProfond;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_hydratation_id")
    private ProduitConseilCapillaire produitHydratation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_finition_id")
    private ProduitConseilCapillaire produitFinition;

    @Column(name = "score_lavage")
    private Integer scoreLavage;

    @Column(name = "score_soin_profond")
    private Integer scoreSoinProfond;

    @Column(name = "score_hydratation")
    private Integer scoreHydratation;

    @Column(name = "score_finition")
    private Integer scoreFinition;

    @Column(name = "score_global")
    private Integer scoreGlobal;


    public RecommandationCapillaire() {
        super();
    }

    public RecommandationCapillaire(
            Long id,
            String reference,
            Compte client,
            LocalDateTime dateRecommandation,
            TypeCheveux typeCheveux,
            TextureCheveux texture,
            PorositeCheveux porosite,
            CuirChevelu cuirChevelu,
            EtatCheveux etatCheveux,
            ProduitConseilCapillaire produitLavage,
            ProduitConseilCapillaire produitSoinProfond,
            ProduitConseilCapillaire produitHydratation,
            ProduitConseilCapillaire produitFinition,
            Integer scoreLavage,
            Integer scoreSoinProfond,
            Integer scoreHydratation,
            Integer scoreFinition,
            Integer scoreGlobal) {
        super();
        this.id = id;
        this.reference = reference;
        this.client = client;
        this.dateRecommandation = dateRecommandation;
        this.typeCheveux = typeCheveux;
        this.texture = texture;
        this.porosite = porosite;
        this.cuirChevelu = cuirChevelu;
        this.etatCheveux = etatCheveux;
        this.produitLavage = produitLavage;
        this.produitSoinProfond = produitSoinProfond;
        this.produitHydratation = produitHydratation;
        this.produitFinition = produitFinition;
        this.scoreLavage = scoreLavage;
        this.scoreSoinProfond = scoreSoinProfond;
        this.scoreHydratation = scoreHydratation;
        this.scoreFinition = scoreFinition;
        this.scoreGlobal = scoreGlobal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String reference;
        private Compte client;
        private LocalDateTime dateRecommandation;
        private TypeCheveux typeCheveux;
        private TextureCheveux texture;
        private PorositeCheveux porosite;
        private CuirChevelu cuirChevelu;
        private EtatCheveux etatCheveux;
        private ProduitConseilCapillaire produitLavage;
        private ProduitConseilCapillaire produitSoinProfond;
        private ProduitConseilCapillaire produitHydratation;
        private ProduitConseilCapillaire produitFinition;
        private Integer scoreLavage;
        private Integer scoreSoinProfond;
        private Integer scoreHydratation;
        private Integer scoreFinition;
        private Integer scoreGlobal;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder client(Compte client) {
            this.client = client;
            return this;
        }

        public Builder dateRecommandation(LocalDateTime dateRecommandation) {
            this.dateRecommandation = dateRecommandation;
            return this;
        }

        public Builder typeCheveux(TypeCheveux typeCheveux) {
            this.typeCheveux = typeCheveux;
            return this;
        }

        public Builder texture(TextureCheveux texture) {
            this.texture = texture;
            return this;
        }

        public Builder porosite(PorositeCheveux porosite) {
            this.porosite = porosite;
            return this;
        }


        public Builder cuirChevelu(CuirChevelu cuirChevelu) {
            this.cuirChevelu = cuirChevelu;
            return this;
        }

        public Builder etatCheveux(EtatCheveux etatCheveux) {
            this.etatCheveux = etatCheveux;
            return this;
        }

        public Builder produitLavage(ProduitConseilCapillaire produitLavage) {
            this.produitLavage = produitLavage;
            return this;
        }

        public Builder produitSoinProfond(ProduitConseilCapillaire produitSoinProfond) {
            this.produitSoinProfond = produitSoinProfond;
            return this;
        }

        public Builder produitHydratation(ProduitConseilCapillaire produitHydratation) {
            this.produitHydratation = produitHydratation;
            return this;
        }

        public Builder produitFinition(ProduitConseilCapillaire produitFinition) {
            this.produitFinition = produitFinition;
            return this;
        }

        public Builder scoreLavage(Integer scoreLavage) {
            this.scoreLavage = scoreLavage;
            return this;
        }

        public Builder scoreSoinProfond(Integer scoreSoinProfond) {
            this.scoreSoinProfond = scoreSoinProfond;
            return this;
        }

        public Builder scoreHydratation(Integer scoreHydratation) {
            this.scoreHydratation = scoreHydratation;
            return this;
        }

        public Builder scoreFinition(Integer scoreFinition) {
            this.scoreFinition = scoreFinition;
            return this;
        }

        public Builder scoreGlobal(Integer scoreGlobal) {
            this.scoreGlobal = scoreGlobal;
            return this;
        }


        public RecommandationCapillaire build() {
            return new RecommandationCapillaire(
                    id, reference, client, dateRecommandation, typeCheveux, texture, porosite,
                    cuirChevelu, etatCheveux, produitLavage, produitSoinProfond,
                    produitHydratation, produitFinition, scoreLavage, scoreSoinProfond, scoreHydratation,
                    scoreFinition, scoreGlobal);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateRecommandation == null) {
            this.dateRecommandation = LocalDateTime.now();
        }
    }
}
