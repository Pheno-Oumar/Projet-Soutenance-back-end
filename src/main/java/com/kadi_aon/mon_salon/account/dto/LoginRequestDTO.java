package com.kadi_aon.mon_salon.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    private String telephone;

    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    public LoginRequestDTO() {
        super();
    }

    public LoginRequestDTO(String telephone, String email, String password) {
        super();
        this.telephone = telephone;
        this.email = email;
        this.password = password;
    }

    public String getIdentifiant() {
        if (telephone != null && !telephone.isBlank()) {
            return telephone.trim();
        }
        if (email != null && !email.isBlank()) {
            return email.trim();
        }
        return "";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String telephone;
        private String email;
        private String password;

        public Builder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public LoginRequestDTO build() {
            return new LoginRequestDTO(telephone, email, password);
        }
    }
}
