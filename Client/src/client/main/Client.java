package client.main;

import client.component.mainapp.MainAppController;
import client.util.http.HttpClientUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static client.resources.CommonResourcesPaths.MAIN_APP_FXML_RESOURCE_LOCATION;

public class Client extends Application {
    private MainAppController mainAppController;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.setTitle("Shticell");

        URL loginPage = getClass().getResource(MAIN_APP_FXML_RESOURCE_LOCATION);
        System.out.println("FXML Location: " + loginPage); // Debug check

        if (loginPage == null) {
            System.out.println("Failed to locate FXML file: " + MAIN_APP_FXML_RESOURCE_LOCATION);
            return; // Exit if FXML not found
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPage);
            Parent root = fxmlLoader.load();

            if (root == null) {
                System.out.println("Root node is null after loading FXML.");
                return; // Exit if root is null
            }

            mainAppController = fxmlLoader.getController();
            System.out.println("Controller loaded: " + (mainAppController != null)); // Debug check

            Scene scene = new Scene(root, 1162, 790);
            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("Primary stage shown."); // Confirm stage is shown
        } catch (IOException e) {
            System.out.println("Error loading FXML: " + e.getMessage());
            System.out.println(e.getMessage()); // Print full stack trace
        }
    }

    @Override
    public void stop() {
        mainAppController.close();
        HttpClientUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}