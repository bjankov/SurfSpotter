package hr.algebra.surfspot.context;

import hr.algebra.surfspot.exception.PersistenceException;
import hr.algebra.surfspot.repository.*;
import hr.algebra.surfspot.repository.sql.*;
import hr.algebra.surfspot.repository.sql.mapper.*;

import javax.sql.DataSource;
import java.util.Map;

public class RepositoryRegistry {
    private final Map<Class<?>, Object> registry;

    public RepositoryRegistry(DataSource dataSource) {

        this.registry = Map.of(
                SurfSpotRepository.class,
                new SqlSurfSpotRepository(
                        dataSource,
                        new SurfSpotRowMapper(),
                        new InstructorRowMapper()
                ),
                UserRepository.class,
                new SqlUserRepository(
                        dataSource,
                        new UserRowMapper()
                ),
                CoastRepository.class,
                new SqlCoastRepository(
                        dataSource,
                        new CoastRowMapper()
                ),
                CountryRepository.class,
                new SqlCountryRepository(
                        dataSource,
                        new CountryRowMapper()
                ),
                InstructorRepository.class,
                new SqlInstructorRepository(
                        dataSource,
                        new InstructorRowMapper()
                ),
                SurfingSchoolRepository.class,
                new SqlSurfingSchoolRepository(
                        dataSource,
                        new SurfingSchoolRowMapper()
                ));
    }

    public <T> T getRepository(Class<T> repositoryInterface) {
        @SuppressWarnings("unchecked")
        T repository = (T) registry.get(repositoryInterface);
        if (repository == null) {
            throw new PersistenceException("No repository found for interface " + repositoryInterface.getName());
        }
        return repository;
    }
}