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
    @FXML private Label FilePathLabel;
    @FXML private Button LoadFileButton;

    private MainAppController mainAppController;
    private String absoluteFilePath;

    public void setMainController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public String getAbsolutePath() {
        return absoluteFilePath;
    }

    @FXML
    void LoadFileButtonListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an " + Engine.SUPPORTED_FILE_TYPE.toUpperCase() + " file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(Engine.SUPPORTED_FILE_TYPE.toUpperCase() + " files", "*." + Engine.SUPPORTED_FILE_TYPE)
        );
        File selectedFile = fileChooser.showOpenDialog(mainAppController.getPrimaryStage());
        if (selectedFile == null) {
            return;
        }

        this.absoluteFilePath = selectedFile.getAbsolutePath();
        // ask from main controller for permission to approve file
        mainAppController.loadFile();
        if (mainAppController.getIsFileSelected()) {
            FilePathLabel.setText(absoluteFilePath);
        }
    }

    public void loadFileFailed(String errorMessage) {
        AlertsHandler.HandleErrorAlert("Error on loading file", errorMessage);
    }
}