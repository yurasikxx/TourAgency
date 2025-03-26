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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
    private TableColumn<TourModel, String> startDateColumn;

    @FXML
    private TableColumn<TourModel, String> endDateColumn;

    @FXML
    private TableColumn<TourModel, String> destinationColumn;

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

    @FXML
    private TextField searchField;

    @FXML
    private Slider priceSlider;

    @FXML
    private Label priceLabel;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> sortComboBox;

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

        // Настройка комбобокса сортировки
        sortComboBox.getItems().addAll(
                "По умолчанию",
                "По цене (возрастание)",
                "По цене (убывание)",
                "По дате (возрастание)",
                "По дате (убывание)",
                "По популярности"
        );
        sortComboBox.setValue("По умолчанию");

        // Настройка слайдера цены
        priceSlider.setMin(0);
        priceSlider.setMax(10000);
        priceSlider.setValue(10000);
        priceSlider.setShowTickLabels(true);
        priceSlider.setShowTickMarks(true);
        priceSlider.setMajorTickUnit(2000);
        priceSlider.setMinorTickCount(4);
        priceSlider.setBlockIncrement(500);

        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            priceLabel.setText(String.format("%.0f", newVal));
        });

        // Форматирование дат
        startDateColumn.setCellFactory(column -> new TableCell<TourModel, String>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatDate(date));
                }
            }
        });

        endDateColumn.setCellFactory(column -> new TableCell<TourModel, String>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatDate(date));
                }
            }
        });
    }

    private String formatDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return dateString; // Возвращаем как есть, если не удалось распарсить
        }
    }

    /**
     * Загружает список туров с сервера.
     */
    private void loadTours() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_TOURS");
            String response = in.readLine();

            if (response.startsWith("TOURS")) {
                tourTable.getItems().clear();
                String[] toursData = response.substring(6).split("\\|");

                // Сначала загрузим все направления для маппинга
                Map<Integer, String> destinations = loadDestinationsMap();

                for (String tourData : toursData) {
                    String[] fields = tourData.split(",");
                    if (fields.length >= 7) {
                        int destinationId = Integer.parseInt(fields[6]);
                        String destinationName = destinations.getOrDefault(destinationId, "Направление " + destinationId);

                        TourModel tour = new TourModel(
                                Integer.parseInt(fields[0]),
                                fields[1],
                                fields[2],
                                Double.parseDouble(fields[3]),
                                fields[4],
                                fields[5],
                                destinationName
                        );
                        tourTable.getItems().add(tour);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, String> loadDestinationsMap() {
        Map<Integer, String> destinations = new HashMap<>();
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_DESTINATIONS");
            String response = in.readLine();

            if (response.startsWith("DESTINATIONS")) {
                String[] destinationsData = response.substring(12).split("\\|");
                for (String destData : destinationsData) {
                    String[] fields = destData.split("(?<!\\\\),");
                    if (fields.length >= 4) {
                        int id = Integer.parseInt(fields[0].trim());
                        String name = fields[1].replace("\\,", ",").replace("\\|", "|");
                        destinations.put(id, name);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destinations;
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

                UserModel currentUser = MainClient.getCurrentUser();
                String currentDate = LocalDate.now().toString();

                // Отправка запроса на сервер
                out.println("BOOK_TOUR " + currentUser.getId() + " " + selectedTour.getId() + " " + currentDate); // Пример: userId = 1, bookingDate = текущая дата

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
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
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

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setWidth(400);
            primaryStage.setHeight(300);
            primaryStage.centerOnScreen();
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
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
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

    // client/controllers/TourController.java
    @FXML
    private void handleSearch() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Получаем параметры поиска
            String searchTerm = searchField.getText();
            Double maxPrice = priceSlider.getValue() < priceSlider.getMax() ?
                    priceSlider.getValue() : null;
            String startDate = startDatePicker.getValue() != null ?
                    startDatePicker.getValue().toString() : "null";
            String endDate = endDatePicker.getValue() != null ?
                    endDatePicker.getValue().toString() : "null";

            // Определяем параметр сортировки
            String sortBy;
            switch (sortComboBox.getValue()) {
                case "По цене (возрастание)": sortBy = "price_asc"; break;
                case "По цене (убывание)": sortBy = "price_desc"; break;
                case "По дате (возрастание)": sortBy = "date_asc"; break;
                case "По дате (убывание)": sortBy = "date_desc"; break;
                case "По популярности": sortBy = "popular"; break;
                default: sortBy = "null";
            }

            // Формируем команду с учетом всех возможных комбинаций фильтров
            String command = String.format("SEARCH_TOURS %s %s %s %s %s %s",
                    searchTerm.isEmpty() ? "null" : searchTerm,
                    maxPrice != null ? String.format("%.2f", maxPrice) : "null",
                    "null", // minPrice (не используем в интерфейсе)
                    startDate,
                    endDate,
                    sortBy);

            out.println(command);

            // Обработка ответа остается без изменений
            String response = in.readLine();
            if (response.startsWith("TOURS")) {
                tourTable.getItems().clear();
                String[] toursData = response.substring(6).split("\\|");
                Map<Integer, String> destinations = loadDestinationsMap();

                for (String tourData : toursData) {
                    String[] fields = tourData.split(",");
                    if (fields.length >= 7) {
                        int destinationId = Integer.parseInt(fields[6]);
                        String destinationName = destinations.getOrDefault(destinationId, "Направление " + destinationId);

                        TourModel tour = new TourModel(
                                Integer.parseInt(fields[0]),
                                fields[1],
                                fields[2],
                                Double.parseDouble(fields[3]),
                                fields[4],
                                fields[5],
                                destinationName
                        );
                        tourTable.getItems().add(tour);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResetFilters() {
        searchField.clear();
        priceSlider.setValue(10000);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        sortComboBox.setValue("По умолчанию");
        handleRefresh(); // Загружаем все туры без фильтров
    }
}