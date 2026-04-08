package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Instructor;

import java.util.Optional;

public interface InstructorRepository extends CrudRepository<Instructor, Long> {
    Optional<Instructor> findByName(String firstName);
    Optional<Instructor> findByLastName(String lastName);
}
