package client.component.dashboard.loadfile;

import client.component.dashboard.loadfile.task.LoadFileTask;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class LoadFileModelUI {
    private final SimpleBooleanProperty isFileLoading;

    public LoadFileModelUI(TextField filePathTextField, Button loadFileButton) {
        isFileLoading = new SimpleBooleanProperty(false);

        loadFileButton.disableProperty().bind(isFileLoading);
    }

    public SimpleBooleanProperty isFileLoadingProperty() {
        return isFileLoading;
    }

    public void setTaskListener(LoadFileTask loadFileTask, Label loadingProcessLabel, ProgressBar progressBar) {
        loadingProcessLabel.textProperty().bind(loadFileTask.messageProperty());
        progressBar.progressProperty().bind(loadFileTask.progressProperty());
    }
}