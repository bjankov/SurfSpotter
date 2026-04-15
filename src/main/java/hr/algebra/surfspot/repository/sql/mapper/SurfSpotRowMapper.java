package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

public class SurfSpotRowMapper implements RowMapper<SurfSpot> {
    public SurfSpot map(ResultSet resultSet) throws SQLException {
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
                .bestSeason(EnumSet.noneOf(Month.class))
                .build();
    }
}
