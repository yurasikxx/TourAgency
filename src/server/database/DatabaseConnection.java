package server.database;

import server.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс для управления подключением к базе данных.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Приватный конструктор для реализации Singleton
    private DatabaseConnection() {
        try {
            // Загрузка драйвера JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Получение параметров подключения из конфигурации
            ConfigLoader configLoader = new ConfigLoader();
            String url = configLoader.getProperty("db.url");
            String user = configLoader.getProperty("db.user");
            String password = configLoader.getProperty("db.password");

            // Установка соединения с базой данных
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Соединение с базой данных успешно установлено.");
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: MySQL JDBC Driver не найден!");
            throw new RuntimeException("MySQL JDBC Driver не найден", e);
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к базе данных: " + e.getMessage());
            throw new RuntimeException("Не удалось подключиться к базе данных", e);
        }
    }

    /**
     * Возвращает единственный экземпляр DatabaseConnection.
     *
     * @return Экземпляр DatabaseConnection.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Возвращает соединение с базой данных.
     *
     * @return Соединение с базой данных.
     * @throws SQLException Если соединение не удалось получить.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Соединение с базой данных не установлено или закрыто.");
        }
        return connection;
    }

    /**
     * Закрывает соединение с базой данных.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}