package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Permission;
import hr.algebra.surfspot.model.User;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String usernameOrEmail);
    void deleteByUsername(String username);
    void deleteByEmail(String email);
    public Set<Permission> findPermissionsByUserId(Long userId);
}
