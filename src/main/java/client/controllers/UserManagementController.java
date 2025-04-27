package client.controllers;

import client.MainClient;
import client.models.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

public class UserManagementController {
    @FXML
    private TableView<UserModel> usersTable;
    @FXML
    private TableColumn<UserModel, Integer> idColumn;
    @FXML
    private TableColumn<UserModel, String> usernameColumn;
    @FXML
    private TableColumn<UserModel, String> roleColumn;
    @FXML
    private TableColumn<UserModel, Double> balanceColumn;
    @FXML
    private TableColumn<UserModel, String> fullNameColumn;
    @FXML
    private TableColumn<UserModel, Integer> ageColumn;
    @FXML
    private TableColumn<UserModel, String> emailColumn;
    @FXML
    private TableColumn<UserModel, String> phoneColumn;
    @FXML
    private TextField usernameField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TextField balanceField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;

    private Stage primaryStage;
    private UserModel currentAdmin;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.currentAdmin = MainClient.getCurrentUser();
        initializeTable();
        loadUsers();
        setupRoleComboBox();
        setupTableSelectionListener();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void setupRoleComboBox() {
        roleComboBox.getItems().addAll("USER", "ADMIN");
        roleComboBox.setValue("USER");
    }

    private void setupTableSelectionListener() {
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillFieldsWithSelectedUser(newSelection);
                    }
                });
    }

    private void fillFieldsWithSelectedUser(UserModel user) {
        usernameField.setText(user.getUsername());
        roleComboBox.setValue(user.getRole());
        balanceField.setText(String.valueOf(user.getBalance()));
        fullNameField.setText(user.getFullName());
        ageField.setText(String.valueOf(user.getAge()));
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
    }

    private void loadUsers() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_ALL_USERS");
            String response = in.readLine();

            if (response.startsWith("USERS")) {
                usersTable.getItems().clear();
                String[] usersData = response.substring(6).split("\\|");

                for (String userData : usersData) {
                    String[] fields = userData.split(",");
                    if (fields.length == 8) {
                        UserModel user = new UserModel(
                                Integer.parseInt(fields[0]),
                                fields[1],
                                fields[2],
                                Double.parseDouble(fields[3]),
                                fields[4],
                                Integer.parseInt(fields[5]),
                                fields[6],
                                fields[7]
                        );
                        usersTable.getItems().add(user);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить пользователей");
        }
    }

    @FXML
    private void handleAddUser() {
        try {
            String username = usernameField.getText().trim();
            String role = roleComboBox.getValue();
            double balance = Double.parseDouble(balanceField.getText());
            String fullName = fullNameField.getText().trim();
            int age = Integer.parseInt(ageField.getText());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showAlert("Ошибка", "Все поля должны быть заполнены");
                return;
            }

            if (balance < 0) {
                showAlert("Ошибка", "Баланс не может быть отрицательным");
                return;
            }

            if (isNotValidFullName(fullName)) {
                showAlert("Ошибка", "ФИО должно соответствовать формату: 'Фамилия Имя Отчество'");
            }

            if (age <= 0) {
                showAlert("Ошибка", "Возраст должен быть положительным числом");
                return;
            }

            if (!email.contains("@")) {
                showAlert("Ошибка", "Введите корректный email");
                return;
            }

            if (isUsernameExists(username)) {
                showAlert("Ошибка", "Пользователь с таким именем уже существует");
                return;
            }

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("ADD_USER " + username + " " + username + " " + role + " " + balance + " " +
                        fullName + " " + age + " " + email + " " + phone);
                String response = in.readLine();

                if ("USER_ADDED".equals(response)) {
                    showAlert("Успех", "Пользователь добавлен");
                    loadUsers();
                    clearFields();
                } else {
                    showAlert("Ошибка", response);
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Некорректное значение баланса или возраста");
        } catch (Exception e) {
            showAlert("Ошибка", "Проверьте введенные данные");
        }
    }

    @FXML
    private void handleUpdateUser() {
        UserModel selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                String newUsername = usernameField.getText().trim();
                String newRole = roleComboBox.getValue();
                double newBalance = Double.parseDouble(balanceField.getText());
                String newFullName = fullNameField.getText().trim();
                int newAge = Integer.parseInt(ageField.getText());
                String newEmail = emailField.getText().trim();
                String newPhone = phoneField.getText().trim();

                if (newUsername.isEmpty() || newFullName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                    showAlert("Ошибка", "Все поля должны быть заполнены");
                    return;
                }

                if (newBalance < 0) {
                    showAlert("Ошибка", "Баланс не может быть отрицательным");
                    return;
                }

                if (isNotValidFullName(newFullName)) {
                    showAlert("Ошибка", "ФИО должно соответствовать формату: 'Фамилия Имя Отчество'");
                }

                if (newAge <= 0) {
                    showAlert("Ошибка", "Возраст должен быть положительным числом");
                    return;
                }

                if (!newEmail.contains("@")) {
                    showAlert("Ошибка", "Введите корректный email");
                    return;
                }

                if (!newUsername.equals(selectedUser.getUsername()) && isUsernameExists(newUsername)) {
                    showAlert("Ошибка", "Пользователь с таким именем уже существует");
                    return;
                }

                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    out.println("UPDATE_USER " + selectedUser.getId() + " " + newUsername + " " +
                            selectedUser.getUsername() + " " + newRole + " " + newBalance + " " +
                            newFullName + " " + newAge + " " + newEmail + " " + newPhone);
                    String response = in.readLine();

                    if ("USER_UPDATED".equals(response)) {
                        showAlert("Успех", "Пользователь обновлен");
                        loadUsers();
                        clearFields();
                    } else {
                        showAlert("Ошибка", response);
                    }
                }
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Некорректное значение баланса или возраста");
            } catch (Exception e) {
                showAlert("Ошибка", "Проверьте введенные данные");
            }
        } else {
            showAlert("Ошибка", "Выберите пользователя для изменения");
        }
    }

    @FXML
    private void handleDeleteUser() {
        UserModel selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (selectedUser.getId() == currentAdmin.getId()) {
                showAlert("Ошибка", "Вы не можете удалить самого себя");
                return;
            }

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("DELETE_USER " + selectedUser.getId());
                String response = in.readLine();

                if ("USER_DELETED".equals(response)) {
                    showAlert("Успех", "Пользователь удален");
                    loadUsers();
                    clearFields();
                } else {
                    showAlert("Ошибка", response);
                }
            } catch (Exception e) {
                showAlert("Ошибка", "Не удалось удалить пользователя");
            }
        } else {
            showAlert("Ошибка", "Выберите пользователя для удаления");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();
            TourController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(new Scene(root));
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        usernameField.clear();
        roleComboBox.setValue("USER");
        balanceField.clear();
        fullNameField.clear();
        ageField.clear();
        emailField.clear();
        phoneField.clear();
    }

    private boolean isUsernameExists(String username) {
        for (UserModel user : usersTable.getItems()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotValidFullName(String fullName) {
        Pattern pattern = Pattern.compile("^[А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)?\\s[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+$");
        return !pattern.matcher(fullName).matches();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}