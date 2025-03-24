package client.models;

import java.util.Objects;

/**
 * Модель пользователя для клиентской части.
 */
public class UserModel {
    private int id;
    private String username;
    private String role;
    private double balance;

    public UserModel(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public UserModel(int id, String username, String role, double balance) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.balance = balance;
    }

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return id == userModel.id && Double.compare(balance, userModel.balance) == 0 && Objects.equals(username, userModel.username) && Objects.equals(role, userModel.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, role, balance);
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", balance=" + balance +
                '}';
    }
}