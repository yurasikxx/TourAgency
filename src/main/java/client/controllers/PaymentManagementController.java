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

public class PaymentManagementController {
    @FXML
    private TableView<PaymentModel> paymentsTable;

    @FXML
    private TableColumn<PaymentModel, Integer> idColumn;

    @FXML
    private TableColumn<PaymentModel, Integer> bookingIdColumn;

    @FXML
    private TableColumn<PaymentModel, Integer> userIdColumn;

    @FXML
    private TableColumn<PaymentModel, Double> amountColumn;

    @FXML
    private TableColumn<PaymentModel, String> paymentDateColumn;

    @FXML
    private TableColumn<PaymentModel, String> statusColumn;

    @FXML
    private TableColumn<PaymentModel, String> tourColumn;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeTable();
        loadPayments();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        tourColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
    }

    private void loadPayments() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_ALL_PAYMENTS_ADMIN");

            String response = in.readLine();
            if (response.startsWith("PAYMENTS_ADMIN")) {
                paymentsTable.getItems().clear();
                String[] paymentsData = response.substring(15).split("\\|");
                for (String paymentData : paymentsData) {
                    if (paymentData.isEmpty()) continue;

                    String[] fields = paymentData.split(",");
                    if (fields.length == 7) {
                        PaymentModel payment = new PaymentModel(
                                Integer.parseInt(fields[0]),
                                Integer.parseInt(fields[1]),
                                Integer.parseInt(fields[2]),
                                Double.parseDouble(fields[3]),
                                fields[4],
                                fields[5],
                                fields[6]
                        );
                        paymentsTable.getItems().add(payment);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadPayments();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();

            TourController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Туры");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}