package client;

import client.controllers.LoginController;
import client.models.UserModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Основной класс клиентского приложения.
 */
public class MainClient extends Application {

    // Текущий авторизированный пользователь
    private static UserModel currentUser;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загрузка FXML-файла для окна авторизации
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();

        // Настройка контроллера для окна авторизации
        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);

        // Настройка сцены и отображение окна
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Туристическое агентство - Авторизация");
        primaryStage.show();
    }

    /**
     * Устанавливает текущего пользователя.
     *
     * @param user Текущий пользователь.
     */
    public static void setCurrentUser(UserModel user) {
        currentUser = user;
    }

    /**
     * Возвращает текущего пользователя.
     *
     * @return Текущий пользователь.
     */
    public static UserModel getCurrentUser() {
        return currentUser;
    }

    /**
     * Точка входа в приложение.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        launch(args);
    }
}