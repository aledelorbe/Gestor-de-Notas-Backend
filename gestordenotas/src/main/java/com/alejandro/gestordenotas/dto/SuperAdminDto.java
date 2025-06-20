package com.alejandro.gestordenotas.dto;

// To create objects that can see the users with the super admin role
public class SuperAdminDto extends UserDto {

    private boolean enabled;

    private boolean admin;

    public SuperAdminDto(Long id, String username, boolean enabled, boolean admin) {
        super(id, username);
        this.enabled = enabled;
        this.admin = admin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}
