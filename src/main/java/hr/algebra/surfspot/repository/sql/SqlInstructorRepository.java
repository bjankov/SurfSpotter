package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.repository.InstructorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlInstructorRepository extends BaseSqlRepository<Instructor> implements InstructorRepository {
    private static final Logger log = LoggerFactory.getLogger(SqlCountryRepository.class);

    public SqlInstructorRepository(DataSource dataSource) {
        super(dataSource);
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM instructors WHERE id = ?";
    private static final String FIND_BY_FIRST_NAME_QUERY = "SELECT * FROM instructors WHERE first_name = ?";
    private static final String FIND_BY_LAST_NAME_QUERY = "SELECT * FROM instructors WHERE last_name = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM instructors";
    private static final String SAVE_QUERY = "INSERT INTO instructors (first_name, last_name) VALUES (?, ?)";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM instructors WHERE id = ?";

    @Override
    public Optional<Instructor> findByLastName(String lastName) {
        return findSingleResult(FIND_BY_LAST_NAME_QUERY, this::mapRowToInstructor, lastName);
    }

    @Override
    public Optional<Instructor> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToInstructor, id);
    }

    @Override
    public Optional<Instructor> findByName(String name) {
        return findSingleResult(FIND_BY_FIRST_NAME_QUERY, this::mapRowToInstructor, name);
    }

    @Override
    public List<Instructor> findAll() {
        return findAll(FIND_ALL_QUERY, this::mapRowToInstructor);
    }

    @Override
    public Instructor save(Instructor instructor) {
        Long generatedId = insertAndGetId(SAVE_QUERY, instructor);

        if  (generatedId != null) {
            return Instructor.builder()
                    .from(instructor)
                    .id(generatedId)
                    .build();
        }
        throw new RepositoryException("Could not save instructor");
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }

    private Instructor mapRowToInstructor(ResultSet resultSet) throws SQLException {
        return Instructor.builder()
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name)"))
                .build();
    }
}
