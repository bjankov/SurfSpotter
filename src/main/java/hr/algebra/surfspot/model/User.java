package hr.algebra.surfspot.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private Set<Role> roles = new HashSet<>();

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.passwordHash = builder.passwordHash;
        if (builder.roles != null) {
            this.roles = builder.roles;
        }
    }

    public User() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String passwordHash;
        private Set<Role> roles;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder addRole(Role role) {
            if (role == null) {
                return this;
            }
            if (this.roles == null) {
                this.roles = new HashSet<>();
            }
            this.roles.add(role);
            return this;
        }

        public Builder withRoles(Set<Role> roles) {
            if (roles != null) {
                this.roles = new HashSet<>(roles);
            }
            return this;
        }

        public Builder from(User user) {
            this.id = user.id;
            this.username = user.username;
            this.passwordHash = user.passwordHash;
            this.roles = new HashSet<>(user.roles);
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Boolean hasPermission(String permissionName) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getName().equals(permissionName));
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void addRole(Role role) {
        if (role != null) {
            this.roles.add(role);
        }
    }
}