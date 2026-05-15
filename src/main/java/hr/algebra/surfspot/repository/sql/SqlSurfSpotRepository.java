package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.repository.SurfSpotRepository;
import hr.algebra.surfspot.repository.sql.mapper.RowMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;

public class SqlSurfSpotRepository extends BaseSqlRepository<SurfSpot> implements SurfSpotRepository {
    private static final Logger log =  LoggerFactory.getLogger(SqlSurfSpotRepository.class);
    private final RowMapper<SurfSpot> surfSpotMapper;

    public SqlSurfSpotRepository(DataSource dataSource) {
        super(dataSource);
        surfSpotMapper = RowMapperFactory.getInstance().getMapper(SurfSpot.class);
    }

    private static final String UPDATE_BY_ID_QUERY = """
            UPDATE surf_spots
            SET name = ?,
                latitude = ?,
                longitude = ?,
                country_code = ?,
                coast_id = ?,
                wave_type = ?,
                wave_height = ?,
                difficulty = ?,
                wind_direction = ?
            WHERE id = ?;""";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM surf_spots WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM surf_spots WHERE name = ?";
    private static final String FIND_BY_COAST_NAME_QUERY = """
        SELECT *
        FROM surf_spots
        JOIN coasts ON coasts.id = surf_spots.coast_id
        WHERE coast_name = ?
        """;
    private static final String FIND_ALL_QUERY = """
    SELECT
        ss.id,
        ss.name AS surf_spot_name,
        ss.latitude,
        ss.longitude,
        ss.wave_type,
        ss.wave_height,
        ss.difficulty,
        ss.wind_direction,
        coasts.id AS coast_id,
        coasts.name AS coast_name,
        countries.code AS country_code,
        countries.name AS country_name
    FROM surf_spots ss
    JOIN coasts  ON ss.coast_id = coasts.id
    JOIN countries ON coasts.country_code = countries.code
    """;
    private static final String SAVE_SURF_SPOT_QUERY = """
        INSERT INTO surf_spots (
            name,
            latitude,
            longitude,
            country_code,
            coast_id,
            wave_type,
            wave_height,
            difficulty,
            wind_direction
            )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
     """;
    private static final String INSERT_MONTHS_QUERY = "INSERT INTO surf_spot_months (surf_spot_id, month_name) VALUES (?, ?)";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM surf_spots WHERE id = ?";
    private static final String COUNT_BY_COUNTRY_CODE_QUERY =  "SELECT COUNT(*) FROM surf_spots WHERE country_code = ?";
    private static final String COUNT_BY_DIFFICULTY_QUERY = "SELECT COUNT(*) FROM surf_spots WHERE difficulty = ?";
    private static final String COUNT_BY_WAVE_TYPE_QUERY = "SELECT COUNT(*) FROM surf_spots WHERE wave_type = ?";

    public Optional<SurfSpot> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, surfSpotMapper, id);
    }

    public Optional<SurfSpot> findByName(final String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, surfSpotMapper, name);
    }

    @Override
    public List<SurfSpot> findAll() {
        return findAll(FIND_ALL_QUERY, surfSpotMapper);
    }

    
    public SurfSpot save(SurfSpot spot) {

        Long generatedId = insertAndGetId(
                SAVE_SURF_SPOT_QUERY,
                spot.getName(),
                spot.getLocation().getCoordinates().latitude(),
                spot.getLocation().getCoordinates().longitude(),
                spot.getLocation().getCoast().getCountry().code(),
                spot.getLocation().getCoast().getId(),
                spot.getWaveDetails().getWaveType().name(),
                spot.getWaveDetails().getWaveHeight(),
                spot.getDifficulty().name(),
                spot.getWindDirectionDegrees()
        );

        if (generatedId != null) {
            saveMonths(generatedId, spot.getBestSeason());

            return SurfSpot.builder()
                    .id(generatedId)
                    .name(spot.getName())
                    .location(spot.getLocation())
                    .bestSeason(spot.getBestSeason())
                    .difficulty(spot.getDifficulty())
                    .windDirectionDegrees(spot.getWindDirectionDegrees())
                    .waveDetails(spot.getWaveDetails())
                    .coordinates(spot.getLocation().getCoordinates())
                    .build();
        }
        throw new RepositoryException("Could not save surf spot");
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }


    private EnumSet<Month> fetchMonthsForSpot(Long surfSpotId) {

        List<Month> months = findAll(
                FIND_ALL_QUERY,
                resultSet -> Month.valueOf(resultSet.getString("month_name")),
                surfSpotId);

        if (months.isEmpty()) {
            return EnumSet.noneOf(Month.class);
        }
        return EnumSet.copyOf(months);
    }

    private void saveMonths(Long surfSpotId, Set<Month> months) {

        if (months == null || months.isEmpty()) {
            return;
        }

        for (final Month month : months) {
            executeUpdate(INSERT_MONTHS_QUERY, surfSpotId, month.name());
        }
    }

    // TODO: Finish implementation
    @Override
    public boolean existsByName(String name) {
        return exists(FIND_BY_NAME_QUERY, name);
    }

    @Override
    public boolean existsById(String code ) {
        return exists(FIND_BY_ID_QUERY, code);
    }

    @Override
    public long countByCountryCode(String countryCode) {
        return count(COUNT_BY_COUNTRY_CODE_QUERY, countryCode);
    }

    @Override
    public long countByDifficultyLevel(DifficultyLevel difficultyLevel) {
        return count(COUNT_BY_DIFFICULTY_QUERY, difficultyLevel.name());
    }

    @Override
    public long countByWaveType(WaveType waveType) {
        return  count(COUNT_BY_WAVE_TYPE_QUERY, waveType.name());
    }

    // TODO: Je li bolje traziti po coast name ili po coast id? Kako ce se to odvijati u pozadini aplikacije
    @Override
    public long countByCoast(Coast coast) {
        return executeUpdate(FIND_BY_COAST_NAME_QUERY, coast.getName());
    }

    @Override
    public List<SurfSpot> findByCountryName(String countryName) {
        return List.of();
    }

    @Override
    public List<SurfSpot> findByDifficulty(DifficultyLevel difficultyLevel) {
        return List.of();
    }

    @Override
    public List<SurfSpot> findByInstructor(Instructor instructor) {
        return List.of();
    }

    @Override
    public List<SurfSpot> findByWaveType(WaveType waveType) {
        return List.of();
    }

    @Override
    public List<SurfSpot> findByMonthInBestSeason(Month month) {
        return List.of();
    }

    @Override
    public List<SurfSpot> findByMonthsInBestSeason(Set<Month> months) {
        return List.of();
    }

    @Override
    public List<SurfSpot> findByWindDirection(WindDirection windDirection) {
        return List.of();
    }

    @Override
    public List<SurfSpot> findBySchool(SurfingSchool surfingSchool) {
        return List.of();
    }

    @Override
    public List<SurfSpot> findByCoast(Coast coast) {
        return List.of();
    }

    // TODO: Mozda napisi wrappere za ove ulancane pozive?
    @Override
    public SurfSpot update(SurfSpot spot) {
        executeUpdate(UPDATE_BY_ID_QUERY,
                spot.getName(),
                spot.getLocation().getCoordinates().latitude(),
                spot.getLocation().getCoordinates().longitude(),
                spot.getLocation().getCoast().getCountry().code(),
                spot.getLocation().getCoast().getId(),
                spot.getWaveDetails().getWaveType().toString(),
                spot.getWaveDetails().getWaveHeight(),
                spot.getDifficulty().toString(),
                spot.getWindDirectionDegrees(),
                spot.getId());
        return spot;
    }
}
