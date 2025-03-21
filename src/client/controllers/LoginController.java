package client.controllers;

import client.MainClient;
import client.models.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

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
     * Обрабатывает нажатие кнопки "Войти".
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Проверка на пустые поля
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Логин и пароль не могут быть пустыми!");
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка запроса на сервер
            out.println("LOGIN " + username + " " + password);

            // Получение ответа от сервера
            String response = in.readLine();
            System.out.println("Ответ сервера: " + response); // Вывод для отладки

            if (response.startsWith("LOGIN_SUCCESS")) {
                // Успешная авторизация
                String[] responseParts = response.split(" ");
                if (responseParts.length >= 3) { // Проверяем, что ответ содержит роль и ID
                    String role = responseParts[1]; // Роль пользователя
                    int userId = Integer.parseInt(responseParts[2]); // ID пользователя

                    // Создаем объект UserModel для текущего пользователя
                    UserModel user = new UserModel(userId, username, role);
                    MainClient.setCurrentUser(user); // Сохраняем текущего пользователя

                    loadMainView(); // Загружаем основной интерфейс
                } else {
                    errorLabel.setText("Ошибка: сервер вернул некорректный ответ.");
                }
            } else {
                // Ошибка авторизации
                errorLabel.setText("Неверный логин или пароль!");
            }
        } catch (IOException e) {
            errorLabel.setText("Ошибка подключения к серверу!");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            errorLabel.setText("Ошибка: некорректный ID пользователя.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register.fxml"));
            Parent root = loader.load();

            RegisterController registerController = loader.getController();
            registerController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Регистрация");
            primaryStage.show();
        } catch (IOException e) {
            errorLabel.setText("Ошибка при загрузке интерфейса регистрации!");
            e.printStackTrace();
        }
    }

    /**
     * Загружает основной интерфейс приложения.
     */
    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();

            // Получаем контроллер для основного интерфейса
            TourController tourController = loader.getController();
            tourController.setPrimaryStage(primaryStage);

            // Устанавливаем новую сцену
            Scene scene = new Scene(root);

            // Устанавливаем размер сцены
            primaryStage.setScene(scene);
            primaryStage.setWidth(1280); // Ширина окна
            primaryStage.setHeight(720); // Высота окна
            primaryStage.setTitle("Туры");
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке основного интерфейса: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) { // Проверяем, что нажата клавиша Enter
            handleLogin(); // Вызываем метод для входа
        }
    }
}