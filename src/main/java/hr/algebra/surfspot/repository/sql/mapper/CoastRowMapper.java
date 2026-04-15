package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CoastRowMapper implements RowMapper<Coast> {

    @Override
    public Coast map(ResultSet resultSet) throws SQLException {
        Country country = new Country(
                resultSet.getString("country_code"),
                resultSet.getString("country_name")
        );

        return Coast.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .country(country)
                .build();
    }
}
