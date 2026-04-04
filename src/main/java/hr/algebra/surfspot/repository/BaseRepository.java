package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.exception.EntityNotFoundException;
import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.util.DataSourceUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T> {
    protected Optional<T> findSingleResult(String query, RowMapper<T> mapper, Object ... params) {
        try (Connection connection = DataSourceUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i += 1) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapper.map(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Greska u bazi:" + e.getMessage());
        }
        return Optional.empty();
    }

    protected <R> List<R> findAll(String query, RowMapper<R> mapper, Object... params) {
        List<R> results = new ArrayList<>();

        try (Connection connection = DataSourceUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            for (int i = 0; i < params.length; i += 1) {
                preparedStatement.setObject(i + 1, params[i]);
            }

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

    protected Long insertAndGetId(String query, Object... params) {
        try (Connection connection = DataSourceUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

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
        try (Connection connection = DataSourceUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Greska u bazi:" + query + ": " + e.getMessage());
        }
    }
}
