package hr.algebra.surfspot.model;

import hr.algebra.surfspot.context.RepositoryRegistry;
import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.repository.*;
import hr.algebra.surfspot.repository.sql.*;
import hr.algebra.surfspot.security.BCryptPasswordService;
import hr.algebra.surfspot.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;

public class TestingMain {
    private static final Logger log = LoggerFactory.getLogger(TestingMain.class);

    static void main() {
        DataSource dataSource = DataSourceSingleton.getInstance();
        RepositoryRegistry repositoryRegistry = new RepositoryRegistry(dataSource);

        UserRepository userRepository = repositoryRegistry.getRepository(UserRepository.class);
        AuthService authService = new AuthService(userRepository, new BCryptPasswordService());

        User adminUser = User.builder()
                .username("lolek")
                .passwordHash("admin")
                .build();
        try {
            authService.register(adminUser.getUsername(), adminUser.getPasswordHash());
            log.info("User {} registered successfully", adminUser.getUsername());
        } catch (RepositoryException e) {
            log.error("Greska prilikom registracije korisnika", e);
            // TODO: Mozda throw new ...
        }

        SurfingSchool school = SurfingSchool.builder()
                .name("Skola Surfanja")
                .build();

        Instructor instructor = Instructor.builder()
                .firstName("Pingvin")
                .lastName("BigZ")
                .build();

        CountryRepository countryRepository = repositoryRegistry.getRepository(CountryRepository.class);
        // TODO: Pravilno rijesi null vrijednosti
        Country croatia = countryRepository.findById("HR").orElse(null);

        Coast obala = Coast.builder()
                .name("Obala")
                .country(croatia)
                .build();

        CoastRepository coastRepository = repositoryRegistry.getRepository(CoastRepository.class);
        obala = coastRepository.save(obala);

        WaveDetails details = new WaveDetails(WaveType.BEACH_BREAK, 1.0);

        SurfSpot spot = SurfSpot.builder()
                .name("Malvarossa")
                .difficulty(DifficultyLevel.EASY)
                .windDirectionDegrees(45)
                .location(
                        new Location(
                            new Coordinates(
                                new BigDecimal("31.252354"), new BigDecimal("14.345235")),
                                obala))
                .waveDetails(details)
                .build();
        System.out.println(spot);

        SurfSpotRepository surfSpotRepository = repositoryRegistry.getRepository(SurfSpotRepository.class);
        surfSpotRepository.save(spot);
    }
}
