package ui.impl.graphic.components.file;

import engine.api.Engine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import ui.impl.graphic.components.app.MainAppController;

import java.io.File;

public class LoadFileController {
    @FXML private Label FilePathLabel;
    @FXML private Button LoadFileButton;

    private MainAppController mainAppController;

    public void setMainController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    @FXML
    void LoadFileButtonListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select words file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("text files", "*." + Engine.SUPPORTED_FILE_TYPE)
        );
        File selectedFile = fileChooser.showOpenDialog(mainAppController.getPrimaryStage());
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        FilePathLabel.setText(absolutePath);
        mainAppController.loadFile();
    }
}