package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.EntityNotFoundException;
import hr.algebra.surfspot.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseSqlRepository<T> {
    private static final Logger log = LoggerFactory.getLogger(BaseSqlRepository.class);

    protected final DataSource dataSource;

    protected BaseSqlRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Optional<T> findSingleResult(String query, RowMapper<T> mapper, Object ... params) {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setParams(preparedStatement, params);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapper.map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Greska u bazi:" + e.getMessage());
        }
        return Optional.empty();
    }

    protected <R> List<R> findAll(String query, RowMapper<R> mapper, Object... params) {
        List<R> results = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            setParams(preparedStatement, params);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(mapper.map(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Greska u bazi:" + e.getMessage());
        }
        return results;
    }

    protected boolean exists(String query, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setParams(preparedStatement, params);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && (resultSet.getInt(1) > 0 || resultSet.getRow() > 0);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Greška pri provjeri postojanja: " + e.getMessage());
        }
    }

    protected long count(String query, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setParams(preparedStatement, params);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Greška pri brojanju: " + e.getMessage());
        }
        return 0;
    }

    protected Long insertAndGetId(String query, Object... params) {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setParams(preparedStatement, params);

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                throw new EntityNotFoundException("No rows affected");
            }
        } catch (SQLException e) {
            throw new RepositoryException("Greska u bazi: " + query + ": " + e.getMessage());
        }
    }

    protected int executeUpdate(String query, Object... params) {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setParams(preparedStatement, params);
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Greska u bazi:" + query + ": " + e.getMessage());
        }
    }

    private void setParams(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }
}
