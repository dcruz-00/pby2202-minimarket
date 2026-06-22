package com.minimarket.dto;

import java.util.Set;

public class LoginResponseDTO {

    private String token;
    private String tipo;
    private String username;
    private Set<String> roles;

    public LoginResponseDTO(String token, String username, Set<String> roles) {
        this.token = token;
        this.tipo = "Bearer";
        this.username = username;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public String getUsername() { return username; }
    public Set<String> getRoles() { return roles; }
}