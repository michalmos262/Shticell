package ui.impl.graphic.components.actionline;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.InputMethodEvent;
import ui.impl.graphic.components.app.MainAppController;

public class ActionLineController {

    @FXML private Label lastCellVersionLabel;
    @FXML private Label originalCellValueLabel;
    @FXML private Label selectedCellIdLabel;
    @FXML private Button updateValueButton;

    private MainAppController mainAppController;

    public void setMainController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void setLabels(SimpleStringProperty selectedCellIdProperty, SimpleStringProperty originalCellValueProperty, SimpleIntegerProperty lastCellVersionProperty) {
        selectedCellIdLabel.textProperty().bind(Bindings.concat("Cell ID: ", selectedCellIdProperty));
        originalCellValueLabel.textProperty().bind(Bindings.concat("Original Value: ", originalCellValueProperty));
        lastCellVersionLabel.textProperty().bind(Bindings.concat("Last Cell Version: ", lastCellVersionProperty));
    }

    @FXML
    void UpdateValueButtonListener(ActionEvent event) {

    }

}
