package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.repository.SurfingSchoolRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlSurfingSchoolRepository extends BaseSqlRepository<SurfingSchool> implements SurfingSchoolRepository {
    public SqlSurfingSchoolRepository(DataSource dataSource) {
        super(dataSource);
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM surfing_schools WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM surfing_schools WHERE name = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM surfing_schools";
    private static final String SAVE_QUERY = "INSERT INTO surfing_schools name VALUES ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM surfing_schools WHERE id = ?";

    public Optional<SurfingSchool> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToSurfingSchool, id);
    }

    public Optional<SurfingSchool> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, this::mapRowToSurfingSchool, name);
    }

    @Override
    public List<SurfingSchool> findAll() {
        return findAll(FIND_ALL_QUERY,  this::mapRowToSurfingSchool);
    }

    public SurfingSchool save(SurfingSchool surfingSchool) {
        Long generatedId = insertAndGetId(SAVE_QUERY, surfingSchool);
        return SurfingSchool.builder()
                .from(surfingSchool)
                .id(generatedId)
                .build();
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }

    private SurfingSchool mapRowToSurfingSchool(ResultSet resultSet) throws SQLException {
        return SurfingSchool.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
