package com.kadi_aon.mon_salon.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistantChatRequest {

    @NotBlank(message = "Le message utilisateur est obligatoire")
    private String message;

    private String sessionId;

    private String slugSalon;

    private String pageCourante;

    private Long ressourceCouranteId;

    private Double latitude;

    private Double longitude;

    public void setPrompt(String prompt) {
        if (this.message == null || this.message.isBlank()) {
            this.message = prompt;
        }
    }

    public AssistantChatRequest() {
        super();
    }

    public AssistantChatRequest(
            String message,
            String sessionId,
            String slugSalon,
            String pageCourante,
            Long ressourceCouranteId,
            Double latitude,
            Double longitude) {
        super();
        this.message = message;
        this.sessionId = sessionId;
        this.slugSalon = slugSalon;
        this.pageCourante = pageCourante;
        this.ressourceCouranteId = ressourceCouranteId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String message;
        private String sessionId;
        private String slugSalon;
        private String pageCourante;
        private Long ressourceCouranteId;
        private Double latitude;
        private Double longitude;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder slugSalon(String slugSalon) {
            this.slugSalon = slugSalon;
            return this;
        }

        public Builder pageCourante(String pageCourante) {
            this.pageCourante = pageCourante;
            return this;
        }

        public Builder ressourceCouranteId(Long ressourceCouranteId) {
            this.ressourceCouranteId = ressourceCouranteId;
            return this;
        }

        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public AssistantChatRequest build() {
            return new AssistantChatRequest(message, sessionId, slugSalon, pageCourante, ressourceCouranteId, latitude, longitude);
        }
    }
}
