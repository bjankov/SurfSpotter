package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Role;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String admin);
}