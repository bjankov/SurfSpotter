package hr.algebra.surfspot.repository.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hr.algebra.surfspot.exception.DataAccessException;
import javafx.fxml.LoadException;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceFactory {
    private static final String PROPERTIES_FILE = "/db.properties";

    private DataSourceFactory() {
    }

    public static DataSource createDataSource() {
        Properties props = new Properties();
        try (InputStream is = DataSourceFactory.class.getResourceAsStream(PROPERTIES_FILE)) {
            props.load(is);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(20000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");

            return new HikariDataSource(config);
        } catch (IOException e) {
            throw new DataAccessException("Neuspjelo učitavanje konfiguracije baze", e);
        }
    }
}