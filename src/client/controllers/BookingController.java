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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
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
        loadBookings(); // Загружаем бронирования при инициализации
    }

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        tourNameColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    /**
     * Загружает список бронирований с сервера.
     */
    private void loadBookings() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Получаем ID текущего пользователя
            UserModel currentUser = MainClient.getCurrentUser();
            if (currentUser == null) {
                System.out.println("Пользователь не авторизован!");
                return;
            }

            // Отправка запроса на сервер с ID текущего пользователя
            out.println("GET_BOOKINGS " + currentUser.getId());

            // Получение ответа от сервера
            String response = in.readLine();
            System.out.println("Ответ сервера: " + response); // Вывод ответа сервера

            if (response.startsWith("BOOKINGS")) {
                // Убираем префикс "BOOKINGS" и разбиваем данные по символу "|"
                String[] bookingsData = response.substring(9).split("\\|");
                for (String bookingData : bookingsData) {
                    if (bookingData.isEmpty()) {
                        continue; // Пропускаем пустые строки
                    }

                    // Разбиваем данные бронирования по запятым
                    String[] fields = bookingData.split(",");
                    if (fields.length == 5) { // Проверяем, что все поля присутствуют
                        int id = Integer.parseInt(fields[0]); // ID бронирования
                        String tourName = fields[1]; // Название тура
                        String bookingDate = fields[2]; // Дата бронирования
                        double price = Double.parseDouble(fields[3]); // Стоимость тура
                        String status = fields[4]; // Статус

                        // Создаем объект BookingModel
                        BookingModel booking = new BookingModel(id, tourName, bookingDate, price, status);
                        bookingTable.getItems().add(booking);
                    } else {
                        System.err.println("Ошибка: некорректные данные бронирования: " + bookingData);
                    }
                }
            }

            // Обновляем баланс пользователя
            double balance = getUserBalance();
            System.out.println("Баланс пользователя: " + balance); // Вывод баланса для отладки
            balanceLabel.setText(String.format("%.2f", balance));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает стоимость тура по его ID.
     *
     * @param tourId ID тура.
     * @return Стоимость тура.
     */
    private double getTourPrice(int tourId) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка запроса на сервер
            out.println("GET_TOUR_PRICE " + tourId);

            // Получение ответа от сервера
            String response = in.readLine();
            if (response.startsWith("TOUR_PRICE")) {
                return Double.parseDouble(response.substring(11)); // Пример ответа: TOUR_PRICE 500.0
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0; // Возвращаем 0, если не удалось получить стоимость
    }

    /**
     * Обрабатывает нажатие кнопки "Отменить бронь".
     */
    @FXML
    private void handleCancelBooking() {
        BookingModel selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            // Проверяем статус бронирования
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

                // Получаем ID текущего пользователя
                UserModel currentUser = MainClient.getCurrentUser();
                if (currentUser == null) {
                    System.out.println("Пользователь не авторизован!");
                    return;
                }

                // Отправка запроса на сервер с ID текущего пользователя
                out.println("CANCEL_BOOKING " + selectedBooking.getId() + " " + currentUser.getId());

                // Получение ответа от сервера
                String response = in.readLine();
                if (response.equals("CANCEL_SUCCESS")) {
                    System.out.println("Бронирование успешно отменено!");

                    // Обновляем баланс пользователя
                    double balance = getUserBalance();
                    balanceLabel.setText(String.format("%.2f", balance));

                    // Обновляем список бронирований
                    handleRefresh(); // Добавлено обновление данных

                    // Показываем уведомление об успешной отмене
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Успешно");
                    alert.setHeaderText(null);
                    alert.setContentText("Бронирование успешно отменено.");
                    alert.showAndWait();
                } else {
                    System.out.println("Ошибка при отмене бронирования: " + response);

                    // Показываем уведомление об ошибке
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

    /**
     * Обрабатывает нажатие кнопки "Обновить".
     */
    @FXML
    private void handleRefresh() {
        bookingTable.getItems().clear(); // Очищаем таблицу
        loadBookings(); // Загружаем бронирования заново
    }

    /**
     * Обрабатывает нажатие кнопки "Назад".
     */
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
    private void handleViewPayments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/payment.fxml"));
            Parent root = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);

            // Устанавливаем размер сцены
            primaryStage.setScene(scene);
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("Платежи");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePay() {
        BookingModel selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            // Проверяем статус бронирования
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

                // Получаем текущую дату
                String currentDate = LocalDate.now().toString(); // Формат: "2023-10-25"

                // Получаем ID текущего пользователя
                UserModel currentUser = MainClient.getCurrentUser();
                if (currentUser == null) {
                    System.out.println("Пользователь не авторизован!");
                    return;
                }

                // Отправка запроса на сервер с ID текущего пользователя
                out.println("MAKE_PAYMENT " + selectedBooking.getId() + " " + selectedBooking.getPrice() + " " + currentDate + " " + currentUser.getId());

                // Получение ответа от сервера
                String response = in.readLine();
                if (response.equals("PAYMENT_SUCCESS")) {
                    System.out.println("Оплата прошла успешно!");

                    // Обновляем баланс пользователя
                    double balance = getUserBalance();
                    balanceLabel.setText(String.format("%.2f", balance));

                    // Обновляем список бронирований
                    handleRefresh(); // Добавлено обновление данных
                } else {
                    System.out.println("Ошибка при оплате: " + response);

                    // Показываем уведомление об ошибке
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.setContentText("Не удалось выполнить оплату: " + response);
                    alert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();

                // Показываем уведомление об ошибке подключения
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
        UserModel currentUser = MainClient.getCurrentUser(); // Получаем текущего пользователя
        if (currentUser == null) {
            System.out.println("Пользователь не авторизован!");
            return 0.0;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка запроса на сервер с ID текущего пользователя
            out.println("GET_BALANCE " + currentUser.getId());
            System.out.println("Отправлен запрос на получение баланса для пользователя " + currentUser.getId()); // Вывод для отладки

            // Получение ответа от сервера
            String response = in.readLine();
            System.out.println("Ответ сервера: " + response); // Вывод ответа сервера

            if (response.startsWith("BALANCE")) {
                return Double.parseDouble(response.substring(8)); // Пример ответа: BALANCE 2000.0
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0; // Возвращаем 0, если не удалось получить баланс
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