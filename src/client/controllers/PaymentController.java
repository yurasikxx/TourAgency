package client.controllers;

import client.models.PaymentModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PaymentController {
    @FXML
    private TableView<PaymentModel> paymentTable;

    @FXML
    private TableColumn<PaymentModel, Double> amountColumn;

    @FXML
    private TableColumn<PaymentModel, String> paymentDateColumn;

    @FXML
    private TableColumn<PaymentModel, String> statusColumn;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadPayments();
    }

    @FXML
    private void initialize() {
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadPayments() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_PAYMENTS 1");

            String response = in.readLine();
            if (response.startsWith("PAYMENTS")) {
                String[] paymentsData = response.substring(9).split("\\|");
                for (String paymentData : paymentsData) {
                    String[] fields = paymentData.split(",");
                    PaymentModel payment = new PaymentModel(
                            Integer.parseInt(fields[0]),
                            Double.parseDouble(fields[1]),
                            fields[2],
                            fields[3]
                    );
                    paymentTable.getItems().add(payment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        paymentTable.getItems().clear();
        loadPayments();
    }

    @FXML
    private void handleViewBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/booking.fxml"));
            Parent root = loader.load();

            BookingController bookingController = loader.getController();
            bookingController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("Бронирования");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}