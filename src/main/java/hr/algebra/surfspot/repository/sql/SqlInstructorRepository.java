package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.repository.InstructorRepository;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class SqlInstructorRepository extends BaseSqlRepository<Instructor> implements InstructorRepository {
    private final RowMapper<Instructor> instructorMapper;

    public SqlInstructorRepository(DataSource dataSource, RowMapper<Instructor> instructorMapper) {
        super(dataSource);
        this.instructorMapper = instructorMapper;
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM instructors WHERE id = ?";
    private static final String FIND_BY_FIRST_NAME_QUERY = "SELECT * FROM instructors WHERE first_name = ?";
    private static final String FIND_BY_LAST_NAME_QUERY = "SELECT * FROM instructors WHERE last_name = ?";
    private static final String FIND_ALL_QUERY = """
            SELECT
                i.id AS instructor_id,
                i.first_name,
                i.last_name,
                ss.id AS school_id,
                ss.name AS school_name
            FROM instructors i
            LEFT JOIN surfing_schools ss
            ON ss.id = i.surfing_school_id
            """;
    private static final String SAVE_QUERY = "INSERT INTO instructors (first_name, last_name, surfing_school_id) VALUES (?, ?, ?)";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM instructors WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE instructors SET first_name = ?, last_name = ?, surfing_school_id = ? WHERE id = ?";

    @Override
    public Optional<Instructor> findByLastName(String lastName) {
        return findSingleResult(FIND_BY_LAST_NAME_QUERY, instructorMapper, lastName);
    }

    @Override
    public Optional<Instructor> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, instructorMapper, id);
    }

    @Override
    public Optional<Instructor> findByName(String name) {
        return findSingleResult(FIND_BY_FIRST_NAME_QUERY, instructorMapper, name);
    }

    @Override
    public List<Instructor> findAll() {
        return findAll(FIND_ALL_QUERY, instructorMapper);
    }

    @Override
    public Instructor save(Instructor instructor) {
        Long schoolId = instructor.getSchool() != null ? instructor.getSchool().getId() : null;

        Long generatedId = insertAndGetId(
                SAVE_QUERY,
                instructor.getFirstName(),
                instructor.getLastName(),
                schoolId);
        requireGeneratedId(generatedId, "Could not save instructor");
        return Instructor.builder()
                .from(instructor)
                .id(generatedId)
                .build();
    }

    @Override
    public Instructor update(Instructor instructor) {
        int affectedRows = executeUpdate(
                UPDATE_QUERY,
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getSchool().getId(),
                instructor.getId()
        );
        requireAffectedRows(affectedRows, "Could not update instructor");
        return instructor;
    }

    @Override
    public void delete(Long id) {
        int affectedRows = executeUpdate(DELETE_BY_ID_QUERY, id);
        requireAffectedRows(affectedRows, "Could not delete instructor");
    }

}
