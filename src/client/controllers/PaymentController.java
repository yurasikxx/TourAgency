package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import client.models.PaymentModel;
import server.services.PaymentService;
import server.models.Payment;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentController {
    @FXML
    private TableView<PaymentModel> paymentTable;

    @FXML
    private TableColumn<PaymentModel, Double> amountColumn;

    @FXML
    private TableColumn<PaymentModel, String> paymentDateColumn;

    @FXML
    private TableColumn<PaymentModel, String> statusColumn;

    private PaymentService paymentService;
    private Stage primaryStage;

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Загрузка данных
        loadPayments();
    }

    private void loadPayments() {
        // Получаем данные от сервера
        List<Payment> serverPayments = paymentService.getPaymentsByBookingId(1); // Пример: ID бронирования = 1

        // Преобразуем серверные модели в клиентские
        List<PaymentModel> payments = serverPayments.stream()
                .map(PaymentModel::fromServerModel)
                .collect(Collectors.toList());

        // Загружаем данные в таблицу
        paymentTable.getItems().setAll(payments);
    }

    @FXML
    private void handleViewBookings() {
        loadBookingView();
    }

    @FXML
    private void handleRefresh() {
        loadPayments(); // Обновляем данные в таблице
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
}