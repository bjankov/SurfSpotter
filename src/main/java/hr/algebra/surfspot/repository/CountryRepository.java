package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.util.DataSourceUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CountryRepository {
    public Optional<Country> findByCode(final String code) {
        String sql = "SELECT code, name FROM countries WHERE code = ? ";

        try (Connection connection = DataSourceUtils.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, code.toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Country(
                            resultSet.getString("code"),
                            resultSet.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }
}
