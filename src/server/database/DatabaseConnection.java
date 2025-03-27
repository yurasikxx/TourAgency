package server.database;

import server.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final ConfigLoader configLoader;
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        configLoader = new ConfigLoader();
        url = configLoader.getProperty("db.url");
        user = configLoader.getProperty("db.user");
        password = configLoader.getProperty("db.password");
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

            // Устанавливаем параметры соединения
            connection.setNetworkTimeout(
                    java.util.concurrent.Executors.newSingleThreadExecutor(),
                    30000 // 30 секунд таймаут
            );

            // Добавляем параметры в URL для автоматического переподключения
            System.out.println("Соединение с базой данных установлено.");
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}