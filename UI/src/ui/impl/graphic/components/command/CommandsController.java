package ui.impl.graphic.components.command;

import engine.api.Engine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ui.impl.graphic.components.app.MainAppController;

import java.util.Arrays;
import java.util.List;

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
    @FXML private TitledPane columnPropsTitledPane;
    @FXML private TitledPane rowPropsTitledPane;
    @FXML private TitledPane cellPropsTitledPane;
    @FXML private TitledPane sortSheetTitledPane;
    @FXML private TitledPane filterSheetTitledPane;

    private MainAppController mainAppController;
    private Engine engine;
    private CommandsModelUI modelUi;

    @FXML
    private void initialize() {
        List<TitledPane> titledPanes = Arrays.asList(columnPropsTitledPane, rowPropsTitledPane, cellPropsTitledPane,
                sortSheetTitledPane, filterSheetTitledPane);
        modelUi = new CommandsModelUI(titledPanes);
    }

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

    public void fileLoadedSuccessfully() {
        modelUi.isSheetLoadedProperty().set(true);
        fileIsLoading(false);
    }

    public void fileIsLoading(boolean isStarted) {
        modelUi.isFileLoadingProperty().set(isStarted);
    }
}