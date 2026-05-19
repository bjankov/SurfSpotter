package hr.algebra.surfspot.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {
    private final Long id;
    private String username;
    private String email;
    private final String passwordHash;
    private Set<Role> roles = new HashSet<>();
    private Set<Permission> permissions = new HashSet<>();

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.passwordHash = builder.passwordHash;
        this.email = builder.email;
        this.roles = builder.roles;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String passwordHash;
        private String email;
        private Set<Role> roles = new HashSet<>();
        private Set<Permission> permissions = new HashSet<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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

        public Builder addPermission(Permission permission) {
            if (permission == null) {
                return this;
            }
            if (this.permissions == null) {
                this.permissions = new HashSet<>();
            }
            this.permissions.add(permission);
            return this;
        }

        public Builder withPermissions(Set<Permission> permissions) {
            if (permissions != null) {
                this.permissions = new HashSet<>(permissions);
            }
            return this;
        }

        public Builder from(User user) {
            this.id = user.id;
            this.username = user.username;
            this.passwordHash = user.passwordHash;
            this.email = user.email;
            this.roles = new HashSet<>(user.roles);
            this.permissions = new HashSet<>(user.permissions);
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
    }

    public Boolean hasPermission(Permission permission) {
        return permissions != null && permissions.contains(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;

        if (id != null && user.id != null) {
            return Objects.equals(id, user.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}