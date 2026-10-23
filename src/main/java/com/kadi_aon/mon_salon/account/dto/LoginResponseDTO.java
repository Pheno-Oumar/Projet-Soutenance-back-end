package com.kadi_aon.mon_salon.account.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private CompteSummaryDTO compte;
    private String slugSalon;
    private String nomSalon;
    private String logoUrl;
    private List<String> rolesSalon;
    private Boolean doitChangerMotDePasse;

    public LoginResponseDTO() {
        super();
        this.tokenType = "Bearer";
    }

    public LoginResponseDTO(
            String accessToken,
            String tokenType,
            Long expiresIn,
            CompteSummaryDTO compte,
            String slugSalon,
            String nomSalon,
            String logoUrl,
            List<String> rolesSalon,
            Boolean doitChangerMotDePasse) {
        super();
        this.accessToken = accessToken;
        this.tokenType = (tokenType != null) ? tokenType : "Bearer";
        this.expiresIn = expiresIn;
        this.compte = compte;
        this.slugSalon = slugSalon;
        this.nomSalon = nomSalon;
        this.logoUrl = logoUrl;
        this.rolesSalon = rolesSalon;
        this.doitChangerMotDePasse = doitChangerMotDePasse;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String accessToken;
        private String tokenType = "Bearer";
        private Long expiresIn;
        private CompteSummaryDTO compte;
        private String slugSalon;
        private String nomSalon;
        private String logoUrl;
        private List<String> rolesSalon;
        private Boolean doitChangerMotDePasse;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder compte(CompteSummaryDTO compte) {
            this.compte = compte;
            return this;
        }

        public Builder slugSalon(String slugSalon) {
            this.slugSalon = slugSalon;
            return this;
        }

        public Builder nomSalon(String nomSalon) {
            this.nomSalon = nomSalon;
            return this;
        }

        public Builder logoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        public Builder rolesSalon(List<String> rolesSalon) {
            this.rolesSalon = rolesSalon;
            return this;
        }

        public Builder doitChangerMotDePasse(Boolean doitChangerMotDePasse) {
            this.doitChangerMotDePasse = doitChangerMotDePasse;
            return this;
        }

        public LoginResponseDTO build() {
            return new LoginResponseDTO(accessToken, tokenType, expiresIn, compte, slugSalon, nomSalon, logoUrl, rolesSalon, doitChangerMotDePasse);
        }
    }
}
