package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.Permission;
import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role map(ResultSet resultSet) throws SQLException {
        Set<Permission> permissions = new HashSet<>();
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            permissions.add(Permission.valueOf(name));
        }
        return Role.builder()
                .name(resultSet.getString("name"))
                .permissions(permissions)
                .build();
    }
}
