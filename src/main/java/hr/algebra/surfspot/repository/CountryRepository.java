package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Country;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CountryRepository extends BaseRepository<Country> {
    private static final String FIND_BY_CODE_QUERY = "SELECT * FROM countries WHERE code = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM countries WHERE name = ?";

    public Optional<Country> findByCode(String code) {
        return findSingleResult(FIND_BY_CODE_QUERY, this::mapRowToCountry, code);
    }

    public Optional<Country> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, this::mapRowToCountry, name);
    }

    private Country mapRowToCountry(ResultSet resultSet) throws SQLException {
        return new Country(
                resultSet.getString("code"),
                resultSet.getString("name")
        );
    }
}
