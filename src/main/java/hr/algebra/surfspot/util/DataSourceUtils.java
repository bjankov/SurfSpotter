package hr.algebra.surfspot.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataSourceUtils {
    private static final Properties PROPERTIES = new Properties();
    private static final String PROPERTIES_FILE = "/db.properties";

    static {
        try (InputStream is = DataSourceUtils.class.getResourceAsStream(PROPERTIES_FILE)) {
            PROPERTIES.load(is);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.user"),
                PROPERTIES.getProperty("db.password")
        );
    }
}