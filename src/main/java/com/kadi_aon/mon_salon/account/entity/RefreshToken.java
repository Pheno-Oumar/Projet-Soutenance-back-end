package com.kadi_aon.mon_salon.account.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_token", columnList = "token", unique = true)
})
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @Column(name = "date_expiration", nullable = false)
    private Instant dateExpiration;

    @Column(nullable = false)
    private Boolean revoked = false;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private Instant dateCreation;

    public RefreshToken() {
        super();
        this.revoked = false;
        this.dateCreation = Instant.now();
    }

    public RefreshToken(Long id, String token, Compte compte, Instant dateExpiration, Boolean revoked, Instant dateCreation) {
        super();
        this.id = id;
        this.token = token;
        this.compte = compte;
        this.dateExpiration = dateExpiration;
        this.revoked = revoked != null ? revoked : false;
        this.dateCreation = dateCreation != null ? dateCreation : Instant.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = Instant.now();
        }
        if (this.revoked == null) {
            this.revoked = false;
        }
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.dateExpiration);
    }

    public boolean isValid() {
        return !this.revoked && !isExpired();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String token;
        private Compte compte;
        private Instant dateExpiration;
        private Boolean revoked = false;
        private Instant dateCreation;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder compte(Compte compte) {
            this.compte = compte;
            return this;
        }

        public Builder dateExpiration(Instant dateExpiration) {
            this.dateExpiration = dateExpiration;
            return this;
        }

        public Builder revoked(Boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder dateCreation(Instant dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(id, token, compte, dateExpiration, revoked, dateCreation);
        }
    }
}
