package client.controllers;

import client.models.DestinationModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DestinationController {
    @FXML
    private ListView<DestinationModel> destinationList;

    @FXML
    private Label nameLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label descriptionLabel;

    private Stage primaryStage;

    /**
     * Устанавливает primaryStage для контроллера.
     *
     * @param primaryStage Основное окно приложения.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadDestinations(); // Загружаем направления при инициализации
    }

    @FXML
    private void initialize() {
        // Обработка выбора направления
        destinationList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDestinationDetails(newValue)
        );
    }

    /**
     * Загружает список направлений с сервера.
     */
    private void loadDestinations() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка запроса на сервер
            out.println("GET_DESTINATIONS");

            // Получение ответа от сервера
            String response = in.readLine();
            if (response.startsWith("DESTINATIONS")) {
                String[] destinationsData = response.substring(12).split("\\|");
                for (String destinationData : destinationsData) {
                    String[] fields = destinationData.split(",");
                    if (fields.length == 4) {
                        int id = Integer.parseInt(fields[0].trim());
                        String name = fields[1];
                        String country = fields[2];
                        String description = fields[3];

                        // Создаем объект DestinationModel
                        DestinationModel destination = new DestinationModel(id, name, country, description);
                        destinationList.getItems().add(destination);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отображает детали выбранного направления.
     *
     * @param destination Выбранное направление.
     */
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

    /**
     * Обрабатывает нажатие кнопки "Туры".
     */
    @FXML
    private void handleViewTours() {
        DestinationModel selectedDestination = destinationList.getSelectionModel().getSelectedItem();
        if (selectedDestination != null) {
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Отправка запроса на сервер
                out.println("GET_TOURS_BY_DESTINATION " + selectedDestination.getId());

                // Получение ответа от сервера
                String response = in.readLine();
                if (response.startsWith("TOURS")) {
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
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Направление не выбрано!");
        }
    }
}