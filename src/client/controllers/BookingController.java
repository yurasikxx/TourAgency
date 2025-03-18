package client.controllers;

import client.models.BookingModel;
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

public class BookingController {
    @FXML
    private TableView<BookingModel> bookingTable;

    @FXML
    private TableColumn<BookingModel, String> tourNameColumn;

    @FXML
    private TableColumn<BookingModel, String> bookingDateColumn;

    @FXML
    private TableColumn<BookingModel, String> statusColumn;

    private Stage primaryStage;

    /**
     * Устанавливает primaryStage для контроллера.
     *
     * @param primaryStage Основное окно приложения.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadBookings(); // Загружаем бронирования при инициализации
    }

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        tourNameColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    /**
     * Загружает список бронирований с сервера.
     */
    private void loadBookings() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка запроса на сервер
            out.println("GET_BOOKINGS 1"); // Пример: userId = 1

            // Получение ответа от сервера
            String response = in.readLine();
            if (response.startsWith("BOOKINGS")) {
                String[] bookingsData = response.substring(9).split("\\|");
                for (String bookingData : bookingsData) {
                    String[] fields = bookingData.split(",");
                    BookingModel booking = new BookingModel(
                            Integer.parseInt(fields[0]),
                            fields[1],
                            fields[2],
                            fields[3]
                    );
                    bookingTable.getItems().add(booking);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Отменить бронь".
     */
    @FXML
    private void handleCancelBooking() {
        BookingModel selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Отправка запроса на сервер
                out.println("CANCEL_BOOKING " + selectedBooking.getId());

                // Получение ответа от сервера
                String response = in.readLine();
                if (response.equals("CANCEL_SUCCESS")) {
                    System.out.println("Бронирование успешно отменено!");
                    loadBookings(); // Обновляем список бронирований
                } else {
                    System.out.println("Ошибка при отмене бронирования.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Бронирование не выбрано!");
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Обновить".
     */
    @FXML
    private void handleRefresh() {
        bookingTable.getItems().clear(); // Очищаем таблицу
        loadBookings(); // Загружаем бронирования заново
    }

    /**
     * Обрабатывает нажатие кнопки "Платежи".
     */
    @FXML
    private void handleViewPayments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/payment.fxml"));
            Parent root = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Платежи");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}