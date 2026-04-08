package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.repository.CoastRepository;
import hr.algebra.surfspot.repository.CountryRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class SqlCoastRepository extends BaseSqlRepository<Coast> implements CoastRepository {
    public SqlCoastRepository(DataSource dataSource) {
        super(dataSource);
    }

    private static final String SAVE_QUERY = "INSERT INTO coasts (name, country_code) VALUES (?, ?)";
    private static final String FIND_BY_ID_QUERY = """
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
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM coast WHERE name = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM coasts";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM coasts WHERE id = ?";

    public Coast save(final Coast coast) {
        Long generatedId = insertAndGetId(SAVE_QUERY, coast.getName(), coast.getCountry().code());

        return new Coast(
            generatedId, coast.getName(), coast.getCountry()
        );
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }

    public Optional<Coast> findById(final Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToCoast, id);
    }

    public Optional<Coast> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, this::mapRowToCoast, name);
    }

    @Override
    public List<Coast> findAll() {
        return findAll(FIND_ALL_QUERY, this::mapRowToCoast);
    }

    private Coast mapRowToCoast(ResultSet resultSet) throws SQLException {
        Country country = new Country(
                resultSet.getString("country_code"),
                resultSet.getString("country_name")
        );
        return  Coast.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .country(country)
                .build();
    }
}
