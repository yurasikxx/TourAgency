package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import client.models.TourModel;
import javafx.stage.Stage;
import server.services.TourService;
import server.models.Tour;

import java.util.List;
import java.util.stream.Collectors;

public class TourController {
    @FXML
    private TableView<TourModel> tourTable;

    @FXML
    private TableColumn<TourModel, String> nameColumn;

    @FXML
    private TableColumn<TourModel, String> descriptionColumn;

    @FXML
    private TableColumn<TourModel, Double> priceColumn;

    private TourService tourService;
    private Stage primaryStage;

    public void setTourService(TourService tourService) {
        this.tourService = tourService;
        System.out.println("TourService установлен в TourController.");
        loadTours(); // Загружаем туры после установки tourService
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        System.out.println("Инициализация TourController...");

        // Настройка колонок таблицы
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Загрузка данных (если tourService уже инициализирован)
        if (tourService != null) {
            loadTours();
        } else {
            System.out.println("Ошибка: TourService не инициализирован!");
        }
    }

    private void loadTours() {
        if (tourService != null) {
            List<Tour> serverTours = tourService.getAllTours();
            List<TourModel> tours = serverTours.stream()
                    .map(TourModel::fromServerModel)
                    .collect(Collectors.toList());

            tourTable.getItems().setAll(tours);
            System.out.println("Туры загружены в таблицу.");
        } else {
            System.out.println("Ошибка: TourService не инициализирован!");
        }
    }

    @FXML
    private void handleBookTour() {
        TourModel selectedTour = tourTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            System.out.println("Выбран тур: " + selectedTour.getName());
            loadBookingView();
        } else {
            System.out.println("Тур не выбран!");
        }
    }

    @FXML
    private void handleViewDestinations() {
        loadDestinationView();
    }

    @FXML
    private void handleRefresh() {
        loadTours(); // Обновляем данные в таблице
        System.out.println("Данные обновлены.");
    }

    private void loadBookingView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/booking.fxml"));
            Parent root = loader.load();

            BookingController bookingController = loader.getController();
            bookingController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Бронирования");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDestinationView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/destination.fxml"));
            Parent root = loader.load();

            DestinationController destinationController = loader.getController();
            destinationController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Направления");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}