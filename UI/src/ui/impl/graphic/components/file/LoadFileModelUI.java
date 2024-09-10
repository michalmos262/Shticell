package ui.impl.graphic.components.file;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;

public class LoadFileModelUI {
    private final SimpleStringProperty selectedFileAbsolutePath;

    public LoadFileModelUI(Label filePathLabel) {
        selectedFileAbsolutePath = new SimpleStringProperty("");
        filePathLabel.textProperty().bind(selectedFileAbsolutePath);
    }

    public SimpleStringProperty selectedFileAbsolutePathProperty() {
        return selectedFileAbsolutePath;
    }
}
