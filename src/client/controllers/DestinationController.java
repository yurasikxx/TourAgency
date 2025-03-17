package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import client.models.DestinationModel;
import server.services.DestinationService;
import server.models.Destination;

import java.util.List;
import java.util.stream.Collectors;

public class DestinationController {
    @FXML
    private ListView<DestinationModel> destinationList;

    @FXML
    private Label nameLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label descriptionLabel;

    private DestinationService destinationService;
    private Stage primaryStage; // Добавляем ссылку на Stage

    public void setDestinationService(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        // Загрузка данных
        loadDestinations();

        // Обработка выбора направления
        destinationList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDestinationDetails(newValue));
    }

    private void loadDestinations() {
        // Получаем данные от сервера
        List<Destination> serverDestinations = destinationService.getAllDestinations();

        // Преобразуем серверные модели в клиентские
        List<DestinationModel> destinations = serverDestinations.stream()
                .map(DestinationModel::fromServerModel)
                .collect(Collectors.toList());

        // Загружаем данные в список
        destinationList.getItems().setAll(destinations);
    }

    private void showDestinationDetails(DestinationModel destination) {
        if (destination != null) {
            nameLabel.setText(destination.getName());
            countryLabel.setText(destination.getCountry());
            descriptionLabel.setText(destination.getDescription());
        } else {
            nameLabel.setText("");
            countryLabel.setText("");
            descriptionLabel.setText("");
        }
    }

    @FXML
    private void handleViewTours() {
        loadTourView(); // Переход к окну туров
    }

    private void loadTourView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();

            TourController tourController = loader.getController();
            tourController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Туры");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}