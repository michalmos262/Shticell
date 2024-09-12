package ui.impl.graphic.components.file;

import engine.api.Engine;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;
import ui.impl.graphic.task.LoadFileTask;

import java.io.File;

public class LoadFileController {
    @FXML private Label filePathLabel;
    @FXML private Button loadFileButton;
    @FXML private Label loadingProcessLabel;
    @FXML private ProgressBar progressBar;

    private MainAppController mainAppController;
    private LoadFileModelUI modelUi;
    private Engine engine;
    private LoadFileTask loadFileTask;

    @FXML
    private void initialize() {
        modelUi = new LoadFileModelUI(filePathLabel, loadFileButton);
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    @FXML
    void loadFileButtonListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an " + Engine.SUPPORTED_FILE_TYPE.toUpperCase() + " file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(Engine.SUPPORTED_FILE_TYPE.toUpperCase() + " files", "*." + Engine.SUPPORTED_FILE_TYPE)
        );
        File selectedFile = fileChooser.showOpenDialog(mainAppController.getPrimaryStage());
        if (selectedFile == null) {
            return;
        }
        String selectedFileName = selectedFile.getAbsolutePath();
        modelUi.isFileLoadingProperty().set(true);
        mainAppController.fileIsLoading();

        loadFileTask = new LoadFileTask(
            selectedFileName,
            file -> {
                try {
                    engine.loadFile(selectedFileName); // Load the file logic
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage()); // Pass the exception
                }
            },
            onFinish -> {
                modelUi.selectedFileAbsolutePathProperty().setValue(selectedFileName);
                modelUi.isFileLoadingProperty().set(false);
                mainAppController.fileLoadedSuccessfully();
            }
        );

        // Handle task failure
        loadFileTask.setOnFailed(eventFailed -> {
            Throwable exception = loadFileTask.getException(); // Get the exception that caused the failure
            if (exception != null) {
                Platform.runLater(() -> {
                    loadFileFailed(exception.getMessage()); // Call failure handler on the JavaFX thread
                    modelUi.isFileLoadingProperty().set(false);
                    mainAppController.fileFailedLoading();
                });
            }
        });

        modelUi.setTaskListener(loadFileTask, loadingProcessLabel, progressBar);
        new Thread(loadFileTask).start();
    }

    public void loadFileFailed(String errorMessage) {
        AlertsHandler.HandleErrorAlert("Error on loading file", errorMessage);
    }
}