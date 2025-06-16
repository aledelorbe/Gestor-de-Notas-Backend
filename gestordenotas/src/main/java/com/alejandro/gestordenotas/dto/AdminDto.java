package com.alejandro.gestordenotas.dto;

public class AdminDto extends UserDto {

    private boolean admin;

    public AdminDto(Long id, String username, boolean enabled, boolean admin) {
        super(id, username, enabled);
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
