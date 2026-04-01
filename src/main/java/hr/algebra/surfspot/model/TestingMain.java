package hr.algebra.surfspot.model;

import hr.algebra.surfspot.repository.CountryRepository;
import hr.algebra.surfspot.repository.SurfSpotRepository;

import java.math.BigDecimal;

public class TestingMain {
    static void main(String[] args) {
        // System.out.println(WindDirection.fromDegrees(Integer.parseInt(args[0])));

        SurfingSchool school = new SurfingSchool("Surfing School");
        Instructor instructor = new Instructor("Ivo", "Ivic", school);

        System.out.println(instructor);
        CountryRepository countryRepository = new CountryRepository();

        Country croatia = countryRepository.findByCode("HR").orElse(null);
        System.out.println(croatia);

        SurfSpot spot = new SurfSpot.Builder()
                .id(1L)
                .name("Pen Gu")
                .difficulty(DifficultyLevel.EXPERT)
                .windDirectionDegrees(270)
                .location(
                        new Location(
                            new Coordinates(
                                new BigDecimal("31.252354"), new BigDecimal("14.345235")),
                                new Coast("Obala", croatia)))
                .build();
        System.out.println(spot);

        SurfSpotRepository surfSpotRepository =  new SurfSpotRepository();
        surfSpotRepository.save(spot);
    }
}
