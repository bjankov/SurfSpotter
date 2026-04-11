package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    void deleteByUsername(String username);
    void deleteByEmail(String email);
}
