package client;

import client.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Основной класс клиентского приложения.
 * Запускает JavaFX-приложение и загружает начальный интерфейс (форму авторизации).
 */
public class MainClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загрузка FXML-файла для окна авторизации
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();

        // Настройка контроллера для окна авторизации
        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);

        // Настройка сцены и отображение окна
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Туристическое агентство - Авторизация");
        primaryStage.setScene(scene);
        primaryStage.show();
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