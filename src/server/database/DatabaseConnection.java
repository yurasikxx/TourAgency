package server.database;

import server.utils.ConfigLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public class DatabaseConnection {
    private static final int NETWORK_TIMEOUT_MS = 30000;
    private static volatile DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        ConfigLoader configLoader = new ConfigLoader();
        this.url = configLoader.getProperty("db.url");
        this.user = configLoader.getProperty("db.user");
        this.password = configLoader.getProperty("db.password");
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.setNetworkTimeout(Executors.newSingleThreadExecutor(), NETWORK_TIMEOUT_MS);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}