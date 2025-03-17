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
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        System.out.println("Инициализация TourController...");

        if (nameColumn == null) {
            System.out.println("nameColumn не инициализирован!");
        }
        if (descriptionColumn == null) {
            System.out.println("descriptionColumn не инициализирован!");
        }
        if (priceColumn == null) {
            System.out.println("priceColumn не инициализирован!");
        }
        if (tourTable == null) {
            System.out.println("tourTable не инициализирован!");
        }
        if (tourService == null) {
            System.out.println("tourService не инициализирован!");
        }

        // Настройка колонок таблицы
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Загрузка данных
        if (tourService != null) {
            loadTours();
        } else {
            System.out.println("Ошибка: TourService не инициализирован!");
        }
    }

    private void loadTours() {
        // Получаем данные от сервера
        List<Tour> serverTours = tourService.getAllTours();

        // Преобразуем серверные модели в клиентские
        List<TourModel> tours = serverTours.stream()
                .map(TourModel::fromServerModel)
                .collect(Collectors.toList());

        // Загружаем данные в таблицу
        tourTable.getItems().setAll(tours);
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