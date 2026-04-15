package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User map(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .username(resultSet.getString("username"))
                .email(resultSet.getString("email"))
                .passwordHash(resultSet.getString("password_hash"))
                .build();
    }
}
