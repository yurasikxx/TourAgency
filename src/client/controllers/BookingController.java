package client.controllers;

import client.MainClient;
import client.models.BookingModel;
import client.models.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;

public class BookingController {
    @FXML
    private TableView<BookingModel> bookingTable;

    @FXML
    private TableColumn<BookingModel, String> tourNameColumn;

    @FXML
    private TableColumn<BookingModel, String> bookingDateColumn;

    @FXML
    private TableColumn<BookingModel, String> priceColumn;

    @FXML
    private TableColumn<BookingModel, String> statusColumn;

    @FXML
    private Label balanceLabel;

    private Stage primaryStage;

    /**
     * Устанавливает primaryStage для контроллера.
     *
     * @param primaryStage Основное окно приложения.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadBookings();
    }

    @FXML
    private void initialize() {
        tourNameColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadBookings() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            UserModel currentUser = MainClient.getCurrentUser();
            if (currentUser == null) {
                System.out.println("Пользователь не авторизован!");
                return;
            }

            out.println("GET_BOOKINGS " + currentUser.getId());

            String response = in.readLine();
            System.out.println("Ответ сервера: " + response);

            if (response.startsWith("BOOKINGS")) {
                String[] bookingsData = response.substring(9).split("\\|");
                for (String bookingData : bookingsData) {
                    if (bookingData.isEmpty()) {
                        continue;
                    }

                    String[] fields = bookingData.split(",");
                    if (fields.length == 5) {
                        int id = Integer.parseInt(fields[0]);
                        String tourName = fields[1];
                        String bookingDate = fields[2];
                        double price = Double.parseDouble(fields[3]);
                        String status = fields[4];

                        BookingModel booking = new BookingModel(id, tourName, bookingDate, price, status);
                        bookingTable.getItems().add(booking);
                    } else {
                        System.err.println("Ошибка: некорректные данные бронирования: " + bookingData);
                    }
                }
            }

            double balance = getUserBalance();
            System.out.println("Баланс пользователя: " + balance);
            balanceLabel.setText(String.format("%.2f", balance));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelBooking() {
        BookingModel selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            if (!selectedBooking.getStatus().equals("В ожидании")) {
                String message = selectedBooking.getStatus().equals("Подтверждено") ?
                        "Этот тур уже оплачен и не может быть отменён" : "Этот тур уже отменён";
                showAlert("Ошибка", message);
                return;
            }
            if (selectedBooking.getStatus().equals("confirmed")) {
                showAlert("Информация", "Этот тур уже оплачен и не может быть отменён");
                return;
            }
            if (selectedBooking.getStatus().equals("cancelled")) {
                showAlert("Ошибка", "Этот тур уже отменён");
                return;
            }

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                UserModel currentUser = MainClient.getCurrentUser();
                if (currentUser == null) {
                    System.out.println("Пользователь не авторизован!");
                    return;
                }

                out.println("CANCEL_BOOKING " + selectedBooking.getId() + " " + currentUser.getId());

                String response = in.readLine();
                if (response.equals("CANCEL_SUCCESS")) {
                    System.out.println("Бронирование успешно отменено!");

                    double balance = getUserBalance();
                    balanceLabel.setText(String.format("%.2f", balance));

                    handleRefresh();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Успешно");
                    alert.setHeaderText(null);
                    alert.setContentText("Бронирование успешно отменено.");
                    alert.showAndWait();
                } else {
                    System.out.println("Ошибка при отмене бронирования: " + response);

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.setContentText("Не удалось отменить бронирование: " + response);
                    alert.showAndWait();
                }
            } catch (IOException e) {
                showAlert("Ошибка", "Ошибка подключения к серверу.");
            }
        } else {
            System.out.println("Бронирование не выбрано!");
            showAlert("Предупреждение", "Выберите бронирование для отмены.");
        }
    }

    @FXML
    private void handleRefresh() {
        bookingTable.getItems().clear();
        loadBookings();
    }

    @FXML
    private void handleBack() {
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

    @FXML
    private void handlePay() {
        BookingModel selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {

            if (!selectedBooking.getStatus().equals("В ожидании")) {
                String message = selectedBooking.getStatus().equals("Подтверждено") ?
                        "Этот тур уже оплачен" : "Нельзя оплатить отменённый тур";
                showAlert("Ошибка", message);
                return;
            }

            if (selectedBooking.getStatus().equals("confirmed")) {
                showAlert("Информация", "Этот тур уже оплачен");
                return;
            }
            if (selectedBooking.getStatus().equals("cancelled")) {
                showAlert("Ошибка", "Нельзя оплатить отменённый тур");
                return;
            }

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String currentDate = LocalDate.now().toString();

                UserModel currentUser = MainClient.getCurrentUser();
                if (currentUser == null) {
                    System.out.println("Пользователь не авторизован!");
                    return;
                }

                out.println("MAKE_PAYMENT " + selectedBooking.getId() + " " + selectedBooking.getPrice() + " " + currentDate + " " + currentUser.getId());

                String response = in.readLine();
                if (response.equals("PAYMENT_SUCCESS")) {
                    System.out.println("Оплата прошла успешно!");

                    double balance = getUserBalance();
                    balanceLabel.setText(String.format("%.2f", balance));

                    handleRefresh();
                } else {
                    System.out.println("Ошибка при оплате: " + response);

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.setContentText("Не удалось выполнить оплату: " + response);
                    alert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка подключения к серверу.");
                alert.showAndWait();
            }
        } else {
            System.out.println("Бронирование не выбрано!");
            showAlert("Предупреждение", "Выберите бронирование для оплаты");
        }
    }

    private double getUserBalance() {
        UserModel currentUser = MainClient.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Пользователь не авторизован!");
            return 0.0;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_BALANCE " + currentUser.getId());
            System.out.println("Отправлен запрос на получение баланса для пользователя " + currentUser.getId());

            String response = in.readLine();
            System.out.println("Ответ сервера: " + response);

            if (response.startsWith("BALANCE")) {
                return Double.parseDouble(response.substring(8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.centerOnScreen();

        alert.showAndWait();
    }
}