package client.controllers;

import client.MainClient;
import client.models.TourModel;
import client.models.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;

public class TourController {
    @FXML
    private TableView<TourModel> tourTable;

    @FXML
    private TableColumn<TourModel, String> nameColumn;

    @FXML
    private TableColumn<TourModel, String> descriptionColumn;

    @FXML
    private TableColumn<TourModel, Double> priceColumn;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button bookTourButton;

    @FXML
    private Button viewBookingsButton;

    @FXML
    private Button viewDestinationsButton;

    @FXML
    private Button manageUsersButton;

    @FXML
    private Button manageToursButton;

    @FXML
    private Button manageDestinationsButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button logoutButton;

    private Stage primaryStage;

    /**
     * Устанавливает primaryStage для контроллера.
     *
     * @param primaryStage Основное окно приложения.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadTours();

        UserModel currentUser = MainClient.getCurrentUser();
        if (currentUser != null) {
            setWelcomeMessage(currentUser.getUsername());
            setupUIForRole(currentUser.getRole());
        }
    }

    public void setWelcomeMessage(String username) {
        welcomeLabel.setText("Добро пожаловать, " + username + "!");
    }

    private void setupUIForRole(String role) {
        if ("ADMIN".equals(role)) {
            // Показываем только кнопки для администратора
            bookTourButton.setVisible(false);
            viewBookingsButton.setVisible(false);
            viewDestinationsButton.setVisible(false);

            manageUsersButton.setVisible(true);
            manageToursButton.setVisible(true);
            manageDestinationsButton.setVisible(true);
        } else {
            // Показываем только кнопки для пользователя
            bookTourButton.setVisible(true);
            viewBookingsButton.setVisible(true);
            viewDestinationsButton.setVisible(true);

            manageUsersButton.setVisible(false);
            manageToursButton.setVisible(false);
            manageDestinationsButton.setVisible(false);
        }

        refreshButton.setVisible(true);
        logoutButton.setVisible(true);
    }

    @FXML
    private void initialize() {
        // Настройка колонок таблицы
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    /**
     * Загружает список туров с сервера.
     */
    private void loadTours() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Отправка запроса на сервер
            out.println("GET_TOURS");

            // Получение ответа от сервера
            String response = in.readLine();
            if (response.startsWith("TOURS")) {
                String[] toursData = response.substring(6).split("\\|");
                for (String tourData : toursData) {
                    String[] fields = tourData.split(",");
                    TourModel tour = new TourModel(
                            Integer.parseInt(fields[0]),
                            fields[1],
                            fields[2],
                            Double.parseDouble(fields[3]),
                            fields[4],
                            fields[5],
                            fields[6]
                    );
                    tourTable.getItems().add(tour);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Забронировать".
     */
    @FXML
    private void handleBookTour() {
        TourModel selectedTour = tourTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Получаем текущую дату
                String currentDate = LocalDate.now().toString(); // Формат: "2023-10-25"

                // Отправка запроса на сервер
                out.println("BOOK_TOUR 1 " + selectedTour.getId() + " " + currentDate); // Пример: userId = 1, bookingDate = текущая дата

                // Получение ответа от сервера
                String response = in.readLine();
                if (response.equals("BOOKING_SUCCESS")) {
                    System.out.println("Тур успешно забронирован!");
                } else {
                    System.out.println("Ошибка при бронировании тура.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Тур не выбран!");
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Обновить".
     */
    @FXML
    private void handleRefresh() {
        tourTable.getItems().clear(); // Очищаем таблицу
        loadTours(); // Загружаем туры заново
    }

    /**
     * Обрабатывает нажатие кнопки "Направления".
     */
    @FXML
    private void handleViewDestinations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/destination.fxml"));
            Parent root = loader.load();

            DestinationController destinationController = loader.getController();
            destinationController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);

            // Устанавливаем размер сцены
            primaryStage.setScene(scene);
            primaryStage.setWidth(1280); // Ширина окна
            primaryStage.setHeight(720); // Высота окна
            primaryStage.setTitle("Направления");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Авторизация");
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при переходе к авторизации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/booking.fxml"));
            Parent root = loader.load();

            BookingController bookingController = loader.getController();
            bookingController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);

            // Устанавливаем размер сцены
            primaryStage.setScene(scene);
            primaryStage.setWidth(1280); // Ширина окна
            primaryStage.setHeight(720); // Высота окна
            primaryStage.setTitle("Мои бронирования");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user_management.fxml"));
            Parent root = loader.load();
            UserManagementController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(new Scene(root, 1280, 720));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить интерфейс управления пользователями");
        }
    }

    @FXML
    private void handleManageTours() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour_management.fxml"));
            Parent root = loader.load();
            TourManagementController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(new Scene(root, 1280, 720));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить интерфейс управления турами");
        }
    }

    @FXML
    private void handleManageDestinations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/destination_management.fxml"));
            Parent root = loader.load();
            DestinationManagementController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(new Scene(root, 1280, 720));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить интерфейс управления направлениями");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}