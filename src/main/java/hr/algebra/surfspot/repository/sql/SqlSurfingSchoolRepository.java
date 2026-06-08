package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.repository.SurfingSchoolRepository;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class SqlSurfingSchoolRepository extends BaseSqlRepository<SurfingSchool> implements SurfingSchoolRepository {
    private final RowMapper<SurfingSchool> surfingSchoolMapper;

    public SqlSurfingSchoolRepository(DataSource dataSource, RowMapper<SurfingSchool> surfingSchoolMapper) {
        super(dataSource);
        this.surfingSchoolMapper = surfingSchoolMapper;
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM surfing_schools WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM surfing_schools WHERE name = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM surfing_schools";
    private static final String SAVE_QUERY = "INSERT INTO surfing_schools (name) VALUES (?)";
    private static final String UPDATE_BY_ID = "UPDATE surfing_schools SET name = ? WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM surfing_schools WHERE id = ?";

    public Optional<SurfingSchool> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, surfingSchoolMapper, id);
    }

    public Optional<SurfingSchool> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, surfingSchoolMapper, name);
    }

    @Override
    public List<SurfingSchool> findAll() {
        return findAll(FIND_ALL_QUERY, surfingSchoolMapper);
    }

    public SurfingSchool save(SurfingSchool surfingSchool) {
        Long generatedId = insertAndGetId(SAVE_QUERY, surfingSchool.getName());

        if (generatedId != null) {
            return SurfingSchool.builder()
                    .from(surfingSchool)
                    .id(generatedId)
                    .build();
        }
        throw new RepositoryException(String.format("Surfing school with id %d already exists", generatedId));
    }

    @Override
    public SurfingSchool update(SurfingSchool school) {
        executeUpdate(UPDATE_BY_ID,
                school.getName(),
                school.getId()
        );
        return school;
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }

}
