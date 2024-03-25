package com.philiance.otakulinks.jwt;

public class JwtResponse {
  
    private String username;

    public JwtResponse(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public JwtResponse setUsername(String username) {
        this.username = username;
        return this;
    }
}
