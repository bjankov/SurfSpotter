package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.util.DataSourceUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

// TODO: Refactor SurfSpotRepository to extend BaseRespository
public class SurfSpotRepository extends BaseRepository<SurfSpot> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM surf_spots WHERE id = ?";
    private static final String INSERT_SURF_SPOT_QUERY = """
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
    private static final String FIND_ALL_QUERY = "SELECT month_name FROM surf_spot_months WHERE id = ?";
    private static final String INSERT_MONTHS_QUERY = "INSERT INTO surf_spot_months (surf_spot_id, month_name) VALUES (?, ?)";

    public Optional<SurfSpot> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapSurfSpot, id);
    }

    public SurfSpot save(SurfSpot spot) {

        Long generatedId = insertAndGetId(
                INSERT_SURF_SPOT_QUERY,
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
                    .build();
        }
        throw new RuntimeException("Could not save surf spot");
    }

    private SurfSpot mapSurfSpot(ResultSet resultSet) throws SQLException {
        Location location = new Location();
        Coordinates coordinates = new Coordinates(
                resultSet.getBigDecimal("latitude"),
                resultSet.getBigDecimal("longitude")
        );
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
}
