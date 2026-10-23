package com.kadi_aon.mon_salon.favori.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favori_variante_service", uniqueConstraints = {
        @UniqueConstraint(name = "uk_favori_variante_compte_variante", columnNames = {"client_id", "variante_id"})
})
@Getter
@Setter
public class FavoriVarianteService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Compte client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteService variante;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    public FavoriVarianteService() {
        super();
    }

    public FavoriVarianteService(Long id, Compte client, VarianteService variante, LocalDateTime dateAjout) {
        super();
        this.id = id;
        this.client = client;
        this.variante = variante;
        this.dateAjout = dateAjout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Compte client;
        private VarianteService variante;
        private LocalDateTime dateAjout;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder client(Compte client) {
            this.client = client;
            return this;
        }

        public Builder variante(VarianteService variante) {
            this.variante = variante;
            return this;
        }

        public Builder dateAjout(LocalDateTime dateAjout) {
            this.dateAjout = dateAjout;
            return this;
        }

        public FavoriVarianteService build() {
            return new FavoriVarianteService(id, client, variante, dateAjout);
        }
    }
}
