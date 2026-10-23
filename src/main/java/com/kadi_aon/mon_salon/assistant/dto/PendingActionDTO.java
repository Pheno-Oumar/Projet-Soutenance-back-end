package com.kadi_aon.mon_salon.assistant.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingActionDTO {
    private String actionId;
    private String outil;
    private String resume;
    private LocalDateTime expireA;
    private Map<String, Object> donnees;

    public PendingActionDTO() {
        super();
    }

    public PendingActionDTO(String actionId, String outil, String resume, LocalDateTime expireA, Map<String, Object> donnees) {
        super();
        this.actionId = actionId;
        this.outil = outil;
        this.resume = resume;
        this.expireA = expireA;
        this.donnees = donnees;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String actionId;
        private String outil;
        private String resume;
        private LocalDateTime expireA;
        private Map<String, Object> donnees;

        public Builder actionId(String actionId) {
            this.actionId = actionId;
            return this;
        }

        public Builder outil(String outil) {
            this.outil = outil;
            return this;
        }

        public Builder resume(String resume) {
            this.resume = resume;
            return this;
        }

        public Builder expireA(LocalDateTime expireA) {
            this.expireA = expireA;
            return this;
        }

        public Builder donnees(Map<String, Object> donnees) {
            this.donnees = donnees;
            return this;
        }

        public PendingActionDTO build() {
            return new PendingActionDTO(actionId, outil, resume, expireA, donnees);
        }
    }
}
