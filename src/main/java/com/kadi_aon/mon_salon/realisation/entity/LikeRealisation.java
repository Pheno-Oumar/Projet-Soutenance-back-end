package com.kadi_aon.mon_salon.realisation.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.salon.entity.AffectationSalon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "likes_realisation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_like_realisation_affectation",
                        columnNames = {"realisation_id", "affectation_salon_id"}
                )
        }
)
@Getter
@Setter
public class LikeRealisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "realisation_id", nullable = false)
    private Realisation realisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affectation_salon_id", nullable = false)
    private AffectationSalon affectationSalon;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    public LikeRealisation() {
        super();
    }

    public LikeRealisation(Long id, Realisation realisation, AffectationSalon affectationSalon, LocalDateTime dateCreation) {
        super();
        this.id = id;
        this.realisation = realisation;
        this.affectationSalon = affectationSalon;
        this.dateCreation = dateCreation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Realisation realisation;
        private AffectationSalon affectationSalon;
        private LocalDateTime dateCreation;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder realisation(Realisation realisation) {
            this.realisation = realisation;
            return this;
        }

        public Builder affectationSalon(AffectationSalon affectationSalon) {
            this.affectationSalon = affectationSalon;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public LikeRealisation build() {
            return new LikeRealisation(id, realisation, affectationSalon, dateCreation);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
    }
}
