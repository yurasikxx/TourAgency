package server.database;

import server.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private final ConfigLoader configLoader;
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        configLoader = new ConfigLoader();
        url = configLoader.getProperty("db.url");
        user = configLoader.getProperty("db.user");
        password = configLoader.getProperty("db.password");
        createConnection();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(1)) {
                createConnection();
            }
        } catch (SQLException e) {
            createConnection(); // Попробуем пересоздать при ошибке проверки
        }
        return connection;
    }

    private void createConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);

            // Установка параметров для предотвращения преждевременного закрытия
            connection.setNetworkTimeout(
                    java.util.concurrent.Executors.newSingleThreadExecutor(),
                    10000 // 10 секунд таймаут на операции
            );

            System.out.println("Соединение с базой данных (пере)установлено.");
        } catch (Exception e) {
            throw new RuntimeException("Не удалось подключиться к базе данных", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}