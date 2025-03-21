package client.controllers;

import client.models.PaymentModel;
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

    /**
     * Устанавливает primaryStage для контроллера.
     *
     * @param primaryStage Основное окно приложения.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadPayments(); // Загружаем платежи при инициализации
    }

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    /**
     * Загружает список платежей с сервера.
     */
    private void loadPayments() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка запроса на сервер
            out.println("GET_PAYMENTS 1"); // Пример: bookingId = 1

            // Получение ответа от сервера
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

    /**
     * Обрабатывает нажатие кнопки "Обновить".
     */
    @FXML
    private void handleRefresh() {
        paymentTable.getItems().clear(); // Очищаем таблицу
        loadPayments(); // Загружаем платежи заново
    }

    /**
     * Обрабатывает нажатие кнопки "Назад".
     */
    @FXML
    private void handleViewBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/booking.fxml"));
            Parent root = loader.load();

            BookingController bookingController = loader.getController();
            bookingController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Бронирования");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}