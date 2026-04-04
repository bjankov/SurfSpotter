package hr.algebra.surfspot.model;

import hr.algebra.surfspot.repository.CoastRepository;
import hr.algebra.surfspot.repository.CountryRepository;
import hr.algebra.surfspot.repository.SurfSpotRepository;

import java.math.BigDecimal;

public class TestingMain {
    static void main(String[] args) {
        // System.out.println(WindDirection.fromDegrees(Integer.parseInt(args[0])));

        SurfingSchool school = SurfingSchool.builder()
                .name("Skola Surfanja")
                .build();

        Instructor instructor = Instructor.builder()
                .firstName("Pingvin")
                .lastName("BigZ")
                .build();

        CountryRepository countryRepository = new CountryRepository();

        Country croatia = countryRepository.findByCode("HR").orElse(null);

        Coast obala = Coast.builder()
                .name("Obala")
                .country(croatia)
                .build();

        CoastRepository coastRepository = new CoastRepository();
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

        SurfSpotRepository surfSpotRepository =  new SurfSpotRepository();
        surfSpotRepository.save(spot);
    }
}
