package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.Permission;
import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.repository.RoleRepository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SqlRoleRepository extends BaseSqlRepository<Role> implements RoleRepository {
    private final RowMapper<Role> roleMapper;

    public static final String UPDATE_BY_ID_QUERY = "UPDATE roles SET name = ? WHERE id = ?";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM roles WHERE id = ?";
    public static final String FIND_BY_NAME_QUERY = "SELECT * FROM roles WHERE name = ?";
    public static final String FIND_ALL_QUERY = "SELECT * FROM roles";
    public static final String SAVE_QUERY = "INSERT INTO roles (name) VALUES (?)";
    public static final String DELETE_BY_ID_QUERY = "DELETE FROM roles WHERE id = ?";
    public static final String FIND_PERMISSIONS_BY_ROLE_ID_QUERY = "SELECT permission_name FROM role_permissions WHERE role_id = ?";

    public SqlRoleRepository(DataSource dataSource, RowMapper<Role> roleMapper) {
        super(dataSource);
        this.roleMapper = roleMapper;
    }

    @Override
    public Optional<Role> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, roleMapper, id)
                .map(this::populatePermissions);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, roleMapper, name)
                .map(this::populatePermissions);
    }

    @Override
    public List<Role> findAll() {
        return findAll(FIND_ALL_QUERY, roleMapper).stream()
                .map(this::populatePermissions)
                .toList();
    }

    @Override
    public Role save(Role role) {
        Long generatedId = insertAndGetId(SAVE_QUERY, role.getName());

        if (generatedId != null) {
            return Role.builder()
                    .from(role)
                    .id(generatedId)
                    .build();
        }
        throw new RepositoryException("Role could not be saved");
    }

    @Override
    public Role update(Role role) {
        executeUpdate(UPDATE_BY_ID_QUERY, role.getName(), role.getId());
        return role;
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }

    private Role populatePermissions(Role role) {
        return Role.builder()
                .from(role)
                .permissions(findPermissionsForRole(role.getId()))
                .build();
    }

    private Set<Permission> findPermissionsForRole(Long roleId) {
        Set<Permission> permissions = new HashSet<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_PERMISSIONS_BY_ROLE_ID_QUERY)) {

            stmt.setLong(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String permissionName = rs.getString("permission_name");
                    if (permissionName != null) {
                        permissions.add(Permission.valueOf(permissionName.toUpperCase().trim()));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Greška pri dohvaćanju dopuštenja za rolu " + roleId, e);
        }
        return permissions;
    }
}