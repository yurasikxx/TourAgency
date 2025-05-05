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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Логин и пароль не могут быть пустыми!");
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("LOGIN " + username + " " + password);

            String response = in.readLine();
            System.out.println("Ответ сервера: " + response);

            if (response.startsWith("LOGIN_SUCCESS")) {
                String[] responseParts = response.split(" ");

                if (responseParts.length >= 10) {
                    UserModel user = getUserModel(responseParts, username);

                    MainClient.setCurrentUser(user);
                    loadMainView();
                } else {
                    errorLabel.setText("Ошибка: сервер вернул некорректный ответ.");
                }
            } else {
                errorLabel.setText("Неверный логин или пароль!");
            }
        } catch (IOException e) {
            errorLabel.setText("Ошибка подключения к серверу!");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            errorLabel.setText("Ошибка: некорректные данные пользователя.");
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

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("Регистрация");
            primaryStage.show();
        } catch (IOException e) {
            errorLabel.setText("Ошибка при загрузке интерфейса регистрации!");
            e.printStackTrace();
        }
    }

    private void loadMainView() {
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
            System.err.println("Ошибка при загрузке основного интерфейса: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    private static UserModel getUserModel(String[] responseParts, String username) {
        String role = responseParts[1];
        int userId = Integer.parseInt(responseParts[2]);
        double balance = Double.parseDouble(responseParts[3]);
        String fullName = responseParts[4] + " " + responseParts[5] + " " + responseParts[6];
        int age = Integer.parseInt(responseParts[7]);
        String email = responseParts[8];
        String phone = responseParts[9];

        return new UserModel(
                userId,
                username,
                role,
                balance,
                fullName,
                age,
                email,
                phone
        );
    }
}