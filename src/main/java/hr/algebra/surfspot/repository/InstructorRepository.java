package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Instructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class InstructorRepository extends BaseRepository<Instructor> {
    Optional<Instructor> findById(Long id) {
        String query = "SELECT * FROM instructors WHERE id = ?";
        return findSingleResult(query, this::mapRowToInstructor, id);
    }

    Optional<Instructor> findByFirstName(String name){
        String query = "SELECT * FROM coasts WHERE first_name = ?";
        return findSingleResult(query, this::mapRowToInstructor, name);
    }

    Optional<Instructor> findByLastName(String last_name){
        String query = "SELECT * FROM coasts WHERE last_name = ?";
        return findSingleResult(query, this::mapRowToInstructor, last_name);
    }

    private Instructor mapRowToInstructor(ResultSet resultSet) throws SQLException {
        return Instructor.builder()
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .build();
    }
}
