package com.kadi_aon.mon_salon.assistant.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistantActionUi {
    private String type;
    private Map<String, Object> payload;

    public AssistantActionUi() {
        super();
    }

    public AssistantActionUi(String type, Map<String, Object> payload) {
        super();
        this.type = type;
        this.payload = payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String type;
        private Map<String, Object> payload;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder payload(Map<String, Object> payload) {
            this.payload = payload;
            return this;
        }

        public AssistantActionUi build() {
            return new AssistantActionUi(type, payload);
        }
    }
}
