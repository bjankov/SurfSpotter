package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.UserRepository;
import hr.algebra.surfspot.repository.sql.mapper.RowMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class SqlUserRepository extends BaseSqlRepository<User> implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(SqlUserRepository.class);
    private final RowMapper<User> userMapper;

    public SqlUserRepository(DataSource dataSource) {
        super(dataSource);
        this.userMapper = RowMapperFactory.getInstance().getMapper(User.class);
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT * FROM users WHERE username = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_BY_USERNAME_QUERY = "DELETE FROM users WHERE username = ?";
    private static final String DELETE_BY_EMAIL_QUERY = "DELETE FROM users WHERE email = ?";

    @Override
    public Optional<User> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, userMapper, id);
    }

    @Override
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

        if (generatedId != null) {
            return User.builder()
                    .id(generatedId)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .passwordHash(user.getPasswordHash())
                    .build();
        }
        throw new RepositoryException("Could not save new user.");
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public void deleteByUsername(String username) {
        int affectedRows = executeUpdate(DELETE_BY_USERNAME_QUERY, username);
        if (affectedRows == 0) {
            log.error("Failed to delete user by username: {}", username);
            throw new RepositoryException("Failed to delete users by username: " + username);
        }
    }

    @Override
    public void deleteByEmail(String email) {
        int affectedRows = executeUpdate(DELETE_BY_EMAIL_QUERY, email);
        if (affectedRows == 0) {
            log.error("Failed to delete user by email: {}", email);
            throw new RepositoryException("Failed to delete users by email: " + email);
        }
    }
}
