package hr.algebra.surfspot.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class Role {
    private static final Logger log = LoggerFactory.getLogger(Role.class);

    private Long id;
    private String name;
    private Set<Permission> permissions;

    public static Builder builder() {
        return new Builder();
    }

    public Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Set<Permission> permissions;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder addPermission(Permission permission) {
            this.permissions.add(permission);
            return this;
        }

        public Builder permissions(Set<Permission> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder from(Role role) {
            this.id = role.id;
            this.name = role.name;
            this.permissions = role.permissions;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
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
