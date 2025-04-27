package client.models;

import java.util.Objects;

public class UserModel {
    private int id;
    private String username;
    private String role;
    private double balance;
    private String fullName;
    private int age;
    private String email;
    private String phone;

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

    public UserModel(int id, String username, String role, double balance, String fullName, int age, String email, String phone) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.balance = balance;
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return id == userModel.id && Double.compare(balance, userModel.balance) == 0 && age == userModel.age && Objects.equals(username, userModel.username) && Objects.equals(role, userModel.role) && Objects.equals(fullName, userModel.fullName) && Objects.equals(email, userModel.email) && Objects.equals(phone, userModel.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, role, balance, fullName, age, email, phone);
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", balance=" + balance +
                ", fullName='" + fullName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}