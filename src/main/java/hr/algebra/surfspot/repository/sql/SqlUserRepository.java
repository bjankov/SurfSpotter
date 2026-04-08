package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlUserRepository extends BaseSqlRepository<User> implements UserRepository {
    public SqlUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT * FROM users WHERE username = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_BY_NAME_QUERY = "DELETE FROM users WHERE username = ?";

    @Override
    public Optional<User> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, this::mapRowToUser, id);
    }

    @Override
    public Optional<User> findByName(String username) {
        return findSingleResult(FIND_BY_USERNAME_QUERY, this::mapRowToUser, username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findSingleResult(FIND_BY_EMAIL_QUERY, this::mapRowToUser, email);
    }

    @Override
    public List<User> findAll() {
        return findAll(FIND_ALL_QUERY, this::mapRowToUser);
    }

    public User save (final User user) {
        Long generatedId = insertAndGetId(
                INSERT_USER_QUERY,
                user.getUsername(),
                user.getPasswordHash()
        );

        if (generatedId != null) {
            return User.builder()
                    .id(generatedId)
                    .username(user.getUsername())
                    .passwordHash(user.getPasswordHash())
                    .build();
        }
        throw new RepositoryException("Could not save new user.");
    }

    @Override
    public void delete(Long id) {
        executeUpdate(DELETE_BY_ID_QUERY);
    }

    private User mapRowToUser(final ResultSet resultSet) throws SQLException {
        // TODO: Add role
        return User.builder()
                .username(resultSet.getString("username"))
                .passwordHash(resultSet.getString("password_hash"))
                .build();
    }
}
