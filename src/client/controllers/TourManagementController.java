package client.controllers;

import client.models.DestinationModel;
import client.models.TourModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class TourManagementController {
    @FXML
    private TableView<TourModel> toursTable;
    @FXML
    private TableColumn<TourModel, Integer> idColumn;
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
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<DestinationModel> destinationComboBox;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeControls();
        initializeTable();
        loadTours();
        loadDestinations();
        setupTableSelectionListener();
    }

    private void initializeControls() {
        // Настройка формата дат
        StringConverter<LocalDate> converter = new LocalDateStringConverter(
                DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_DATE);
        startDatePicker.setConverter(converter);
        endDatePicker.setConverter(converter);

        // Настройка ComboBox для направлений
        destinationComboBox.setCellFactory(param -> new ListCell<DestinationModel>() {
            @Override
            protected void updateItem(DestinationModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getCountry() + ")");
                }
            }
        });

        destinationComboBox.setButtonCell(new ListCell<DestinationModel>() {
            @Override
            protected void updateItem(DestinationModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getCountry() + ")");
                }
            }
        });
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
    }

    private void setupTableSelectionListener() {
        toursTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillFieldsWithSelectedTour(newSelection);
                    }
                });
    }

    private void fillFieldsWithSelectedTour(TourModel tour) {
        nameField.setText(tour.getName());
        descriptionField.setText(tour.getDescription());
        priceField.setText(String.valueOf(tour.getPrice()));

        try {
            startDatePicker.setValue(LocalDate.parse(tour.getStartDate()));
            endDatePicker.setValue(LocalDate.parse(tour.getEndDate()));
        } catch (Exception e) {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        }

        // Находим соответствующее направление в ComboBox
        for (DestinationModel dest : destinationComboBox.getItems()) {
            if (dest.getId() == Integer.parseInt(tour.getDestination())) {
                destinationComboBox.setValue(dest);
                break;
            }
        }
    }

    private void loadTours() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_ALL_TOURS_ADMIN");
            String response = in.readLine();

            if (response.startsWith("TOURS_ADMIN")) {
                toursTable.getItems().clear();
                String[] toursData = response.substring(11).split("\\|");

                for (String tourData : toursData) {
                    String[] fields = tourData.split(",");
                    if (fields.length == 7) {
                        TourModel tour = new TourModel(
                                Integer.parseInt(fields[0].trim()),
                                fields[1],
                                fields[2],
                                Double.parseDouble(fields[3].trim()),
                                fields[4],
                                fields[5],
                                fields[6]
                        );
                        toursTable.getItems().add(tour);
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить список туров");
        }
    }

    private void loadDestinations() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_DESTINATIONS");
            String response = in.readLine();

            if (response == null) {
                showAlert("Ошибка", "Пустой ответ от сервера");
                return;
            }

            if (response.startsWith("DESTINATIONS EMPTY")) {
                showAlert("Информация", "Список направлений пуст");
                return;
            }

            if (!response.startsWith("DESTINATIONS ")) {
                showAlert("Ошибка", "Неверный формат ответа: " + response);
                return;
            }

            destinationComboBox.getItems().clear();
            String destinationsData = response.substring(12);

            // Обрабатываем случай, когда нет направлений
            if (destinationsData.isEmpty()) {
                return;
            }

            String[] destinations = destinationsData.split("\\|");
            for (String destData : destinations) {
                try {
                    String[] fields = destData.split("(?<!\\\\),"); // Разделяем по запятым, не экранированным
                    if (fields.length == 4) {
                        // Убираем экранирование
                        String name = fields[1].replace("\\,", ",").replace("\\|", "|");
                        String country = fields[2].replace("\\,", ",").replace("\\|", "|");
                        String description = fields[3].replace("\\,", ",").replace("\\|", "|");

                        DestinationModel dest = new DestinationModel(
                                Integer.parseInt(fields[0].trim()),
                                name,
                                country,
                                description
                        );
                        destinationComboBox.getItems().add(dest);
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга направления: " + destData);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить направления: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTour() {
        if (!validateTourInput()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String startDate = startDatePicker.getValue().format(DateTimeFormatter.ISO_DATE);
            String endDate = endDatePicker.getValue().format(DateTimeFormatter.ISO_DATE);
            int destinationId = destinationComboBox.getValue().getId();

            // Проверка на уникальность названия тура
            if (isTourNameExists(name)) {
                showAlert("Ошибка", "Тур с таким названием уже существует");
                nameField.setStyle("-fx-border-color: red;");
                return;
            }

            // Формируем команду
            String delimiter = "--";
            String command = String.format("ADD_TOUR %s%s%s%s%.2f%s%s%s%s%s%d",
                    name, delimiter,
                    description, delimiter,
                    price, delimiter,
                    startDate, delimiter,
                    endDate, delimiter,
                    destinationId);

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(command);
                String response = in.readLine();

                if ("TOUR_ADDED".equals(response)) {
                    showAlert("Успех", "Тур успешно добавлен");
                    loadTours();
                    clearFields();
                } else {
                    showAlert("Ошибка", response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при добавлении тура: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateTour() {
        TourModel selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            try {
                String newName = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                String priceText = priceField.getText().trim();
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                DestinationModel selectedDestination = destinationComboBox.getValue();

                // Валидация
                String error = validateInput(newName, description, priceText, startDate, endDate, selectedDestination);
                if (error != null) {
                    showAlert("Ошибка", error);
                    return;
                }

                // Проверяем, не пытаемся ли изменить на существующее имя (кроме текущего тура)
                if (!newName.equals(selectedTour.getName()) && isTourNameExists(newName)) {
                    showAlert("Ошибка", "Тур с таким названием уже существует");
                    nameField.setStyle("-fx-border-color: red;");
                    return;
                }

                double price = Double.parseDouble(priceText);
                int destinationId = selectedDestination.getId();

                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String delimiter = "--";
                    String command = String.format("UPDATE_TOUR %d%s%s%s%s%s%.2f%s%s%s%s%s%d",
                            selectedTour.getId(), delimiter,
                            newName, delimiter,
                            description, delimiter,
                            price, delimiter,
                            startDate.toString(), delimiter,
                            endDate.toString(), delimiter,
                            destinationId);

                    out.println(command);
                    String response = in.readLine();

                    if ("TOUR_UPDATED".equals(response)) {
                        showAlert("Успех", "Тур обновлен");
                        loadTours();
                        clearFields();
                    } else {
                        showAlert("Ошибка", response);
                    }
                }
            } catch (Exception e) {
                showAlert("Ошибка", "Ошибка при обновлении тура: " + e.getMessage());
            }
        } else {
            showAlert("Ошибка", "Выберите тур для изменения");
        }
    }

    @FXML
    private void handleDeleteTour() {
        TourModel selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            // Подтверждение удаления
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Подтверждение удаления");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Вы уверены, что хотите удалить тур \"" + selectedTour.getName() + "\"?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // Сначала проверяем, есть ли бронирования для этого тура
                    out.println("HAS_BOOKINGS " + selectedTour.getId());
                    String hasBookingsResponse = in.readLine();

                    if ("HAS_BOOKINGS true".equals(hasBookingsResponse)) {
                        showAlert("Ошибка", "Нельзя удалить тур, так как он забронирован или оплачен");
                        return;
                    }

                    // Если бронирований нет, удаляем тур
                    out.println("DELETE_TOUR " + selectedTour.getId());
                    String response = in.readLine();

                    if ("TOUR_DELETED".equals(response)) {
                        showAlert("Успех", "Тур удален");
                        loadTours();
                        clearFields();
                    } else {
                        showAlert("Ошибка", response);
                    }
                } catch (Exception e) {
                    showAlert("Ошибка", "Не удалось удалить тур: " + e.getMessage());
                }
            }
        } else {
            showAlert("Ошибка", "Выберите тур для удаления");
        }
    }

    // Проверяет, существует ли тур с таким названием
    private boolean isTourNameExists(String tourName) {
        return toursTable.getItems().stream()
                .anyMatch(tour -> tour.getName().equalsIgnoreCase(tourName));
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();
            TourController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(new Scene(root));
            primaryStage.setWidth(1600);
            primaryStage.setHeight(900);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String validateInput(String name, String description, String priceText,
                                 LocalDate startDate, LocalDate endDate, DestinationModel destination) {
        if (name.isEmpty()) {
            nameField.setStyle("-fx-border-color: red;");
            return "Введите название тура";
        } else {
            nameField.setStyle("");
        }

        if (description.isEmpty()) {
            descriptionField.setStyle("-fx-border-color: red;");
            return "Введите описание тура";
        } else {
            descriptionField.setStyle("");
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                priceField.setStyle("-fx-border-color: red;");
                return "Стоимость должна быть положительным числом";
            } else {
                priceField.setStyle("");
            }
        } catch (NumberFormatException e) {
            priceField.setStyle("-fx-border-color: red;");
            return "Некорректная стоимость тура";
        }

        if (startDate == null) {
            startDatePicker.setStyle("-fx-border-color: red;");
            return "Выберите дату начала";
        } else {
            startDatePicker.setStyle("");
        }

        if (endDate == null) {
            endDatePicker.setStyle("-fx-border-color: red;");
            return "Выберите дату окончания";
        } else {
            endDatePicker.setStyle("");
        }

        if (endDate.isBefore(startDate)) {
            startDatePicker.setStyle("-fx-border-color: red;");
            endDatePicker.setStyle("-fx-border-color: red;");
            return "Дата окончания должна быть после даты начала";
        } else {
            startDatePicker.setStyle("");
            endDatePicker.setStyle("");
        }

        if (destination == null) {
            destinationComboBox.setStyle("-fx-border-color: red;");
            return "Выберите направление";
        } else {
            destinationComboBox.setStyle("");
        }

        return null;
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        destinationComboBox.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateTourInput() {
        boolean isValid = true;

        // Валидация названия
        if (nameField.getText().trim().isEmpty()) {
            nameField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            nameField.setStyle("");
        }

        // Валидация описания
        if (descriptionField.getText().trim().isEmpty()) {
            descriptionField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            descriptionField.setStyle("");
        }

        // Валидация цены
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                priceField.setStyle("-fx-border-color: red;");
                isValid = false;
            } else {
                priceField.setStyle("");
            }
        } catch (NumberFormatException e) {
            priceField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        // Валидация дат
        if (startDatePicker.getValue() == null) {
            startDatePicker.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            startDatePicker.setStyle("");
        }

        if (endDatePicker.getValue() == null) {
            endDatePicker.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            endDatePicker.setStyle("");
        }

        // Валидация направления
        if (destinationComboBox.getValue() == null) {
            destinationComboBox.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            destinationComboBox.setStyle("");
        }

        return isValid;
    }

    @FXML
    private void handleManagePayments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/payment_management.fxml"));
            Parent root = loader.load();

            PaymentManagementController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Управление платежами");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/booking_management.fxml"));
            Parent root = loader.load();

            BookingManagementController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Управление бронированиями");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}