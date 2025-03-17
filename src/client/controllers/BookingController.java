package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import client.models.BookingModel;
import server.services.BookingService;
import server.models.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingController {
    @FXML
    private TableView<BookingModel> bookingTable;

    @FXML
    private TableColumn<BookingModel, String> tourNameColumn;

    @FXML
    private TableColumn<BookingModel, String> bookingDateColumn;

    @FXML
    private TableColumn<BookingModel, String> statusColumn;

    private BookingService bookingService;
    private Stage primaryStage;

    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        tourNameColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Загрузка данных
        loadBookings();
    }

    private void loadBookings() {
        // Получаем данные от сервера
        List<Booking> serverBookings = bookingService.getBookingsByUserId(1); // Пример: ID пользователя = 1

        // Преобразуем серверные модели в клиентские
        List<BookingModel> bookings = serverBookings.stream()
                .map(BookingModel::fromServerModel)
                .collect(Collectors.toList());

        // Загружаем данные в таблицу
        bookingTable.getItems().setAll(bookings);
    }

    @FXML
    private void handleCancelBooking() {
        BookingModel selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            System.out.println("Отменено бронирование: " + selectedBooking.getTourName());
        } else {
            System.out.println("Бронирование не выбрано!");
        }
    }

    @FXML
    private void handleViewPayments() {
        loadPaymentView();
    }

    @FXML
    private void handleRefresh() {
        loadBookings(); // Обновляем данные в таблице
        System.out.println("Данные обновлены.");
    }

    private void loadPaymentView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/payment.fxml"));
            Parent root = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Платежи");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}