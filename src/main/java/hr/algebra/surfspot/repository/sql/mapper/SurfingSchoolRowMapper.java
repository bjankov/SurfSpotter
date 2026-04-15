package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SurfingSchoolRowMapper implements RowMapper<SurfingSchool> {
    @Override
    public SurfingSchool map(ResultSet resultSet) throws SQLException {
        return SurfingSchool.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
