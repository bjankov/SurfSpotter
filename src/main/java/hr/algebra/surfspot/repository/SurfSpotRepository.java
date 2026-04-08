package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.*;

import java.util.List;
import java.util.Set;

public interface SurfSpotRepository extends CrudRepository<SurfSpot, Long> {
    boolean existsByName(String name);
    boolean existsById(String code);
    long countByCountryCode(String countryCode);
    long countByDifficultyLevel(DifficultyLevel difficultyLevel);
    long  countByWaveType(WaveType waveType);
    long countByCoast(Coast coast);
    List<SurfSpot> findByCountryName(String countryName);
    List<SurfSpot> findByDifficulty(DifficultyLevel difficultyLevel);
    List<SurfSpot> findByInstructor(Instructor instructor);
    List<SurfSpot> findByWaveType(WaveType waveType);
    List<SurfSpot> findByMonthInBestSeason(Month month);
    List<SurfSpot> findByMonthsInBestSeason(Set<Month> months);
    List<SurfSpot> findByWindDirection(WindDirection windDirection);
    List<SurfSpot> findBySchool(SurfingSchool surfingSchool);
    List<SurfSpot> findByCoast(Coast coast);
}
