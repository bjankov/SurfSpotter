package hr.algebra.surfspot.repository.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetExtractor<R> {
    R extract(ResultSet resultSet) throws SQLException;
}