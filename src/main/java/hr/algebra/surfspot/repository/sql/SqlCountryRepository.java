package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.repository.CountryRepository;
import hr.algebra.surfspot.repository.sql.mapper.RowMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class SqlCountryRepository extends BaseSqlRepository<Country> implements CountryRepository {
    private static final Logger log = LoggerFactory.getLogger(SqlCountryRepository.class);
    private final RowMapper<Country> countryMapper;

    public SqlCountryRepository(DataSource dataSource) {
        super(dataSource);
        countryMapper = RowMapperFactory.getInstance().getMapper(Country.class);
    }

    private static final String UPDATE_BY_ID_QUERY = "UPDATE countries SET name = ? WHERE code = ?";
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
        return findSingleResult(FIND_BY_ID_QUERY, countryMapper, code);
    }

    public Optional<Country> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY,countryMapper, name);
    }

    @Override
    public List<Country> findAll() {
        return findAll(FIND_ALL_QUERY, countryMapper);
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
    public Country update(Country country) {
        executeUpdate(UPDATE_BY_ID_QUERY,
                country.name(),
                country.code());
        return country;
    }

    @Override
    public void delete(String id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }
}
