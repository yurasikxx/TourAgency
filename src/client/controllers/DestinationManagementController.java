package client.controllers;

import client.models.DestinationModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DestinationManagementController {
    @FXML
    private TableView<DestinationModel> destinationsTable;
    @FXML
    private TableColumn<DestinationModel, Integer> idColumn;
    @FXML
    private TableColumn<DestinationModel, String> nameColumn;
    @FXML
    private TableColumn<DestinationModel, String> countryColumn;
    @FXML
    private TableColumn<DestinationModel, String> descriptionColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField countryField;
    @FXML
    private TextArea descriptionField;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeTable();
        loadDestinations();
        setupFieldValidation();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        destinationsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillFieldsWithSelectedDestination(newSelection);
                    }
                });
    }

    private void setupFieldValidation() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 100) {
                nameField.setText(oldVal);
            }
        });

        countryField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 50) {
                countryField.setText(oldVal);
            }
        });
    }

    private void fillFieldsWithSelectedDestination(DestinationModel destination) {
        nameField.setText(destination.getName());
        countryField.setText(destination.getCountry());
        descriptionField.setText(destination.getDescription());
    }

    private void loadDestinations() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_ALL_DESTINATIONS");
            String response = in.readLine();

            if (response.startsWith("DESTINATIONS")) {
                destinationsTable.getItems().clear();
                String[] destinationsData = response.substring(12).split("\\|");

                for (String destinationData : destinationsData) {
                    String[] fields = destinationData.split("(?<!\\\\),");
                    if (fields.length == 4) {
                        DestinationModel destination = getDestinationModel(fields);
                        destinationsTable.getItems().add(destination);
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить направления");
            e.printStackTrace();
        }
    }

    private static DestinationModel getDestinationModel(String[] fields) {
        String name = fields[1].replace("\\,", ",").replace("\\|", "|");
        String country = fields[2].replace("\\,", ",").replace("\\|", "|");
        String description = fields[3].replace("\\,", ",").replace("\\|", "|");

        return new DestinationModel(
                Integer.parseInt(fields[0].trim()),
                name,
                country,
                description
        );
    }

    @FXML
    private void handleAddDestination() {
        if (validateInput()) {
            return;
        }

        String name = nameField.getText().trim();

        if (isDestinationNameExists(name)) {
            showAlert("Ошибка", "Направление с таким названием уже существует");
            nameField.setStyle("-fx-border-color: red;");
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String country = countryField.getText().trim();
            String description = descriptionField.getText().trim();

            out.println("ADD_DESTINATION " +
                    escapeSpecialChars(name) + "," +
                    escapeSpecialChars(country) + "," +
                    escapeSpecialChars(description));

            String response = in.readLine();

            if ("DESTINATION_ADDED".equals(response)) {
                showAlert("Успех", "Направление добавлено");
                loadDestinations();
                clearFields();
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Ошибка при добавлении направления");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateDestination() {
        DestinationModel selectedDestination = destinationsTable.getSelectionModel().getSelectedItem();
        if (selectedDestination == null) {
            showAlert("Ошибка", "Выберите направление для изменения");
            return;
        }

        if (validateInput()) {
            return;
        }

        String newName = nameField.getText().trim();

        if (!newName.equals(selectedDestination.getName()) &&
                isDestinationNameExists(newName)) {
            showAlert("Ошибка", "Направление с таким названием уже существует");
            nameField.setStyle("-fx-border-color: red;");
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String country = countryField.getText().trim();
            String description = descriptionField.getText().trim();

            out.println("UPDATE_DESTINATION " + selectedDestination.getId() + "," +
                    escapeSpecialChars(newName) + "," +
                    escapeSpecialChars(country) + "," +
                    escapeSpecialChars(description));

            String response = in.readLine();

            if ("DESTINATION_UPDATED".equals(response)) {
                showAlert("Успех", "Направление обновлено");
                loadDestinations();
                clearFields();
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Ошибка при обновлении направления");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteDestination() {
        DestinationModel selectedDestination = destinationsTable.getSelectionModel().getSelectedItem();
        if (selectedDestination == null) {
            showAlert("Ошибка", "Выберите направление для удаления");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Вы уверены, что хотите удалить направление \"" +
                selectedDestination.getName() + "\"?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("HAS_TOURS_FOR_DESTINATION " + selectedDestination.getId());
            String hasToursResponse = in.readLine();

            if ("HAS_TOURS true".equals(hasToursResponse)) {
                showAlert("Ошибка", "Нельзя удалить направление, так как к нему привязаны туры");
                return;
            }

            out.println("DELETE_DESTINATION " + selectedDestination.getId());
            String response = in.readLine();

            if ("DESTINATION_DELETED".equals(response)) {
                showAlert("Успех", "Направление удалено");
                loadDestinations();
                clearFields();
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось удалить направление");
            e.printStackTrace();
        }
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

    private boolean validateInput() {
        boolean isValid = true;
        resetFieldStyles();

        if (nameField.getText().trim().isEmpty()) {
            nameField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (countryField.getText().trim().isEmpty()) {
            countryField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (!isValid) {
            showAlert("Ошибка", "Заполните все обязательные поля");
        }

        return !isValid;
    }

    private void resetFieldStyles() {
        nameField.setStyle("");
        countryField.setStyle("");
        descriptionField.setStyle("");
    }

    private boolean isDestinationNameExists(String name) {
        return destinationsTable.getItems().stream()
                .anyMatch(dest -> dest.getName().equalsIgnoreCase(name));
    }

    private String escapeSpecialChars(String input) {
        return input.replace(",", "\\,").replace("|", "\\|");
    }

    private void clearFields() {
        nameField.clear();
        countryField.clear();
        descriptionField.clear();
        resetFieldStyles();
        destinationsTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}