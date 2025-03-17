package client.models;

/**
 * Модель пользователя для клиентской части.
 */
public class UserModel {
    private int id;
    private String username;
    private String role;

    // Конструкторы
    public UserModel() {}

    public UserModel(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // Метод преобразования серверной модели в клиентскую
    public static UserModel fromServerModel(server.models.User serverUser) {
        return new UserModel(
                serverUser.getId(),
                serverUser.getUsername(),
                serverUser.getRole()
        );
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}