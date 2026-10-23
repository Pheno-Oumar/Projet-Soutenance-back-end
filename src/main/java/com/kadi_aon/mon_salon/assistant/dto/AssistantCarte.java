package com.kadi_aon.mon_salon.assistant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistantCarte {
    private String type;
    private String titre;
    private Object donnees;

    public AssistantCarte() {
        super();
    }

    public AssistantCarte(String type, String titre, Object donnees) {
        super();
        this.type = type;
        this.titre = titre;
        this.donnees = donnees;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type;
        private String titre;
        private Object donnees;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder titre(String titre) {
            this.titre = titre;
            return this;
        }

        public Builder donnees(Object donnees) {
            this.donnees = donnees;
            return this;
        }

        public AssistantCarte build() {
            return new AssistantCarte(type, titre, donnees);
        }
    }
}
