package ui.impl.graphic.components.command;

import engine.api.Engine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import ui.impl.graphic.components.app.MainAppController;

public class CommandsController {

    @FXML private ColorPicker cellBackgroundColorPicker;
    @FXML private ChoiceBox<?> cellPositionChoiceBox;
    @FXML private ColorPicker cellTextColorPicker;
    @FXML private ChoiceBox<?> columnChoiceBox;
    @FXML private ChoiceBox<?> columnTextAlignChoiceBox;
    @FXML private Button defaultCellPropsButton;
    @FXML private Button defaultColumnPropsButton;
    @FXML private Button defaultRowPropsButton;
    @FXML private Spinner<?> heightSpinner;
    @FXML private ChoiceBox<?> rowChoiceBox;
    @FXML private Button setCellPropsButton;
    @FXML private Button setColumnPropsButton;
    @FXML private Button setRowPropsButton;
    @FXML private Spinner<?> widthSpinner;

    private MainAppController mainAppController;
    private Engine engine;

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    @FXML
    void defaultCellPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void defaultColumnPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void defaultRowPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void setCellPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void setColumnPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void setRowPropsButtonListener(ActionEvent event) {

    }

    public void fileLoaded() {

    }
}