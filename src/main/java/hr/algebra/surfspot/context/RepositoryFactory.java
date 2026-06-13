package hr.algebra.surfspot.context;

import hr.algebra.surfspot.repository.*;
import hr.algebra.surfspot.repository.sql.*;
import hr.algebra.surfspot.repository.sql.mapper.*;

import javax.sql.DataSource;

public class RepositoryFactory {
    private final UserRepository userRepository;
    private final SurfSpotRepository surfSpotRepository;
    private final InstructorRepository instructorRepository;
    private final SurfingSchoolRepository surfingSchoolRepository;
    private final CoastRepository coastRepository;
    private final CountryRepository countryRepository;

    public RepositoryFactory(DataSource dataSource) {
        this.userRepository = new SqlUserRepository(
                dataSource,
                new UserRowMapper()
        );
        this.surfSpotRepository = new SqlSurfSpotRepository(
                dataSource,
                new SurfSpotRowMapper(),
                new InstructorRowMapper()
        );
        this.instructorRepository = new SqlInstructorRepository(
                dataSource,
                new InstructorRowMapper()
        );
        this.surfingSchoolRepository = new SqlSurfingSchoolRepository(
                dataSource,
                new SurfingSchoolRowMapper(),
                new SurfSpotRowMapper()
        );
        this.coastRepository = new SqlCoastRepository(
                dataSource,
                new CoastRowMapper()
        );
        this.countryRepository = new SqlCountryRepository(
                dataSource,
                new CountryRowMapper()
        );
    }

    public UserRepository getUserRepository() { return userRepository; }
    public SurfSpotRepository getSurfSpotRepository() { return surfSpotRepository; }
    public InstructorRepository getInstructorRepository() { return instructorRepository; }
    public SurfingSchoolRepository getSurfingSchoolRepository() { return surfingSchoolRepository; }
    public CoastRepository getCoastRepository() { return coastRepository; }
    public CountryRepository getCountryRepository() { return countryRepository; }
}