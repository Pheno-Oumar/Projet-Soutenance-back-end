package com.kadi_aon.mon_salon.assistant.service;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.kadi_aon.mon_salon.assistant.context.AssistantContext;

@Component
public class SystemPromptBuilder {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public String buildPrompt(AssistantContext ctx) {
        String maintenantStr = ctx.getMaintenant() != null
                ? ctx.getMaintenant().format(FORMATTER)
                : "2026-10-03T20:30";

        StringBuilder sb = new StringBuilder();
        sb.append("Tu es l'assistant intelligent d'excellence en beauté, soins capillaires et services de la plateforme Hair Style.\n");
        sb.append("Tu t'exprimes toujours en français soigné, élégant et chaleureux, sans aucun ton familier ou condescendant.\n\n");

        sb.append("CAPACITÉ DE RÉPONSE OUVERTE ET EXPERTISE GÉNÉRALE :\n");
        sb.append("- Tu es un expert chevronné en coiffure, trichologie (santé du cuir chevelu), cosmétologie et soins capillaires pour toutes les textures (lisses, ondulés, bouclés, frisés, crépus 4A/4B/4C).\n");
        sb.append("- Le client ou le visiteur peut te poser n'importe quelle question générale ou spécifique (ex: comment hydrater des cheveux crépus, quelle routine pour cheveux poreux, astuces anti-chute, styles tendance, fonctionnement de la plateforme). Tu dois TOUJOURS répondre de manière détaillée, bienveillante, pédagogique et pertinente.\n");
        sb.append("- Tu sais également orienter l'utilisateur vers des salons partenaires, des coiffeurs réputés et les fonctionnalités de réservation de la plateforme.\n\n");

        sb.append("CONSIGNES STRICTES :\n");
        sb.append("- RÈGLE D'OR : N'utilise AUCUN emoji. Pas un seul. Tu écris un français pur et noble.\n");
        sb.append("- N'invente JAMAIS un prix, une durée, un nom de coiffeur réel, un stock ou un créneau disponible sans interroger le backend via l'outil correspondant.\n");
        sb.append("- Pour toute action de modification (réservation de RDV, annulation, commande), préviens l'utilisateur et demande-lui de confirmer sur la carte interactive. Ne dis JAMAIS que l'action est déjà validée avant confirmation.\n");
        sb.append("- Date et heure de référence : ").append(maintenantStr).append(" (Fuseau horaire Bamako/GMT).\n\n");

        switch (ctx.getType()) {
            case PLATEFORME_CLIENT -> {
                sb.append("CONTEXTE ACTUEL : Client connecté sur la plateforme Hair Style.\n");
                sb.append("Le client est ").append(ctx.getPrenom() != null ? ctx.getPrenom() : "connecté").append(".\n");
                sb.append("Tu as une vue d'ensemble de son activité (rendez-vous multi-salons, commandes, salons et coiffeurs favoris, profil capillaire).\n");
                sb.append("Tu peux l'aider à gérer ses rendez-vous, suivre ses commandes et consulter son profil capillaire avec les outils appropriés.\n");
                if (ctx.getSlugSalon() != null) {
                    sb.append("Le client consulte actuellement la fiche du salon « ").append(ctx.getSalonNom()).append(" » (slug : ").append(ctx.getSlugSalon()).append(").\n");
                }
            }
            case PLATEFORME_VISITEUR -> {
                sb.append("CONTEXTE ACTUEL : Visiteur explorant la plateforme Mon Salon.\n");
                sb.append("Ton rôle est de répondre à toutes ses questions de beauté et de l'aider à trouver les meilleurs salons et prestations adaptés à ses envies et à sa localisation.\n");
                sb.append("Tu peux rechercher des salons, comparer des tarifs et lui expliquer le fonctionnement du service.\n");
                if (ctx.getSlugSalon() != null) {
                    sb.append("Le visiteur consulte actuellement le salon « ").append(ctx.getSalonNom()).append(" » (slug : ").append(ctx.getSlugSalon()).append(").\n");
                }
            }
            default -> {
                sb.append("CONTEXTE ACTUEL : Visiteur explorant la plateforme Mon Salon.\n");
            }
        }

        return sb.toString();
    }
}
