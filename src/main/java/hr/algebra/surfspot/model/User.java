package hr.algebra.surfspot.model;

import java.util.Collections;
import java.util.Set;

public class User {
    public Long id;
    public String username;
    public String passwordHash;
    public Set<Role> roles;

    public User() {
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public User(Long id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Boolean hasPermission(String permissionName) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getName().equals(permissionName));
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }
}
