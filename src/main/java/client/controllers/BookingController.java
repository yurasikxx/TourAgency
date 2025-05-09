package client.controllers;

import client.MainClient;
import client.models.BookingModel;
import client.models.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import server.utils.DocumentGenerator;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

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
    private TableColumn<BookingModel, String> adultsColumn;

    @FXML
    private TableColumn<BookingModel, String> childrenColumn;

    @FXML
    private TableColumn<BookingModel, String> mealOptionColumn;

    @FXML
    private TableColumn<BookingModel, String> additionalServicesColumn;

    @FXML
    private TableColumn<BookingModel, String> totalPriceColumn;

    @FXML
    private TableColumn<BookingModel, String> paidAmountColumn;

    @FXML
    private TableColumn<BookingModel, String> paymentStatusColumn;

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
        adultsColumn.setCellValueFactory(new PropertyValueFactory<>("adults"));
        childrenColumn.setCellValueFactory(new PropertyValueFactory<>("children"));
        mealOptionColumn.setCellValueFactory(new PropertyValueFactory<>("mealOption"));
        additionalServicesColumn.setCellValueFactory(new PropertyValueFactory<>("additionalServices"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        paidAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
    }

    private void loadBookings() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            UserModel currentUser = MainClient.getCurrentUser();
            if (currentUser == null) return;

            balanceLabel.setText(String.format("%.2f", getUserBalance()));
            out.println("GET_BOOKINGS " + currentUser.getId());

            String response = in.readLine();
            if (response.startsWith("BOOKINGS")) {
                String[] bookingsData = response.substring(9).split("\\|");
                for (String bookingData : bookingsData) {
                    if (bookingData.isEmpty()) continue;

                    String[] fields = bookingData.split(",");
                    if (fields.length == 13) {
                        BookingModel booking = getBookingModel(fields);

                        if (shouldHighlightBooking(booking)) {
                            booking.setPaymentStatus("Требуется оплата!");
                            bookingTable.setRowFactory(tv -> new TableRow<BookingModel>() {
                                @Override
                                protected void updateItem(BookingModel item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (item == null || empty) {
                                        setStyle("");
                                    } else {
                                        if (shouldHighlightBooking(item)) {
                                            setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: bold;");
                                        } else {
                                            setStyle("");
                                        }
                                    }
                                }
                            });
                        }

                        bookingTable.getItems().add(booking);
                    }
                }
            }
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

                out.println("MAKE_PAYMENT " + selectedBooking.getId() + " " + selectedBooking.getTotalPrice() + " " + currentDate + " " + currentUser.getId());

                String response = in.readLine();
                if (response.equals("PAYMENT_SUCCESS")) {
                    System.out.println("Оплата прошла успешно!");

                    double balance = getUserBalance();
                    balanceLabel.setText(String.format("%.2f", balance));

                    handleRefresh();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Успешно");
                    alert.setHeaderText(null);
                    alert.setContentText("Оплата прошла успешно!");
                    alert.showAndWait();
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

    @FXML
    private void handleGenerateContract() {
        BookingModel selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            if (!selectedBooking.getStatus().equals("Подтверждено")) {
                showAlert("Ошибка", "Договор можно сгенерировать только для подтверждённых бронирований");
                return;
            }

            UserModel currentUser = MainClient.getCurrentUser();
            if (currentUser == null) {
                showAlert("Ошибка", "Пользователь не авторизован");
                return;
            }

            try {
                String filePath = DocumentGenerator.generateContract(selectedBooking, currentUser);
                showAlert("Успех", "Договор успешно сгенерирован и сохранён по пути: " + filePath);

                // Открываем документ в системе
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(filePath));
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось сгенерировать договор: " + e.getMessage());
            }
        } else {
            showAlert("Предупреждение", "Выберите бронирование для генерации договора");
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

    @FXML
    private void handlePartialPayment() {
        BookingModel selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            if (!selectedBooking.getStatus().equals("В ожидании")) {
                showAlert("Ошибка", "Частичная оплата доступна только для туров в статусе 'В ожидании'");
                return;
            }

            ChoiceDialog<Double> dialog = new ChoiceDialog<>(10.0, Arrays.asList(10.0, 30.0, 60.0, 100.0));
            dialog.setTitle("Частичная оплата");
            dialog.setHeaderText("Выберите процент оплаты");
            dialog.setContentText("Процент:");

            Optional<Double> result = dialog.showAndWait();
            result.ifPresent(percent -> {
                double amountToPay = selectedBooking.getTotalPrice() * (percent / 100.0);

                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String currentDate = LocalDate.now().toString();
                    UserModel currentUser = MainClient.getCurrentUser();

                    out.println("MAKE_PARTIAL_PAYMENT " + selectedBooking.getId() + " " +
                            amountToPay + " " + currentDate + " " + currentUser.getId());

                    String response = in.readLine();
                    if (response.equals("PARTIAL_PAYMENT_SUCCESS")) {
                        showAlert("Успешно", String.format("Оплачено %.2f%% от суммы бронирования", percent));
                        handleRefresh();
                    } else {
                        showAlert("Ошибка", "Не удалось выполнить оплату: " + response);
                    }
                } catch (IOException e) {
                    showAlert("Ошибка", "Ошибка подключения к серверу");
                }
            });
        } else {
            showAlert("Предупреждение", "Выберите бронирование для оплаты");
        }
    }

    private static BookingModel getBookingModel(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String tourName = fields[1];
        String bookingDate = fields[2];
        double price = Double.parseDouble(fields[3]);
        String status = fields[4];
        int adults = Integer.parseInt(fields[5]);
        int children = Integer.parseInt(fields[6]);
        String mealOption = fields[7];
        String additionalServices = fields[8];
        double totalPrice = Double.parseDouble(fields[9]);
        double paidAmount = Double.parseDouble(fields[10]);
        String paymentStatus = fields[11];
        String tourStartDate = fields[12];

        return new BookingModel(id, tourName, bookingDate, status, price, adults, children, mealOption, additionalServices, totalPrice, paidAmount, paymentStatus, tourStartDate);
    }

    private boolean shouldHighlightBooking(BookingModel booking) {
        if (!booking.getStatus().equals("В ожидании")) return false;

        try {
            LocalDate startDate = LocalDate.parse(booking.getTourStartDate());
            LocalDate now = LocalDate.now();
            long daysUntilTour = ChronoUnit.DAYS.between(now, startDate);

            return daysUntilTour <= 14 && booking.getPaidAmount() < booking.getTotalPrice();
        } catch (Exception e) {
            return false;
        }
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