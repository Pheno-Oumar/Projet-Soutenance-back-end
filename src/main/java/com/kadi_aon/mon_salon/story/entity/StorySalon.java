package com.kadi_aon.mon_salon.story.entity;

import java.time.LocalDateTime;

import com.kadi_aon.mon_salon.account.entity.Compte;
import com.kadi_aon.mon_salon.salon.entity.Salon;
import com.kadi_aon.mon_salon.story.enums.TypeMediaStory;

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
@Table(name = "stories_salon")
@Getter
@Setter
public class StorySalon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Compte auteur;

    @Column(name = "media_url", nullable = false, length = 500)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_media", nullable = false, length = 20)
    private TypeMediaStory typeMedia;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(nullable = false)
    private boolean actif = true;

    public StorySalon() {
        super();
    }

    public StorySalon(Long id, Salon salon, Compte auteur, String mediaUrl, TypeMediaStory typeMedia,
                      LocalDateTime dateCreation, LocalDateTime dateExpiration, boolean actif) {
        super();
        this.id = id;
        this.salon = salon;
        this.auteur = auteur;
        this.mediaUrl = mediaUrl;
        this.typeMedia = typeMedia;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
        this.actif = actif;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Salon salon;
        private Compte auteur;
        private String mediaUrl;
        private TypeMediaStory typeMedia;
        private LocalDateTime dateCreation;
        private LocalDateTime dateExpiration;
        private boolean actif = true;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder auteur(Compte auteur) {
            this.auteur = auteur;
            return this;
        }

        public Builder mediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
            return this;
        }

        public Builder typeMedia(TypeMediaStory typeMedia) {
            this.typeMedia = typeMedia;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public Builder dateExpiration(LocalDateTime dateExpiration) {
            this.dateExpiration = dateExpiration;
            return this;
        }

        public Builder actif(boolean actif) {
            this.actif = actif;
            return this;
        }

        public StorySalon build() {
            return new StorySalon(id, salon, auteur, mediaUrl, typeMedia, dateCreation, dateExpiration, actif);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.dateExpiration == null) {
            this.dateExpiration = this.dateCreation.plusHours(24);
        }
    }
}
