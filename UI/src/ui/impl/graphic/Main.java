package ui.impl.graphic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.impl.graphic.components.app.MainAppController;
import ui.impl.graphic.model.BusinessLogic;

import java.net.URL;
import java.util.Objects;

import static ui.impl.graphic.resources.CommonResourcesPaths.GRID_CSS_RESOURCE;
import static ui.impl.graphic.resources.CommonResourcesPaths.MAIN_APP_FXML_RESOURCE;

public class Main extends Application {

    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();

        // load main fxml
        URL url = getClass().getResource(MAIN_APP_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        // wire up controller
        MainAppController mainAppController = fxmlLoader.getController();
        BusinessLogic businessLogic = new BusinessLogic(mainAppController);
        mainAppController.setPrimaryStage(primaryStage);
        mainAppController.setBusinessLogic(businessLogic);

        // set stage
        primaryStage.setTitle("Shticell");
        Scene scene = new Scene(root, 1100, 770);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}