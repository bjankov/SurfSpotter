package hr.algebra.surfspot.service;

import hr.algebra.surfspot.model.Role;

import java.util.Optional;

public interface RoleService extends BaseService<Role, Long> {
    Optional<Role> findByName(String targetRoleName);
}
