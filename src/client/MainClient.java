package client;

import client.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.services.AuthService;
import server.services.TourService;
import server.services.impl.AuthServiceImpl;
import server.services.impl.TourServiceImpl;
import server.database.DAO.UserDAO;
import server.database.DAO.TourDAO;
import server.database.DAO.impl.UserDAOImpl;
import server.database.DAO.impl.TourDAOImpl;

public class MainClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Инициализация DAO и сервисов
        UserDAO userDAO = new UserDAOImpl();
        TourDAO tourDAO = new TourDAOImpl();

        AuthService authService = new AuthServiceImpl(userDAO);
        TourService tourService = new TourServiceImpl(tourDAO); // Инициализация TourService

        // Загрузка FXML-файла для окна авторизации
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();

        // Настройка контроллера для окна авторизации
        LoginController loginController = loader.getController();
        loginController.setAuthService(authService);
        loginController.setTourService(tourService); // Передаем TourService
        loginController.setPrimaryStage(primaryStage); // Передаем Stage

        // Настройка сцены и отображение окна
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Туристическое агентство");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}