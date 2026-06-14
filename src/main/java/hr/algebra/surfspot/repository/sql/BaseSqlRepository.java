package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.PersistenceException;
import hr.algebra.surfspot.exception.ResourceNotFoundException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("SqlSourceToSinkFlow")
public abstract class BaseSqlRepository<T> {
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
            throw new PersistenceException("Greska u bazi: " + e.getMessage(), e);
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
            throw new PersistenceException("Greska u bazi:" + e.getMessage(), e);
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
            throw new PersistenceException("Greška pri provjeri postojanja: " + e.getMessage(), e);
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
            throw new PersistenceException("Greška pri brojanju: " + e.getMessage(), e);
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
                throw new ResourceNotFoundException("No rows affected");
            }
        } catch (SQLException e) {
            throw new PersistenceException("Greska u bazi: " + query + ": " + e.getMessage(), e);
        }
    }

    protected int executeUpdate(String query, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setParams(preparedStatement, params);
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenceException("Greska u bazi:" + query + ": " + e.getMessage(), e);
        }
    }

    protected void requireAffectedRows(int affectedRows, String errorMessage) {
        if (affectedRows == 0) {
            throw new PersistenceException(errorMessage);
        }
    }

    protected void requireGeneratedId(Long generatedId, String errorMessage) {
        if (generatedId == null) {
            throw new PersistenceException(errorMessage);
        }
    }

    private void setParams(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }

    protected <R> R executeQuery(String query, ResultSetExtractor<R> extractor, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setParams(preparedStatement, params);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractor.extract(resultSet);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Greška u bazi kod izvršavanja upita: " + e.getMessage(), e);
        }
    }

    protected void executeInTransaction(TransactionalWork work) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            work.execute(connection);
            connection.commit();
        } catch (Exception e) {
            throw new PersistenceException("Greška u transakciji: " + e.getMessage(), e);
        }
    }

    protected void executeDeleteAndBatchInsert(
            String deleteQuery, Object[] deleteParams,
            String insertQuery, List<Object[]> insertParamSets) {
        executeInTransaction(connection -> {
            executeUpdate(connection, deleteQuery, deleteParams);
            if (!insertParamSets.isEmpty()) {
                executeBatchUpdate(connection, insertQuery, insertParamSets);
            }
        });
    }

    private void executeUpdate(Connection connection, String query, Object... params) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            setParams(preparedStatement, params);
            preparedStatement.executeUpdate();
        }
    }

    private void executeBatchUpdate(Connection connection, String query, List<Object[]> paramSets) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Object[] params : paramSets) {
                setParams(preparedStatement, params);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    @FunctionalInterface
    protected interface TransactionalWork {
        void execute(Connection connection) throws SQLException;
    }
}