package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.util.DataSourceUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public abstract class BaseRepository<T> {
    protected Optional<T> findSingleResult(String sql, RowMapper<T> mapper, Object ... params) {
        try (Connection connection = DataSourceUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i += 1) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapper.map(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Greska u bazi:" + e.getMessage());
        }
        return Optional.empty();
    }
}
