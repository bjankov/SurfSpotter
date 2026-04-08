package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.repository.CountryRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlCountryRepository extends BaseSqlRepository<Country> implements CountryRepository {
    public SqlCountryRepository(DataSource dataSource) {
        super(dataSource);
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM countries WHERE code = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM countries WHERE name = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM countries WHERE id = ?";

    @Override
    public Optional<Country> findById(String id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToCountry, id);
    }

    @Override
    public Optional<Country> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, this::mapRowToCountry, name);
    }

    @Override
    public List<Country> findAll() {
        return List.of();
    }

    @Override
    public Country save(Country country) {
        return null;
    }

    @Override
    public void delete(String id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }

    private Country mapRowToCountry(ResultSet resultSet) throws SQLException {
        return new Country(
                resultSet.getString("code"),
                resultSet.getString("name")
        );
    }
}
