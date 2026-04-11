package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlCountryRepository extends BaseSqlRepository<Country> implements CountryRepository {
    private static final Logger log = LoggerFactory.getLogger(SqlCountryRepository.class);
    public SqlCountryRepository(DataSource dataSource) {
        super(dataSource);
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM countries WHERE code = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM countries WHERE name = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM countries";
    private static final String SAVE_QUERY = """
            INSERT INTO countries (code, name)
            VALUES(?, ?)
            ON CONFLICT(code)
            DO UPDATE SET name = EXCLUDED.name
            """;
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM countries WHERE  = ?";

    @Override
    public Optional<Country> findById(String code) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToCountry, code);
    }

    @Override
    public Optional<Country> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, this::mapRowToCountry, name);
    }

    @Override
    public List<Country> findAll() {
        return findAll(FIND_ALL_QUERY, this::mapRowToCountry);
    }

    @Override
    public Country save(Country country) {
        int affectedRows = executeUpdate(SAVE_QUERY, country.code(), country.name());
        if (affectedRows > 0) {
            return country;
        }
        throw new RepositoryException("Could not save country");
    }

    @Override
    public void delete(String id) {
        int affectedRows = executeUpdate(DELETE_BY_ID_QUERY, id);
        if (affectedRows > 0) {

        }
    }

    private Country mapRowToCountry(ResultSet resultSet) throws SQLException {
        return new Country(
                resultSet.getString("code"),
                resultSet.getString("name")
        );
    }
}
