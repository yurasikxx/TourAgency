package client;

import client.controllers.LoginController;
import client.models.UserModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {

    private static UserModel currentUser;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Туристическое агентство - Авторизация");
        primaryStage.show();
    }

    public static void setCurrentUser(UserModel user) {
        currentUser = user;
    }

    public static UserModel getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        launch(args);
    }
}