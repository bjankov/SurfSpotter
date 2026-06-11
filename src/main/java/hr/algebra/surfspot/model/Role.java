package hr.algebra.surfspot.model;

import java.util.EnumSet;
import java.util.Set;

public enum Role {
    ADMIN(1L, EnumSet.allOf(Permission.class)),
    USER(2L, EnumSet.of(
            Permission.MANAGE_COASTS,
            Permission.MANAGE_SCHOOLS,
            Permission.MANAGE_INSTRUCTORS,
            Permission.MANAGE_SPOTS
    ));

    private final Long id;
    private final Set<Permission> permissions;

    Role(Long id, Set<Permission> permissions) {
        this.id = id;
        this.permissions = permissions;
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
}