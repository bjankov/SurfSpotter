package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryRowMapper implements RowMapper<Country> {

    @Override
    public Country map(ResultSet resultSet) throws SQLException {
        return new Country(
                resultSet.getString("code"),
                resultSet.getString("name")
        );
    }
}
