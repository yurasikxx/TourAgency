package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import server.services.AuthService;
import server.services.TourService;
import server.models.User;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private AuthService authService;
    private TourService tourService; // Добавляем TourService
    private Stage primaryStage;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setTourService(TourService tourService) {
        this.tourService = tourService;
    }

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

        User user = authService.authenticate(username, password);
        if (user != null) {
            loadMainView();
        } else {
            errorLabel.setText("Неверный логин или пароль!");
        }
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();

            // Получаем контроллер для экрана туров
            TourController tourController = loader.getController();
            tourController.setTourService(tourService); // Передаем TourService
            tourController.setPrimaryStage(primaryStage); // Передаем Stage

            // Устанавливаем новую сцену
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Туры");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка при загрузке основного интерфейса!");
        }
    }
}