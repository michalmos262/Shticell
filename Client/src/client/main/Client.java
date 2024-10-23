package client.main;

import client.component.mainapp.MainAppController;
import client.util.http.HttpClientUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.resources.CommonResourcesPaths.MAIN_APP_FXML_RESOURCE_LOCATION;

public class Client extends Application {
    private MainAppController mainAppController;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.setTitle("Shticell");

        URL loginPage = getClass().getResource(MAIN_APP_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPage);
            Parent root = fxmlLoader.load();
            mainAppController = fxmlLoader.getController();

            Scene scene = new Scene(root, 1162, 790);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void stop() {
        mainAppController.close();
        HttpClientUtil.shutdown();
    }

    public static void main(String[] args) {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        launch(args);
    }
}