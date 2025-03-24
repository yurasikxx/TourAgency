package client.controllers;

import client.models.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TextField balanceField;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeTable();
        loadUsers();
        setupRoleComboBox();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }

    private void setupRoleComboBox() {
        roleComboBox.getItems().addAll("USER", "ADMIN");
        roleComboBox.setValue("USER");
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
                    if (fields.length == 4) {
                        UserModel user = new UserModel(
                                Integer.parseInt(fields[0]),
                                fields[1],
                                fields[2],
                                Double.parseDouble(fields[3])
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
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            double balance = Double.parseDouble(balanceField.getText());

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("ADD_USER " + username + " " + password + " " + role + " " + balance);
                String response = in.readLine();

                if ("USER_ADDED".equals(response)) {
                    showAlert("Успех", "Пользователь добавлен");
                    loadUsers();
                    clearFields();
                } else {
                    showAlert("Ошибка", response);
                }
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Проверьте введенные данные");
        }
    }

    @FXML
    private void handleUpdateUser() {
        UserModel selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String role = roleComboBox.getValue();
                double balance = Double.parseDouble(balanceField.getText());

                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    out.println("UPDATE_USER " + selectedUser.getId() + " " + username + " " + password + " " + role + " " + balance);
                    String response = in.readLine();

                    if ("USER_UPDATED".equals(response)) {
                        showAlert("Успех", "Пользователь обновлен");
                        loadUsers();
                        clearFields();
                    } else {
                        showAlert("Ошибка", response);
                    }
                }
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
        // Вернуться к списку туров
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();
            TourController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue("USER");
        balanceField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}