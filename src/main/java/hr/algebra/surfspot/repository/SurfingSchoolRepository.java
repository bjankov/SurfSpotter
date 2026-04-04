package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.SurfingSchool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SurfingSchoolRepository extends BaseRepository<SurfingSchool> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM surfing_schools WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM surfing_schools WHERE name = ?";

    public Optional<SurfingSchool> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToSurfingSchool, id);
    }

    public Optional<SurfingSchool> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, this::mapRowToSurfingSchool, name);
    }

    private SurfingSchool mapRowToSurfingSchool(ResultSet resultSet) throws SQLException {
        return SurfingSchool.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
