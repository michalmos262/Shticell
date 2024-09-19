package ui.impl.graphic.components.file;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import ui.impl.graphic.task.LoadFileTask;

public class LoadFileModelUI {
    private final SimpleBooleanProperty isFileLoading;
    private final SimpleStringProperty selectedFileAbsolutePath;

    public LoadFileModelUI(TextField filePathTextField, Button loadFileButton) {
        selectedFileAbsolutePath = new SimpleStringProperty("");
        isFileLoading = new SimpleBooleanProperty(false);

        filePathTextField.textProperty().bind(selectedFileAbsolutePath);
        loadFileButton.disableProperty().bind(isFileLoading);
    }

    public SimpleBooleanProperty isFileLoadingProperty() {
        return isFileLoading;
    }

    public SimpleStringProperty selectedFileAbsolutePathProperty() {
        return selectedFileAbsolutePath;
    }

    public void setTaskListener(LoadFileTask loadFileTask, Label loadingProcessLabel, ProgressBar progressBar) {
        loadingProcessLabel.textProperty().bind(loadFileTask.messageProperty());
        progressBar.progressProperty().bind(loadFileTask.progressProperty());
    }
}