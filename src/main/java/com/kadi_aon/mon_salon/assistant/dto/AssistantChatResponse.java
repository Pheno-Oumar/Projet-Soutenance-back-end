package com.kadi_aon.mon_salon.assistant.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistantChatResponse {

    private String texte;
    private List<AssistantCarte> cartes = new ArrayList<>();
    private List<AssistantActionUi> actionsUi = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();
    private PendingActionDTO actionEnAttente;
    private String contexte;

    public AssistantChatResponse() {
        super();
        this.cartes = new ArrayList<>();
        this.actionsUi = new ArrayList<>();
        this.suggestions = new ArrayList<>();
    }

    public AssistantChatResponse(
            String texte,
            List<AssistantCarte> cartes,
            List<AssistantActionUi> actionsUi,
            List<String> suggestions,
            PendingActionDTO actionEnAttente,
            String contexte) {
        super();
        this.texte = texte;
        this.cartes = (cartes != null) ? cartes : new ArrayList<>();
        this.actionsUi = (actionsUi != null) ? actionsUi : new ArrayList<>();
        this.suggestions = (suggestions != null) ? suggestions : new ArrayList<>();
        this.actionEnAttente = actionEnAttente;
        this.contexte = contexte;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String texte;
        private List<AssistantCarte> cartes = new ArrayList<>();
        private List<AssistantActionUi> actionsUi = new ArrayList<>();
        private List<String> suggestions = new ArrayList<>();
        private PendingActionDTO actionEnAttente;
        private String contexte;

        public Builder texte(String texte) {
            this.texte = texte;
            return this;
        }

        public Builder cartes(List<AssistantCarte> cartes) {
            this.cartes = (cartes != null) ? cartes : new ArrayList<>();
            return this;
        }

        public Builder actionsUi(List<AssistantActionUi> actionsUi) {
            this.actionsUi = (actionsUi != null) ? actionsUi : new ArrayList<>();
            return this;
        }

        public Builder suggestions(List<String> suggestions) {
            this.suggestions = (suggestions != null) ? suggestions : new ArrayList<>();
            return this;
        }

        public Builder actionEnAttente(PendingActionDTO actionEnAttente) {
            this.actionEnAttente = actionEnAttente;
            return this;
        }

        public Builder contexte(String contexte) {
            this.contexte = contexte;
            return this;
        }

        public AssistantChatResponse build() {
            return new AssistantChatResponse(texte, cartes, actionsUi, suggestions, actionEnAttente, contexte);
        }
    }
}
