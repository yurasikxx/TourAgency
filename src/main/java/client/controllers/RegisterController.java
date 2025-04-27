package client.controllers;

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

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label errorLabel;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String fullName = fullNameField.getText();
        String ageText = ageField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                fullName.isEmpty() || ageText.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("Все поля должны быть заполнены!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Пароли не совпадают!");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age <= 0) {
                errorLabel.setText("Возраст должен быть положительным числом!");
                return;
            }

            if (isNotValidFullName(fullName)) {
                errorLabel.setText("ФИО должно соответствовать формату: 'Фамилия Имя Отчество'!");
            }

            if (!email.contains("@")) {
                errorLabel.setText("Введите корректный email!");
                return;
            }

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("REGISTER " + username + " " + password + " " +
                        fullName + " " + age + " " + email + " " + phone);

                String response = in.readLine();
                if (response.equals("REGISTER_SUCCESS")) {
                    errorLabel.setText("Регистрация успешна! Теперь вы можете войти.");
                } else {
                    errorLabel.setText("Ошибка регистрации: " + response);
                }
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Возраст должен быть числом!");
        } catch (IOException e) {
            errorLabel.setText("Ошибка подключения к серверу!");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("Авторизация");
            primaryStage.show();
        } catch (IOException e) {
            errorLabel.setText("Ошибка при загрузке интерфейса авторизации!");
            e.printStackTrace();
        }
    }

    private boolean isNotValidFullName(String fullName) {
        Pattern pattern = Pattern.compile("^[А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)?\\s[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+$");
        return !pattern.matcher(fullName).matches();
    }

}