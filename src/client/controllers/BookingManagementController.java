package client.controllers;

import client.models.BookingModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BookingManagementController {
    @FXML
    private TableView<BookingModel> bookingsTable;
    @FXML
    private TableColumn<BookingModel, Integer> idColumn;
    @FXML
    private TableColumn<BookingModel, String> userColumn;
    @FXML
    private TableColumn<BookingModel, String> tourColumn;
    @FXML
    private TableColumn<BookingModel, String> bookingDateColumn;
    @FXML
    private TableColumn<BookingModel, String> statusColumn;
    @FXML
    private TableColumn<BookingModel, Double> amountColumn;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeTable();
        loadBookings();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        tourColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void loadBookings() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_ALL_BOOKINGS_ADMIN");

            String response = in.readLine();
            if (response.startsWith("BOOKINGS_ADMIN")) {
                bookingsTable.getItems().clear();
                String[] bookingsData = response.substring(15).split("\\|");
                for (String bookingData : bookingsData) {
                    if (bookingData.isEmpty()) continue;

                    String[] fields = bookingData.split(",");
                    if (fields.length == 6) {
                        BookingModel booking = new BookingModel(
                                Integer.parseInt(fields[0]),
                                fields[1],
                                fields[2],
                                fields[3],
                                fields[4],
                                Double.parseDouble(fields[5])
                        );
                        bookingsTable.getItems().add(booking);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert();
        }
    }

    @FXML
    private void handleRefresh() {
        loadBookings();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();
            TourController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(new Scene(root));
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText("Не удалось загрузить бронирования");
        alert.showAndWait();
    }
}