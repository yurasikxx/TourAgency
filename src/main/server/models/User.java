package main.server.models;

/**
 * Класс, представляющий сущность "Пользователь".
 */
public class User {
    private int id;          // Уникальный идентификатор пользователя
    private String username; // Логин пользователя
    private String password; // Пароль пользователя
    private String role;     // Роль пользователя (администратор, сотрудник, гость)

    // Конструкторы

    /**
     * Конструктор по умолчанию.
     */
    public User() {
    }

    /**
     * Конструктор с параметрами.
     *
     * @param id       Уникальный идентификатор пользователя.
     * @param username Логин пользователя.
     * @param password Пароль пользователя.
     * @param role     Роль пользователя.
     */
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Конструктор без ID (используется при создании нового пользователя).
     *
     * @param username Логин пользователя.
     * @param password Пароль пользователя.
     * @param role     Роль пользователя.
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Переопределение метода toString()

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}