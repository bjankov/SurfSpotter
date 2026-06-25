package hr.algebra.surfspot.model;

import java.util.EnumSet;
import java.util.Set;

public enum Role {
    ADMIN(1L, EnumSet.allOf(Permission.class), "Admin"),
    USER(2L, EnumSet.of(
            Permission.MANAGE_COASTS,
            Permission.MANAGE_SCHOOLS,
            Permission.MANAGE_INSTRUCTORS,
            Permission.MANAGE_SPOTS
    ), "User");

    private final Long id;
    private final Set<Permission> permissions;
    private final String displayValue;

    Role(Long id, Set<Permission> permissions, String displayValue) {
        this.id = id;
        this.permissions = permissions;
        this.displayValue = displayValue;
    }

    public Long getId() {
        return this.id;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public String getDisplayValue() {
        return displayValue;
    }
}