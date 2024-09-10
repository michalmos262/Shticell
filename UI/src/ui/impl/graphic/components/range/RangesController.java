package ui.impl.graphic.components.range;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.range.Range;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;

import java.util.List;

public class RangesController {

    @FXML private TextField addFromRangeTextInput;
    @FXML private TitledPane addNewRangeTitledPane;
    @FXML private Button addRangeButton;
    @FXML private TextField addRangeNameTextInput;
    @FXML private TextField addToRangeTextInput;
    @FXML private Button deleteRangeButton;
    @FXML private ChoiceBox<String> deleteRangeNameChoiceBox;
    @FXML private TitledPane deleteRangeTitledPane;
    @FXML private TableView<RangeModelUI.TableEntry> showRangesTable;
    @FXML private TableColumn<RangeModelUI.TableEntry, String> nameColumn;
    @FXML private TableColumn<RangeModelUI.TableEntry, String> rangeColumn;
    @FXML private TitledPane showRangesTitledPane;

    private MainAppController mainAppController;
    private Engine engine;
    private RangeModelUI modelUi;

    @FXML
    private void initialize() {
        modelUi = new RangeModelUI(showRangesTable, nameColumn, rangeColumn);
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public void fileLoaded() {
        showRangesTitledPane.disableProperty().set(false);
        addNewRangeTitledPane.disableProperty().set(false);
        deleteRangeTitledPane.disableProperty().set(false);

        List<String> rangeNames = engine.getRangeNames();
        for (String rangeName : rangeNames) {
            Range range = engine.getRangeByName(rangeName);
            modelUi.addRange(rangeName, range);
        }
    }

    @FXML
    void AddRangeButtonListener(ActionEvent event) {
        String alertTitle = "Add range";
        try {
            if (!addRangeNameTextInput.getText().isEmpty()) {
                String rangeName = addRangeNameTextInput.getText();
                CellPositionInSheet fromPosition = PositionFactory.createPosition(addFromRangeTextInput.getText());
                CellPositionInSheet toPosition = PositionFactory.createPosition(addToRangeTextInput.getText());
                engine.createRange(rangeName, fromPosition, toPosition);
                modelUi.addRange(rangeName, engine.getRangeByName(rangeName));
                AlertsHandler.HandleOkAlert("Range " + addRangeNameTextInput.getText() + " added successfully!");
            } else {
                AlertsHandler.HandleErrorAlert(alertTitle, "Range name cannot be empty");
            }
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert(alertTitle, e.getMessage());
        }
    }

    @FXML
    void deleteRangeButtonListener(ActionEvent event) {

    }
}