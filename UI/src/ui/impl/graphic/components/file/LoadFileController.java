package ui.impl.graphic.components.file;

import engine.api.Engine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;
import ui.impl.graphic.model.BusinessLogic;

import java.io.File;

public class LoadFileController {
    @FXML private Label filePathLabel;
    @FXML private Button loadFileButton;

    private MainAppController mainAppController;
    private String absoluteFilePath;

    public void setMainController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void bindToModel(BusinessLogic modelUi) {
        filePathLabel.textProperty().bind(modelUi.selectedFileAbsolutePathProperty());
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
        mainAppController.loadFile();
    }

    public void loadFileFailed(String errorMessage) {
        AlertsHandler.HandleErrorAlert("Error on loading file", errorMessage);
    }
}