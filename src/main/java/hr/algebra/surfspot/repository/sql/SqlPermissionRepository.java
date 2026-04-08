package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.model.Permission;
import hr.algebra.surfspot.repository.PermissionRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlPermissionRepository extends BaseSqlRepository<Permission> implements PermissionRepository {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM permissions WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM permissions WHERE name = ?";
    private static final String FIND_ALL_BY_ID_QUERY = "SELECT * FROM permissions";
    private static final String SAVE_QUERY = "INSERT INTO permissions name VALUES ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM permissions WHERE id = ?";

    public SqlPermissionRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToRole, id);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, this::mapRowToRole, name);
    }

    @Override
    public List<Permission> findAll() {
        return findAll(FIND_ALL_BY_ID_QUERY, this::mapRowToRole);
    }

    @Override
    public Permission save(Permission permission) {
        Long generatedId = insertAndGetId(SAVE_QUERY, permission);
        return new Permission(generatedId, permission.getName());
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }

    private Permission mapRowToRole(ResultSet resultSet) throws SQLException {
        return new Permission(resultSet.getLong("id"), resultSet.getString("name"));
    }
}
