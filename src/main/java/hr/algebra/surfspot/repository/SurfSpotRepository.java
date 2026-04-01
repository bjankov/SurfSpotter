package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.util.DataSourceUtils;

import java.sql.*;

// TODO: Refactor SurfSpotRepository to extend BaseRespository
public class SurfSpotRepository {
    public SurfSpot save(SurfSpot surfSpot) {
        String sql = """
                INSERT INTO surf_spot (name, latitude, longitude, country_code, coast_id, wave_type, difficulty)"
                "VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DataSourceUtils.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, surfSpot.getName());
            preparedStatement.setBigDecimal(2, surfSpot.getLocation().getCoordinates().latitude());
            preparedStatement.setBigDecimal(3, surfSpot.getLocation().getCoordinates().longitude());
            preparedStatement.setLong(4, surfSpot.getLocation().getCoast().getId());
            preparedStatement.setLong(5, surfSpot.getLocation().getCoast().getId());
            preparedStatement.setString(6, surfSpot.getWaveDetails().getWaveType().name());
            preparedStatement.setString(7, surfSpot.getDifficulty().name());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Neuspjesno spremanje surf spota u bazu podataka.");
            }

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    Long newId = resultSet.getLong(1);

                    System.out.println("Surf spot uspješno spremljen u bazu podataka.");

                    return SurfSpot.builder()
                            .from(surfSpot)
                            .id(newId)
                            .build();
                } else {
                    throw new SQLException("Stvaranje surf spota nije uspjelo, ID nije dobiven");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
