package com.alejandro.gestordenotas.services.dto;

public class UserDto {

    protected Long id;

    protected String username;

    protected boolean enabled;

    public UserDto(Long id, String username, boolean enabled) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
}
