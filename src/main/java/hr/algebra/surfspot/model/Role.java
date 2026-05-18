package hr.algebra.surfspot.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Role {
    private final Long id;
    private final String name;
    private final Set<Permission> permissions;

    public static Builder builder() {
        return new Builder();
    }

    private Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.permissions = builder.permissions != null ? new HashSet<>(builder.permissions) : new HashSet<>();
    }

    public static class Builder {
        private Long id;
        private String name;
        private Set<Permission> permissions = new HashSet<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder addPermission(Permission permission) {
            if (permission != null) {
                if (this.permissions == null) {
                    this.permissions = new HashSet<>();
                }
                this.permissions.add(permission);
            }
            return this;
        }

        public Builder permissions(Set<Permission> permissions) {
            if (permissions != null) {
                this.permissions = new HashSet<>(permissions);
            }
            return this;
        }

        public Builder from(Role role) {
            this.id = role.id;
            this.name = role.name;
            this.permissions = new HashSet<>(role.permissions);
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
