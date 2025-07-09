package com.alejandro.gestordenotas.dto;

// To create objects that can see the users with the admin role
public class AdminDto extends UserDto {

    private boolean enabled;

    public AdminDto(Long id, String username, boolean enabled) {
        super(id, username);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
