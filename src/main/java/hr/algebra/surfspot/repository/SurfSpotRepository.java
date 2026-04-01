package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.util.DataSourceUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SurfSpotRepository {
    public void save(SurfSpot surfSpot) {
        String sql = "INSERT INTO surf_spot (name, latitude, longitude, coast, wave_type, difficulty)" +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataSourceUtils.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, surfSpot.getName());
            preparedStatement.setBigDecimal(2, surfSpot.getLocation().getCoordinates().latitude());
            preparedStatement.setBigDecimal(3, surfSpot.getLocation().getCoordinates().longitude());
            preparedStatement.setLong(4, surfSpot.getLocation().getCoast().getId());
            preparedStatement.setString(5, surfSpot.getWaveDetails().getWaveType().name());
            preparedStatement.setString(6, surfSpot.getDifficulty().name());

            preparedStatement.executeUpdate();
            System.out.println("Surf spot uspješno spremljen u bazu podataka.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
