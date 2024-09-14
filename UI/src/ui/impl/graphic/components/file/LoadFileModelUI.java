package ui.impl.graphic.components.file;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import ui.impl.graphic.task.LoadFileTask;

public class LoadFileModelUI {
    private final SimpleBooleanProperty isFileLoading;
    private final SimpleStringProperty selectedFileAbsolutePath;

    public LoadFileModelUI(Label filePathLabel, Button loadFileButton) {
        selectedFileAbsolutePath = new SimpleStringProperty("");
        isFileLoading = new SimpleBooleanProperty(false);

        filePathLabel.textProperty().bind(selectedFileAbsolutePath);
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