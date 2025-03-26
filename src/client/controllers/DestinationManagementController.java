package client.controllers;

import client.models.DestinationModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Обработчик выбора строки в таблице
        destinationsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillFieldsWithSelectedDestination(newSelection);
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
                        // Убираем экранирование
                        String name = fields[1].replace("\\,", ",").replace("\\|", "|");
                        String country = fields[2].replace("\\,", ",").replace("\\|", "|");
                        String description = fields[3].replace("\\,", ",").replace("\\|", "|");

                        DestinationModel destination = new DestinationModel(
                                Integer.parseInt(fields[0].trim()),
                                name,
                                country,
                                description
                        );
                        destinationsTable.getItems().add(destination);
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить направления");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDestination() {
        if (!validateInput()) {
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String name = escapeSpecialChars(nameField.getText().trim());
            String country = escapeSpecialChars(countryField.getText().trim());
            String description = escapeSpecialChars(descriptionField.getText().trim());

            out.println("ADD_DESTINATION " + name + "," + country + "," + description);
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
        if (selectedDestination != null) {
            if (!validateInput()) {
                return;
            }

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String name = escapeSpecialChars(nameField.getText().trim());
                String country = escapeSpecialChars(countryField.getText().trim());
                String description = escapeSpecialChars(descriptionField.getText().trim());

                out.println("UPDATE_DESTINATION " + selectedDestination.getId() + "," +
                        name + "," + country + "," + description);
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
        } else {
            showAlert("Ошибка", "Выберите направление для изменения");
        }
    }

    @FXML
    private void handleDeleteDestination() {
        DestinationModel selectedDestination = destinationsTable.getSelectionModel().getSelectedItem();
        if (selectedDestination != null) {
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

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
        } else {
            showAlert("Ошибка", "Выберите направление для удаления");
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
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Введите название направления");
            return false;
        }
        if (countryField.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Введите страну");
            return false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            showAlert("Ошибка", "Введите описание");
            return false;
        }
        return true;
    }

    private String escapeSpecialChars(String input) {
        return input.replace(",", "\\,").replace("|", "\\|");
    }

    private void clearFields() {
        nameField.clear();
        countryField.clear();
        descriptionField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}