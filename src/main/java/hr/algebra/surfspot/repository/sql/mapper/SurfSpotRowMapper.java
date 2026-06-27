package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

public class SurfSpotRowMapper implements RowMapper<SurfSpot> {
    @Override
    public SurfSpot map(ResultSet resultSet) throws SQLException {
        Country country = new Country(
                resultSet.getString("country_code"),
                resultSet.getString("country_name")
        );
        Coast coast = Coast.builder()
                .id(resultSet.getLong("coast_id"))
                .name(resultSet.getString("coast_name"))
                .country(country)
                .build();
        Coordinates coordinates = new Coordinates(
                resultSet.getBigDecimal("latitude"),
                resultSet.getBigDecimal("longitude")
        );
        Location location = new Location(coordinates, coast);

        String waveTypeStr = resultSet.getString("wave_type");
        BigDecimal waveHeightVal = resultSet.getBigDecimal("wave_height");
        WaveType waveType = waveTypeStr != null ? WaveType.valueOf(waveTypeStr) : null;
        Double waveHeight = waveHeightVal != null ? waveHeightVal.doubleValue() : null;
        WaveDetails waveDetails = (waveType != null || waveHeight != null)
                ? new WaveDetails(waveType, waveHeight)
                : null;

        int windDirection = resultSet.getInt("wind_direction");
        Integer windDirectionVal = resultSet.wasNull() ? null : windDirection;

        return SurfSpot.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("surf_spot_name"))
                .location(location)
                .waveDetails(waveDetails)
                .windDirectionDegrees(windDirectionVal)
                .difficulty(DifficultyLevel.valueOf(resultSet.getString("difficulty")))
                .bestSeason(EnumSet.noneOf(Month.class))
                .imagePath(resultSet.getString("image_path"))
                .build();
    }
}