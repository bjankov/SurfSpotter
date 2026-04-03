package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Country;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CountryRepository extends BaseRepository<Country> {
    public Optional<Country> findByCode(String code) {
        String query = "SELECT * FROM countries WHERE code = ?";
        return findSingleResult(query, this::mapRowToCountry, code);
    }

    public Optional<Country> findByName(String name) {
        String query = "SELECT * FROM countries WHERE name = ?";
        return findSingleResult(query, this::mapRowToCountry, name);
    }

    private Country mapRowToCountry(ResultSet resultSet) throws SQLException {
        return new Country(
                resultSet.getString("code"),
                resultSet.getString("name")
        );
    }
}
