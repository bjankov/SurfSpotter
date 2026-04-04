package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Instructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class InstructorRepository extends BaseRepository<Instructor> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM instructors WHERE id = ?";
    private static final String FIND_BY_FIRST_NAME_QUERY = "SELECT * FROM instructors WHERE first_name = ?";
    private static final String FIND_BY_LAST_NAME_QUERY = "SELECT * FROM instructors WHERE last_name = ?";

    Optional<Instructor> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToInstructor, id);
    }

    Optional<Instructor> findByFirstName(String name){
        return findSingleResult(FIND_BY_FIRST_NAME_QUERY, this::mapRowToInstructor, name);
    }

    Optional<Instructor> findByLastName(String last_name){
        return findSingleResult(FIND_BY_LAST_NAME_QUERY, this::mapRowToInstructor, last_name);
    }

    private Instructor mapRowToInstructor(ResultSet resultSet) throws SQLException {
        return Instructor.builder()
                .id(resultSet.getLong("id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .build();
    }
}
