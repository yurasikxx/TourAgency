package client.controllers;

import client.models.DestinationModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DestinationController {
    @FXML
    private ListView<DestinationModel> destinationList;

    @FXML
    private Label nameLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label descriptionLabel;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadDestinations();
    }

    @FXML
    private void initialize() {
        destinationList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDestinationDetails(newValue)
        );
    }

    private void loadDestinations() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_DESTINATIONS");

            String response = in.readLine();
            if (response.startsWith("DESTINATIONS")) {
                String[] destinationsData = response.substring(12).split("\\|");
                for (String destinationData : destinationsData) {
                    String[] fields = destinationData.split(",");
                    if (fields.length == 4) {
                        int id = Integer.parseInt(fields[0].trim());
                        String name = fields[1];
                        String country = fields[2];
                        String description = fields[3];

                        DestinationModel destination = new DestinationModel(id, name, country, description);
                        destinationList.getItems().add(destination);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDestinationDetails(DestinationModel destination) {
        if (destination != null) {
            nameLabel.setText(destination.getName());
            countryLabel.setText(destination.getCountry());
            descriptionLabel.setText(destination.getDescription());
        } else {
            nameLabel.setText("");
            countryLabel.setText("");
            descriptionLabel.setText("");
        }
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
}