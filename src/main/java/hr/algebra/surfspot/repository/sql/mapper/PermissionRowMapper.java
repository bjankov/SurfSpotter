package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.Permission;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionRowMapper implements RowMapper<Permission> {
    @Override
    public Permission map(ResultSet resultSet) throws SQLException {
        String permissionName = resultSet.getString("permission_name");

        try {
            return Permission.valueOf(permissionName);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Pronađena je nepoznata permisija u bazi podataka: " + permissionName, e);
        }
    }
}
