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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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

        // Установка обработчика изменения выбора
        destinationComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        // Дополнительные действия при выборе направления
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

            // Формируем команду - название и описание передаем как есть
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
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                String priceText = priceField.getText().trim();
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                DestinationModel selectedDestination = destinationComboBox.getValue();

                // Валидация
                String error = validateInput(name, description, priceText, startDate, endDate, selectedDestination);
                if (error != null) {
                    showAlert("Ошибка", error);
                    return;
                }

                double price = Double.parseDouble(priceText);
                int destinationId = selectedDestination.getId();

                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // Используем тот же разделитель, что и в ADD_TOUR
                    String delimiter = "--";
                    String command = String.format("UPDATE_TOUR %d%s%s%s%s%s%.2f%s%s%s%s%s%d",
                            selectedTour.getId(), delimiter,
                            name, delimiter,
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
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

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
                showAlert("Ошибка", "Не удалось удалить тур");
            }
        } else {
            showAlert("Ошибка", "Выберите тур для удаления");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour.fxml"));
            Parent root = loader.load();
            TourController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(new Scene(root, 1280, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String validateInput(String name, String description, String priceText,
                                 LocalDate startDate, LocalDate endDate, DestinationModel destination) {
        if (name.isEmpty()) {
            return "Введите название тура";
        }
        if (description.isEmpty()) {
            return "Введите описание тура";
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                return "Стоимость должна быть положительным числом";
            }
        } catch (NumberFormatException e) {
            return "Некорректная стоимость тура";
        }

        if (startDate == null) {
            return "Выберите дату начала";
        }

        if (endDate == null) {
            return "Выберите дату окончания";
        }

        if (endDate.isBefore(startDate)) {
            return "Дата окончания должна быть после даты начала";
        }

        if (destination == null) {
            return "Выберите направление";
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
}