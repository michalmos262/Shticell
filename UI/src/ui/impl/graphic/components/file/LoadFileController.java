package ui.impl.graphic.components.file;

import engine.api.Engine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;

import java.io.File;

public class LoadFileController {
    @FXML private Label filePathLabel;
    @FXML private Button loadFileButton;

    private MainAppController mainAppController;
    private LoadFileModelUI modelUi;
    private String absoluteFilePath;
    private Engine engine;

    @FXML
    private void initialize() {
        modelUi = new LoadFileModelUI(filePathLabel);
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public String getAbsolutePath() {
        return absoluteFilePath;
    }

    @FXML
    void LoadFileButtonListener(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select an " + Engine.SUPPORTED_FILE_TYPE.toUpperCase() + " file");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(Engine.SUPPORTED_FILE_TYPE.toUpperCase() + " files", "*." + Engine.SUPPORTED_FILE_TYPE)
            );
            File selectedFile = fileChooser.showOpenDialog(mainAppController.getPrimaryStage());
            if (selectedFile == null) {
                return;
            }

            absoluteFilePath = selectedFile.getAbsolutePath();
            modelUi.selectedFileAbsolutePathProperty().set(absoluteFilePath);
            engine.loadFile(absoluteFilePath);
            mainAppController.fileLoaded();
        } catch (Exception e) {
            loadFileFailed(e.getMessage());
        }

    }

    public void loadFileFailed(String errorMessage) {
        AlertsHandler.HandleErrorAlert("Error on loading file", errorMessage);
    }
}