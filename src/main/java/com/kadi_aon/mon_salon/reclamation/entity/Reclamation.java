package com.kadi_aon.mon_salon.reclamation.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.reclamation.enums.StatutReclamation;
import com.kadi_aon.mon_salon.reclamation.enums.TypeReclamation;
import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;
import com.kadi_aon.mon_salon.salon.entity.Salon;

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
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reclamation")
@Getter
@Setter
public class Reclamation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affectation_client_id", nullable = false)
    private AffectationSalon affectationClient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @NotBlank(message = "L'objet de la réclamation est obligatoire.")
    @Column(nullable = false, length = 255)
    private String objet;

    @NotBlank(message = "La description de la réclamation est obligatoire.")
    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeReclamation type = TypeReclamation.AUTRE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutReclamation statut = StatutReclamation.EN_ATTENTE;

    @Column(name = "reponse_traitement", length = 2000)
    private String reponseTraitement;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traite_par_affectation_id")
    private AffectationSalon traitePar;

    public Reclamation() {
        super();
    }

    public Reclamation(Long id, AffectationSalon affectationClient, Salon salon, String objet,
                       String description, TypeReclamation type, StatutReclamation statut,
                       String reponseTraitement, LocalDateTime dateCreation, LocalDateTime dateTraitement,
                       AffectationSalon traitePar) {
        super();
        this.id = id;
        this.affectationClient = affectationClient;
        this.salon = salon;
        this.objet = objet;
        this.description = description;
        this.type = type != null ? type : TypeReclamation.AUTRE;
        this.statut = statut != null ? statut : StatutReclamation.EN_ATTENTE;
        this.reponseTraitement = reponseTraitement;
        this.dateCreation = dateCreation;
        this.dateTraitement = dateTraitement;
        this.traitePar = traitePar;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private AffectationSalon affectationClient;
        private Salon salon;
        private String objet;
        private String description;
        private TypeReclamation type = TypeReclamation.AUTRE;
        private StatutReclamation statut = StatutReclamation.EN_ATTENTE;
        private String reponseTraitement;
        private LocalDateTime dateCreation;
        private LocalDateTime dateTraitement;
        private AffectationSalon traitePar;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder affectationClient(AffectationSalon affectationClient) {
            this.affectationClient = affectationClient;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder objet(String objet) {
            this.objet = objet;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(TypeReclamation type) {
            this.type = type;
            return this;
        }

        public Builder statut(StatutReclamation statut) {
            this.statut = statut;
            return this;
        }

        public Builder reponseTraitement(String reponseTraitement) {
            this.reponseTraitement = reponseTraitement;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public Builder dateTraitement(LocalDateTime dateTraitement) {
            this.dateTraitement = dateTraitement;
            return this;
        }

        public Builder traitePar(AffectationSalon traitePar) {
            this.traitePar = traitePar;
            return this;
        }

        public Reclamation build() {
            return new Reclamation(id, affectationClient, salon, objet, description, type,
                    statut, reponseTraitement, dateCreation, dateTraitement, traitePar);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutReclamation.EN_ATTENTE;
        }
        if (this.type == null) {
            this.type = TypeReclamation.AUTRE;
        }
    }
}
