package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.repository.sql.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role map(ResultSet resultSet) throws SQLException {
        return Role.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}