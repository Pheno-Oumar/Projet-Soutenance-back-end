package com.kadi_aon.mon_salon.favori.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.salon.entity.Salon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "favori_salon", uniqueConstraints = {
        @UniqueConstraint(name = "uk_favori_salon_compte_salon", columnNames = {"client_id", "salon_id"})
})
@Getter
@Setter
public class FavoriSalon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Compte client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    public FavoriSalon() {
        super();
    }

    public FavoriSalon(Long id, Compte client, Salon salon, LocalDateTime dateAjout) {
        super();
        this.id = id;
        this.client = client;
        this.salon = salon;
        this.dateAjout = dateAjout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Compte client;
        private Salon salon;
        private LocalDateTime dateAjout;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder client(Compte client) {
            this.client = client;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder dateAjout(LocalDateTime dateAjout) {
            this.dateAjout = dateAjout;
            return this;
        }

        public FavoriSalon build() {
            return new FavoriSalon(id, client, salon, dateAjout);
        }
    }
}
