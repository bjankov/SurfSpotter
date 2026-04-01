package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.SurfingSchool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SurfingSchoolRepository extends BaseRepository<SurfingSchool> {
    public Optional<SurfingSchool> findById(Long id) {
        String query = "SELECT * FROM surfing_school WHERE id = ?";
        return findSingleResult(query, this::mapRowToSurfingSchool, id);
    }

    public Optional<SurfingSchool> findByName(String name) {
        String query = "SELECT * FROM surfing_school WHERE name = ?";
        return findSingleResult(query, this::mapRowToSurfingSchool, name);
    }

    private SurfingSchool mapRowToSurfingSchool(ResultSet resultSet) throws SQLException {
        return new SurfingSchool(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );

    }
}
