package client.controllers;

import client.MainClient;
import client.models.ReviewModel;
import client.models.TourModel;
import client.models.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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

    @FXML
    private Button addReviewButton;

    @FXML
    private VBox reviewsContainer;

    @FXML
    private Label ratingLabel;

    @FXML
    private Button profileButton;

    private Stage primaryStage;

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
            addReviewButton.setVisible(false);
            bookTourButton.setVisible(false);
            viewBookingsButton.setVisible(false);
            viewDestinationsButton.setVisible(false);
            profileButton.setVisible(false);

            manageUsersButton.setVisible(true);
            manageToursButton.setVisible(true);
            manageDestinationsButton.setVisible(true);
        } else {
            addReviewButton.setVisible(true);
            bookTourButton.setVisible(true);
            viewBookingsButton.setVisible(true);
            viewDestinationsButton.setVisible(true);
            profileButton.setVisible(true);

            manageUsersButton.setVisible(false);
            manageToursButton.setVisible(false);
            manageDestinationsButton.setVisible(false);
        }

        refreshButton.setVisible(true);
        logoutButton.setVisible(true);
    }

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        sortComboBox.getItems().addAll(
                "По умолчанию",
                "По цене (возрастание)",
                "По цене (убывание)",
                "По дате (возрастание)",
                "По дате (убывание)",
                "По популярности"
        );
        sortComboBox.setValue("По умолчанию");

        priceSlider.setMin(0);
        priceSlider.setMax(10000);
        priceSlider.setValue(10000);
        priceSlider.setShowTickLabels(true);
        priceSlider.setShowTickMarks(true);
        priceSlider.setMajorTickUnit(2000);
        priceSlider.setMinorTickCount(4);
        priceSlider.setBlockIncrement(500);

        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> priceLabel.setText(String.format("%.0f", newVal)));

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

        tourTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadTourReviews(newSelection.getId());
                        checkIfUserCanReview(newSelection.getId());
                    }
                });
    }

    private String formatDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return dateString;
        }
    }

    private void loadTours() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_TOURS");
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

    @FXML
    private void handleBookTour() {
        TourModel selectedTour = tourTable.getSelectionModel().getSelectedItem();
        AtomicReference<Double> totalPrice = new AtomicReference<>((double) 0);

        if (selectedTour != null) {
            Dialog<BookingDetails> dialog = new Dialog<>();
            dialog.setTitle("Бронирование тура");
            dialog.setHeaderText("Бронирование тура: " + selectedTour.getName());

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            Spinner<Integer> adultsSpinner = new Spinner<>(1, 10, 1);
            Spinner<Integer> childrenSpinner = new Spinner<>(0, 10, 0);

            ComboBox<String> mealCombo = new ComboBox<>();
            mealCombo.getItems().addAll(
                    "Без (0 руб.)",
                    "Завтрак (+500 руб./чел.)",
                    "Полупансион (+1000 руб./чел.)",
                    "Полное (+1500 руб./чел.)"
            );
            mealCombo.setValue("Без (0 руб.)");

            TextField servicesField = new TextField();
            servicesField.setPromptText("Доп. услуги (через запятую)");

            Label basePriceLabel = new Label(String.format("Базовая цена: %.2f руб.", selectedTour.getPrice()));
            Label mealPriceLabel = new Label("Питание: 0 руб.");
            Label additionalServicesLabel = new Label("Доп.услуги: 0 руб.");
            Label totalPriceLabel = new Label("Итого: " + selectedTour.getPrice() + " руб.");
            totalPriceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

            grid.add(new Label("Взрослые:"), 0, 0);
            grid.add(adultsSpinner, 1, 0);
            grid.add(new Label("Дети (до 13 лет, скидка 30%):"), 0, 1);
            grid.add(childrenSpinner, 1, 1);
            grid.add(new Label("Питание:"), 0, 2);
            grid.add(mealCombo, 1, 2);
            grid.add(new Label("Доп. услуги:"), 0, 3);
            grid.add(servicesField, 1, 3);
            grid.add(basePriceLabel, 0, 4, 2, 1);
            grid.add(mealPriceLabel, 0, 5, 2, 1);
            grid.add(additionalServicesLabel, 0, 6, 2, 1);
            grid.add(totalPriceLabel, 0, 7, 2, 1);

            Runnable updatePrice = () -> {
                int adults = adultsSpinner.getValue();
                int children = childrenSpinner.getValue();
                String mealOption = mealCombo.getValue();

                double basePrice = selectedTour.getPrice();
                double baseTotal = basePrice * adults + (basePrice * 0.7) * children;

                int mealPricePerPerson = 0;
                if (mealOption != null && !mealOption.startsWith("Без")) {
                    if (mealOption.contains("Завтрак")) mealPricePerPerson = 500;
                    else if (mealOption.contains("Полупансион")) mealPricePerPerson = 1000;
                    else if (mealOption.contains("Полное")) mealPricePerPerson = 1500;
                }

                double additionalServicesCost = 0;
                if (!servicesField.getText().isEmpty()) {
                    String[] services = servicesField.getText().split(",");
                    additionalServicesCost = services.length * 500;
                }

                double totalMealPrice = mealPricePerPerson * (adults + children);
                double totalAdditionalServicePrice = additionalServicesCost * (adults + children);
                double total = baseTotal + totalMealPrice + totalAdditionalServicePrice;
                totalPrice.set(total);

                basePriceLabel.setText(String.format("Базовая цена: %.2f руб. (%d взр. × %.2f + %d дет. × %.2f)",
                        baseTotal, adults, basePrice, children, basePrice * 0.7));
                mealPriceLabel.setText(String.format("Питание: %.2f руб. (%s × %d чел.)",
                        totalMealPrice, mealOption, adults + children));
                additionalServicesLabel.setText(String.format("Доп.услуги: %.2f руб. (%d чел.)",
                        totalAdditionalServicePrice, adults + children));
                totalPriceLabel.setText(String.format("Итого: %.2f руб.", total));
            };

            adultsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updatePrice.run());
            childrenSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updatePrice.run());
            mealCombo.valueProperty().addListener((obs, oldVal, newVal) -> updatePrice.run());
            servicesField.textProperty().addListener((obs, oldVal, newVal) -> updatePrice.run());

            updatePrice.run();

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    String mealOption = mealCombo.getValue().split("\\(")[0].trim();
                    return new BookingDetails(
                            adultsSpinner.getValue(),
                            childrenSpinner.getValue(),
                            mealOption,
                            servicesField.getText()
                    );
                }
                return null;
            });

            Optional<BookingDetails> result = dialog.showAndWait();

            result.ifPresent(bookingDetails -> {
                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    UserModel currentUser = MainClient.getCurrentUser();
                    String currentDate = LocalDate.now().toString();

                    String command = String.format("BOOK_TOUR %d %d %s %d %d %s %s %.2f",
                            currentUser.getId(),
                            selectedTour.getId(),
                            currentDate,
                            bookingDetails.getAdults(),
                            bookingDetails.getChildren(),
                            bookingDetails.getMealOption(),
                            bookingDetails.getAdditionalServices(),
                            Double.parseDouble(totalPrice.toString()));

                    out.println(command);
                    String response = in.readLine();

                    if (response.equals("BOOKING_SUCCESS")) {
                        showAlert("Успех", "Тур успешно забронирован");
                    } else {
                        showAlert("Ошибка", response.substring(response.indexOf(":") + 1));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Ошибка", "Ошибка подключения к серверу");
                }
            });
        } else {
            showAlert("Предупреждение", "Выберите тур для бронирования");
        }
    }

    private static class BookingDetails {
        private final int adults;
        private final int children;
        private final String mealOption;
        private final String additionalServices;

        public BookingDetails(int adults, int children, String mealOption, String additionalServices) {
            this.adults = adults;
            this.children = children;
            this.mealOption = mealOption;
            this.additionalServices = additionalServices;
        }

        public int getAdults() {
            return adults;
        }

        public int getChildren() {
            return children;
        }

        public String getMealOption() {
            return mealOption;
        }

        public String getAdditionalServices() {
            return additionalServices;
        }
    }

    @FXML
    private void handleRefresh() {
        tourTable.getItems().clear();
        loadTours();
    }

    @FXML
    private void handleViewDestinations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/destination.fxml"));
            Parent root = loader.load();

            DestinationController destinationController = loader.getController();
            destinationController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);

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
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.centerOnScreen();

        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String command = getString();
            out.println(command);

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

    private String getString() {
        String searchTerm = searchField.getText();
        Double maxPrice = priceSlider.getValue() < priceSlider.getMax() ?
                priceSlider.getValue() : null;
        String startDate = startDatePicker.getValue() != null ?
                startDatePicker.getValue().toString() : "null";
        String endDate = endDatePicker.getValue() != null ?
                endDatePicker.getValue().toString() : "null";

        String sortBy;
        switch (sortComboBox.getValue()) {
            case "По цене (возрастание)":
                sortBy = "price_asc";
                break;
            case "По цене (убывание)":
                sortBy = "price_desc";
                break;
            case "По дате (возрастание)":
                sortBy = "date_asc";
                break;
            case "По дате (убывание)":
                sortBy = "date_desc";
                break;
            case "По популярности":
                sortBy = "popular";
                break;
            default:
                sortBy = "null";
        }

        return String.format("SEARCH_TOURS %s %s %s %s %s %s",
                searchTerm.isEmpty() ? "null" : searchTerm,
                maxPrice != null ? String.format("%.2f", maxPrice) : "null",
                "null",
                startDate,
                endDate,
                sortBy);
    }

    @FXML
    private void handleResetFilters() {
        searchField.clear();
        priceSlider.setValue(10000);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        sortComboBox.setValue("По умолчанию");
        handleRefresh();
    }

    private void loadTourReviews(int tourId) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_REVIEWS " + tourId);
            String response = in.readLine();

            reviewsContainer.getChildren().clear();

            if (response.startsWith("REVIEWS")) {
                String[] reviewsData = response.substring(7).split("\\|");
                for (String reviewData : reviewsData) {
                    try {
                        String[] fields = reviewData.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        if (fields.length >= 6) {
                            ReviewModel review = new ReviewModel(
                                    Integer.parseInt(fields[0].trim()),
                                    Integer.parseInt(fields[1].trim()),
                                    fields[2].replace("\"", "").replace("\\,", ",").replace("\\|", "|"),
                                    Integer.parseInt(fields[3].trim()),
                                    fields[4].replace("\"", "").replace("\\,", ",").replace("\\|", "|"),
                                    fields[5].trim()
                            );
                            addReviewToUI(review);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка парсинга отзыва: " + reviewData);
                        e.printStackTrace();
                    }
                }
            }

            out.println("GET_TOUR_RATING " + tourId);
            response = in.readLine();
            if (response.startsWith("TOUR_RATING")) {
                String[] parts = response.split(" ");
                double rating = Double.parseDouble(parts[1]);
                int count = Integer.parseInt(parts[2]);
                ratingLabel.setText(String.format("Рейтинг: %.1f/5 (%d отзывов)", rating, count));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addReviewToUI(ReviewModel review) {
        VBox reviewBox = new VBox(5);
        reviewBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        HBox headerBox = new HBox(10);
        Label userLabel = new Label(review.getUsername());
        Label ratingLabel = new Label("Оценка: " + review.getRating() + "/5");
        Label dateLabel = new Label(review.getReviewDate());

        headerBox.getChildren().addAll(userLabel, ratingLabel, dateLabel);

        Text commentText = new Text(review.getComment());
        commentText.setWrappingWidth(600);

        reviewBox.getChildren().addAll(headerBox, commentText);
        reviewsContainer.getChildren().add(reviewBox);
    }

    private void checkIfUserCanReview(int tourId) {
        UserModel currentUser = MainClient.getCurrentUser();
        if (currentUser != null) {
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("HAS_REVIEWED " + currentUser.getId() + " " + tourId);
                String response = in.readLine();

                if (response.startsWith("HAS_REVIEWED")) {
                    boolean hasReviewed = Boolean.parseBoolean(response.split(" ")[1]);
                    addReviewButton.setVisible(!hasReviewed);

                    out.println("HAS_BOOKED " + currentUser.getId() + " " + tourId);
                    response = in.readLine();
                    boolean hasBooked = response.startsWith("HAS_BOOKED true");
                    addReviewButton.setDisable(!hasBooked);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            addReviewButton.setVisible(false);
        }
    }

    @FXML
    private void handleAddReview() {
        TourModel selectedTour = tourTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            Dialog<Pair<Integer, String>> dialog = new Dialog<>();
            dialog.setTitle("Добавить отзыв");
            dialog.setHeaderText("Оставьте отзыв о туре: " + selectedTour.getName());

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            ComboBox<Integer> ratingCombo = new ComboBox<>();
            ratingCombo.getItems().addAll(1, 2, 3, 4, 5);
            ratingCombo.setValue(5);

            TextArea commentField = new TextArea();
            commentField.setPromptText("Ваш отзыв...");
            commentField.setWrapText(true);

            grid.add(new Label("Оценка:"), 0, 0);
            grid.add(ratingCombo, 1, 0);
            grid.add(new Label("Комментарий:"), 0, 1);
            grid.add(commentField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return new Pair<>(ratingCombo.getValue(), commentField.getText());
                }
                return null;
            });

            Optional<Pair<Integer, String>> result = dialog.showAndWait();

            result.ifPresent(ratingAndComment -> {
                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    UserModel currentUser = MainClient.getCurrentUser();
                    String currentDate = java.time.LocalDate.now().toString();

                    String escapedComment = ratingAndComment.getValue()
                            .replace("\\", "\\\\")
                            .replace(",", "\\,")
                            .replace("|", "\\|")
                            .replace("\"", "\\\"");

                    String command = String.format("ADD_REVIEW %d %d %d \"%s\" %s",
                            currentUser.getId(),
                            selectedTour.getId(),
                            ratingAndComment.getKey(),
                            escapedComment,
                            currentDate);

                    out.println(command);
                    String response = in.readLine();

                    if ("REVIEW_ADDED".equals(response)) {
                        loadTourReviews(selectedTour.getId());
                        checkIfUserCanReview(selectedTour.getId());
                    } else {
                        showAlert("Ошибка", "Не удалось добавить отзыв: " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Ошибка", "Ошибка подключения к серверу");
                }
            });
        }
    }

    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/profile.fxml"));
            Parent root = loader.load();

            ProfileController profileController = loader.getController();
            profileController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Личный кабинет");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить личный кабинет");
        }
    }
}