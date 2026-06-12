package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.PersistenceException;
import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;

public class SqlUserRepository extends BaseSqlRepository<User> implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(SqlUserRepository.class);
    private final RowMapper<User> userMapper;

    public SqlUserRepository(DataSource dataSource, RowMapper<User> userMapper) {
        super(dataSource);
        this.userMapper = userMapper;
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
    private static final String DELETE_USER_ROLES_BY_USER_ID = "DELETE FROM user_roles WHERE user_id = ?";
    private static final String INSERT_USER_ROLE = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
    private static final String LOAD_ROLES_WITH_PERMISSIONS_FOR_USER = """
        SELECT r.id AS role_id, r.name AS role_name, rp.permission_name
        FROM user_roles ur
        JOIN roles r ON ur.role_id = r.id
        LEFT JOIN role_permissions rp ON r.id = rp.role_id
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

    @Override
    public User save(final User user) {
        Long generatedId = insertAndGetId(
                INSERT_USER_QUERY,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash()
        );

        requireGeneratedId(generatedId, "Could not save user with ID: " + user.getId());

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                executeUpdate(INSERT_USER_ROLE, generatedId, role);
            }
        }

        return User.builder()
                .id(generatedId)
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .withRoles(user.getRoles())
                .build();
    }

    @Override
    public User update(User user) {
        int affectedRows = executeUpdate(
                UPDATE_BY_ID,
                user.getUsername(),
                user.getEmail(),
                user.getId()
        );
        requireAffectedRows(affectedRows, "Could not update user with ID: " + user.getId());

        executeUpdate(DELETE_USER_ROLES_BY_USER_ID, user.getId());

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                executeUpdate(INSERT_USER_ROLE, user.getId(), role.getId());
            }
        }

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
    public Set<Role> findRolesByUserId(Long userId) {
        return executeQuery(LOAD_ROLES_WITH_PERMISSIONS_FOR_USER, rs -> {
            Set<Role> roles = new HashSet<>();

            while (rs.next()) {
                String roleName = rs.getString("role_name");

                if (roleName != null && !roleName.isBlank()) {
                    try {
                        roles.add(Role.valueOf(roleName.toUpperCase()));
                    } catch (IllegalArgumentException _) {
                        log.warn("Nepoznata uloga u bazi podataka: {}", roleName);
                    }
                }
            }

            return roles;
        }, userId);
    }
}
