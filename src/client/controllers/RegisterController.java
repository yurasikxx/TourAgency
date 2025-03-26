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

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    private Stage primaryStage;

    /**
     * Устанавливает primaryStage для контроллера.
     *
     * @param primaryStage Основное окно приложения.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Обрабатывает нажатие кнопки "Зарегистрироваться".
     */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Проверка на пустые поля
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Все поля должны быть заполнены!");
            return;
        }

        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Пароли не совпадают!");
            return;
        }

        // Отправка запроса на сервер
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка команды регистрации
            out.println("REGISTER " + username + " " + password);

            // Получение ответа от сервера
            String response = in.readLine();
            if (response.equals("REGISTER_SUCCESS")) {
                errorLabel.setText("Регистрация успешна! Теперь вы можете войти.");
            } else {
                errorLabel.setText("Ошибка регистрации: " + response);
            }
        } catch (IOException e) {
            errorLabel.setText("Ошибка подключения к серверу!");
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Назад".
     */
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
}