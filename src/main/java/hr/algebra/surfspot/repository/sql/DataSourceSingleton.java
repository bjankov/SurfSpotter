package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.DataAccessException;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceSingleton {
    private static final String PROPERTIES_FILE = "/db.properties";

    private DataSourceSingleton() {
    }

    private static class Holder {
        private static final DataSource INSTANCE = createDataSource();

        private static DataSource createDataSource() {
            Properties properties = new Properties();
            try (InputStream inputStream = DataSourceSingleton.class.getResourceAsStream(PROPERTIES_FILE)) {
                if (inputStream == null) {
                    throw new DataAccessException("Cannot load properties file: " + PROPERTIES_FILE);
                }
                properties.load(inputStream);

                PGSimpleDataSource dataSource = new PGSimpleDataSource();
                dataSource.setUrl(properties.getProperty("db.url"));
                dataSource.setUser(properties.getProperty("db.username"));
                dataSource.setPassword(properties.getProperty("db.password"));

                return dataSource;
            } catch (IOException e) {
                throw new DataAccessException("Cannot load properties file: " + PROPERTIES_FILE, e);
            }
        }
    }

    // Bill Pugh Singleton
    public static DataSource getInstance() {
        return Holder.INSTANCE;
    }
}
