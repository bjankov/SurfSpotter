package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Country;

import java.sql.*;
import java.util.Optional;

public class CoastRepository extends BaseRepository<Coast> {
    public Coast save(final Coast coast) {
        String query = "INSERT INTO coasts (name, country_code) VALUES (?, ?)";
        Long generatedId = insertAndGetId(query, coast.getName(), coast.getCountry().code());

        return new Coast(
            generatedId, coast.getName(), coast.getCountry()
        );
    }

    public Optional<Coast> findById(final Long id) {
        String query = """
                SELECT
                    coasts.id AS coast_id,
                    coasts.name AS coast_name,
                    countries.code AS country_code,
                    countries.name AS country_name,
                FROM coast
                WHERE id = ?
                JOIN countries
                ON  coasts.country_code = countries.code
                """;
        return findSingleResult(query, this::mapRowToCoast, id);
    }

    public Optional<Coast> findByName(String name) {
        String query = "SELECT * FROM coast WHERE name = ?";
        return findSingleResult(query, this::mapRowToCoast, name);
    }

    private Coast mapRowToCoast(ResultSet resultSet) throws SQLException {
        Country country = new Country(
                resultSet.getString("country_code"),
                resultSet.getString("country_name")
        );
        return new Coast(
                resultSet.getLong("coast_id"),
                resultSet.getString("name"),
                country
        );
    }
}
