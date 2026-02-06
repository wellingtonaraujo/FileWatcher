package com.wellington.easyeyeintegrator.api.dto;

public class SignInRequest {

    private String email;
    private String password;
    private String code;

    public SignInRequest(String email, String password, String code) {
        this.email = email;
        this.password = password;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCode() {
        return code;
    }
}
