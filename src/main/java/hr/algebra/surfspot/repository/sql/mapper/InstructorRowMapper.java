package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstructorRowMapper implements RowMapper<Instructor> {
    @Override
    public Instructor map(ResultSet resultSet) throws SQLException {
        SurfingSchool school = new SurfingSchool(
                resultSet.getLong("school_id"),
                resultSet.getString("school_name")
        );

        return Instructor.builder()
                .id(resultSet.getLong("instructor_id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .school(school)
                .build();
    }
}
