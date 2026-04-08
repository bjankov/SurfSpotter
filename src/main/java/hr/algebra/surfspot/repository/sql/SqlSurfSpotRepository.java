package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.repository.SurfSpotRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class SqlSurfSpotRepository extends BaseSqlRepository<SurfSpot> implements SurfSpotRepository {
    public SqlSurfSpotRepository(DataSource dataSource) {
        super(dataSource);
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM surf_spots WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM surf_spots WHERE name = ?";
    private static final String FIND_ALL_QUERY = "SELECT month_name FROM surf_spot_months WHERE id = ?";
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

    public Optional<SurfSpot> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapSurfSpot, id);
    }

    public Optional<SurfSpot> findByName(final String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, this::mapSurfSpot, name);
    }

    @Override
    public List<SurfSpot> findAll() {
        return List.of();
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

    private SurfSpot mapSurfSpot(ResultSet resultSet) throws SQLException {
        Coordinates coordinates = new Coordinates(
                resultSet.getBigDecimal("latitude"),
                resultSet.getBigDecimal("longitude")
        );
        // TODO: Properly map the objects
        Coast coast = new Coast();
        Location location = new Location(coordinates, coast);
        WaveDetails waveDetails = new WaveDetails(
                WaveType.valueOf(resultSet.getString("wave_type")),
                resultSet.getDouble("wave_height")
        );

        return SurfSpot.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .location(location)
                .waveDetails(waveDetails)
                .windDirectionDegrees(resultSet.getInt("wind_direction"))
                .difficulty(DifficultyLevel.valueOf(resultSet.getString("difficulty")))
                .bestSeason(fetchMonthsForSpot(resultSet.getLong("id")))
                .build();
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
        return false;
    }

    @Override
    public boolean existsById(String code) {
        return false;
    }

    @Override
    public long countByCountryCode(String countryCode) {
        return 0;
    }

    @Override
    public long countByDifficultyLevel(DifficultyLevel difficultyLevel) {
        return 0;
    }

    @Override
    public long countByWaveType(WaveType waveType) {
        return 0;
    }

    @Override
    public long countByCoast(Coast coast) {
        return 0;
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
}
