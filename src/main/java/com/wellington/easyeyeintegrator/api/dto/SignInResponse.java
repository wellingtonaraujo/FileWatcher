package com.wellington.easyeyeintegrator.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_at")
    private String expiresAt;

    private User user;
    private Integrator integrator;
    private Entity entity;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public User getUser() {
        return user;
    }

    public Integrator getIntegrator() {
        return integrator;
    }

    public Entity getEntity() {
        return entity;
    }

    // =========================
    // INNER CLASSES
    // =========================

    public static class User {

        public String id;

        @JsonProperty("entity_id")
        public String entityId;

        public String name;
        public String email;

        @JsonProperty("email_verified_at")
        public String emailVerifiedAt;

        public boolean active;

        @JsonProperty("created_at")
        public String createdAt;

        @JsonProperty("updated_at")
        public String updatedAt;

        @JsonProperty("deleted_at")
        public String deletedAt; // pode vir null
    }


    public static class Integrator {
        public String id;

        @JsonProperty("entity_user_integrator_id")
        public String entityUserIntegratorId;

        public String code;
        public String name;
        public String ip;
        public String mac;
        public boolean active;

        @JsonProperty("created_at")
        public String createdAt;

        @JsonProperty("updated_at")
        public String updatedAt;
    }

    public static class Entity {
        public String id;
        public String code;
        public String name;
        public String subdomain;

        public String zipcode;
        public String address;
        public String number;
        public String complement;
        public String district;
        public String city;
        public String state;
        public String country;

        @JsonProperty("national_registration")
        public String nationalRegistration;

        @JsonProperty("state_registration")
        public String stateRegistration;

        @JsonProperty("municipal_registration")
        public String municipalRegistration;

        public String telephone;
        public String cellphone;
        public String email;
        public String website;
        public String logo;

        @JsonProperty("is_client")
        public boolean isClient;

        public boolean active;

        @JsonProperty("created_at")
        public String createdAt;

        @JsonProperty("updated_at")
        public String updatedAt;

        @JsonProperty("deleted_at")
        public String deletedAt;
    }

}
