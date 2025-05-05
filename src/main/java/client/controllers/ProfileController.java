package client.controllers;

import client.MainClient;
import client.models.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

public class ProfileController {
    @FXML
    private Label usernameLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    private Stage primaryStage;
    private UserModel currentUser;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.currentUser = MainClient.getCurrentUser();
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            balanceLabel.setText(String.valueOf(currentUser.getBalance()));
            fullNameField.setText(currentUser.getFullName());
            ageField.setText(String.valueOf(currentUser.getAge()));
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
        }
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");
        successLabel.setText("");

        String newFullName = fullNameField.getText().trim();
        String newAgeText = ageField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String oldPassword = oldPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newFullName.isEmpty() || newAgeText.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            showError("Все поля должны быть заполнены");
            return;
        }

        if (isNotValidFullName(newFullName)) {
            showError("ФИО должно соответствовать формату: 'Фамилия Имя Отчество'");
            return;
        }

        int newAge;
        try {
            newAge = Integer.parseInt(newAgeText);
            if (newAge <= 0) {
                showError("Введите корректный возраст");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Возраст должен быть числом");
            return;
        }

        if (!isValidEmail(newEmail)) {
            showError("Введите корректный email");
            return;
        }

        boolean changingPassword = !newPassword.isEmpty() || !confirmPassword.isEmpty();
        if (changingPassword) {
            if (oldPassword.isEmpty()) {
                showError("Для смены пароля введите старый пароль");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showError("Новый пароль и подтверждение не совпадают");
                return;
            }
            if (newPassword.length() < 6) {
                showError("Пароль должен содержать минимум 6 символов");
                return;
            }
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String command = String.format("UPDATE_PROFILE %d %s %d %s %s %s %s %s",
                    currentUser.getId(),
                    escapeSpaces(newFullName),
                    newAge,
                    newEmail,
                    newPhone,
                    oldPassword,
                    newPassword,
                    currentUser.getUsername());

            out.println(command);
            String response = in.readLine();

            if ("PROFILE_UPDATED".equals(response)) {
                currentUser.setFullName(newFullName);
                currentUser.setAge(newAge);
                currentUser.setEmail(newEmail);
                currentUser.setPhone(newPhone);

                showSuccess();
                clearPasswordFields();
            } else {
                showError("Ошибка: " + response);
            }
        } catch (IOException e) {
            showError("Ошибка подключения к серверу");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();

            TourController tourController = loader.getController();
            tourController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("Туры");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isNotValidFullName(String fullName) {
        Pattern pattern = Pattern.compile("^[А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)?\\s[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+$");
        return !pattern.matcher(fullName).matches();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.length() >= 5;
    }

    private String escapeSpaces(String input) {
        return input.replace(" ", "~");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        successLabel.setText("");
    }

    private void showSuccess() {
        successLabel.setText("Данные успешно обновлены");
        errorLabel.setText("");
    }

    private void clearPasswordFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}