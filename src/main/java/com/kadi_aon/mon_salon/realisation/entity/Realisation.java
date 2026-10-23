package com.kadi_aon.mon_salon.realisation.entity;

import java.time.LocalDate;
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
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "realisation")
@Getter
@Setter
public class Realisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre de la réalisation est obligatoire.")
    @Column(nullable = false)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Column(name = "url_video")
    private String urlVideo; // Stocké en base, hébergé sur Cloudinary

    @Column(name = "date_realisation")
    private LocalDate dateRealisation;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @Column(name = "statut_publication", nullable = false)
    private boolean statutPublication = false;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "total_likes", nullable = false)
    private int totalLikes = 0;

    @Column(name = "total_commentaires", nullable = false)
    private int totalCommentaires = 0;

    @Column(name = "total_vues", nullable = false)
    private long totalVues = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coiffeur_id")
    private Compte coiffeur;

    public Realisation() {
        super();
        this.statutPublication = false;
        this.totalLikes = 0;
        this.totalCommentaires = 0;
        this.totalVues = 0;
    }

    public Realisation(
            Long id,
            String titre,
            String description,
            String urlVideo,
            LocalDate dateRealisation,
            LocalDateTime datePublication,
            boolean statutPublication,
            LocalDateTime dateCreation,
            LocalDateTime dateModification,
            int totalLikes,
            int totalCommentaires,
            long totalVues,
            Salon salon,
            Compte coiffeur) {
        super();
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.urlVideo = urlVideo;
        this.dateRealisation = dateRealisation;
        this.datePublication = datePublication;
        this.statutPublication = statutPublication;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.totalLikes = totalLikes;
        this.totalCommentaires = totalCommentaires;
        this.totalVues = totalVues;
        this.salon = salon;
        this.coiffeur = coiffeur;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String titre;
        private String description;
        private String urlVideo;
        private LocalDate dateRealisation;
        private LocalDateTime datePublication;
        private boolean statutPublication = false;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;
        private int totalLikes = 0;
        private int totalCommentaires = 0;
        private long totalVues = 0;
        private Salon salon;
        private Compte coiffeur;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder titre(String titre) {
            this.titre = titre;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder urlVideo(String urlVideo) {
            this.urlVideo = urlVideo;
            return this;
        }

        public Builder dateRealisation(LocalDate dateRealisation) {
            this.dateRealisation = dateRealisation;
            return this;
        }

        public Builder datePublication(LocalDateTime datePublication) {
            this.datePublication = datePublication;
            return this;
        }

        public Builder statutPublication(boolean statutPublication) {
            this.statutPublication = statutPublication;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public Builder dateModification(LocalDateTime dateModification) {
            this.dateModification = dateModification;
            return this;
        }

        public Builder totalLikes(int totalLikes) {
            this.totalLikes = totalLikes;
            return this;
        }

        public Builder totalCommentaires(int totalCommentaires) {
            this.totalCommentaires = totalCommentaires;
            return this;
        }

        public Builder totalVues(long totalVues) {
            this.totalVues = totalVues;
            return this;
        }

        public Builder salon(Salon salon) {
            this.salon = salon;
            return this;
        }

        public Builder coiffeur(Compte coiffeur) {
            this.coiffeur = coiffeur;
            return this;
        }

        public Realisation build() {
            return new Realisation(
                    id, titre, description, urlVideo, dateRealisation, datePublication, statutPublication,
                    dateCreation, dateModification, totalLikes, totalCommentaires, totalVues, salon, coiffeur);
        }
    }
}
