package client.controllers;

import client.models.TourModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TourController {
    @FXML
    private TableView<TourModel> tourTable;

    @FXML
    private TableColumn<TourModel, String> nameColumn;

    @FXML
    private TableColumn<TourModel, String> descriptionColumn;

    @FXML
    private TableColumn<TourModel, Double> priceColumn;

    private Stage primaryStage;

    /**
     * Устанавливает primaryStage для контроллера.
     *
     * @param primaryStage Основное окно приложения.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadTours(); // Загружаем туры при инициализации
    }

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    /**
     * Загружает список туров с сервера.
     */
    private void loadTours() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка запроса на сервер
            out.println("GET_TOURS");

            // Получение ответа от сервера
            String response = in.readLine();
            if (response.startsWith("TOURS")) {
                String[] toursData = response.substring(6).split("\\|");
                for (String tourData : toursData) {
                    String[] fields = tourData.split(",");
                    TourModel tour = new TourModel(
                            Integer.parseInt(fields[0]),
                            fields[1],
                            fields[2],
                            Double.parseDouble(fields[3]),
                            fields[4],
                            fields[5],
                            fields[6]
                    );
                    tourTable.getItems().add(tour);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Забронировать".
     */
    @FXML
    private void handleBookTour() {
        TourModel selectedTour = tourTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Отправка запроса на сервер
                out.println("BOOK_TOUR 1 " + selectedTour.getId() + " 2023-10-01"); // Пример: userId = 1, bookingDate = 2023-10-01

                // Получение ответа от сервера
                String response = in.readLine();
                if (response.equals("BOOKING_SUCCESS")) {
                    System.out.println("Тур успешно забронирован!");
                } else {
                    System.out.println("Ошибка при бронировании тура.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Тур не выбран!");
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Обновить".
     */
    @FXML
    private void handleRefresh() {
        tourTable.getItems().clear(); // Очищаем таблицу
        loadTours(); // Загружаем туры заново
    }

    /**
     * Обрабатывает нажатие кнопки "Направления".
     */
    @FXML
    private void handleViewDestinations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/destination.fxml"));
            Parent root = loader.load();

            DestinationController destinationController = loader.getController();
            destinationController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Направления");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}