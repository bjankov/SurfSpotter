package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.PersistenceException;
import hr.algebra.surfspot.model.Permission;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SqlUserRepository extends BaseSqlRepository<User> implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(SqlUserRepository.class);
    private final RowMapper<User> userMapper;
    private final RowMapper<Permission> permissionMapper;

    public SqlUserRepository(DataSource dataSource, RowMapper<User> userMapper, RowMapper<Permission> permissionMapper) {
        super(dataSource);
        this.userMapper = userMapper;
        this.permissionMapper = permissionMapper;
    }

    private static final String UPDATE_BY_ID = "UPDATE users SET username = ?, email = ? WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT * FROM users WHERE username = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_BY_USERNAME_QUERY = "DELETE FROM users WHERE username = ?";
    private static final String DELETE_BY_EMAIL_QUERY = "DELETE FROM users WHERE email = ?";
    private static final String LOAD_PERMISSIONS_FOR_USER_BY_ID = """
            SELECT rp.permission_name
            FROM role_permissions rp
            JOIN user_roles ur ON rp.role_id = ur.role_id
            WHERE ur.user_id = ?
            """;

    @Override
    public Optional<User> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, userMapper, id);
    }

    public Optional<User> findByName(String username) {
        return findSingleResult(FIND_BY_USERNAME_QUERY, userMapper, username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return findSingleResult(FIND_BY_EMAIL_QUERY, userMapper, email);
    }

    @Override
    public List<User> findAll() {
        return findAll(FIND_ALL_QUERY, userMapper);
    }

    public User save (final User user) {
        Long generatedId = insertAndGetId(
                INSERT_USER_QUERY,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash()
        );

        requireGeneratedId(generatedId, "Could not save user with ID: " + user.getId());

        return User.builder()
                .id(generatedId)
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .build();
    }

    // TODO: Sto sa passwordom?
    @Override
    public User update(User user) {
        int affectedRows = executeUpdate(
                UPDATE_BY_ID,
                user.getUsername(),
                user.getEmail(),
                user.getId()
        );
        requireAffectedRows(affectedRows, "Could not update user with ID: " + user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        int affectedRows = executeUpdate(
                DELETE_BY_ID_QUERY,
                id
        );
        requireAffectedRows(affectedRows, "Could not delete user with ID: " + id);
    }

    @Override
    public void deleteByUsername(String username) {
        int affectedRows = executeUpdate(DELETE_BY_USERNAME_QUERY, username);
        if (affectedRows == 0) {
            log.error("Failed to delete user by username: {}", username);
            throw new PersistenceException("Failed to delete users by username: " + username);
        }
    }

    @Override
    public void deleteByEmail(String email) {
        int affectedRows = executeUpdate(DELETE_BY_EMAIL_QUERY, email);
        if (affectedRows == 0) {
            log.error("Failed to delete user by email: {}", email);
            throw new PersistenceException("Failed to delete users by email: " + email);
        }
    }

    @Override
    public Set<Permission> findPermissionsByUserId(Long userId) {
        List<Permission> permList = findAll(LOAD_PERMISSIONS_FOR_USER_BY_ID, permissionMapper, userId);

        Set<Permission> permissions = new HashSet<>(permList);
        permissions.remove(null);

        return permissions;
    }
}
