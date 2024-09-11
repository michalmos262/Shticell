package ui.impl.graphic.components.range;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.range.Range;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
        modelUi = new RangeModelUI(showRangesTable, nameColumn, rangeColumn, deleteRangeNameChoiceBox);
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public void fileLoaded() {
        showRangesTitledPane.disableProperty().set(false);
        addNewRangeTitledPane.disableProperty().set(false);
        deleteRangeTitledPane.disableProperty().set(false);
        modelUi.resetRanges();

        List<String> rangeNames = engine.getRangeNames();
        for (String rangeName : rangeNames) {
            Range range = engine.getRangesByName(rangeName);
            modelUi.addRange(rangeName, range);
        }
    }

    private void resetAddRangeTextInputs() {
        addRangeNameTextInput.setText("");
        addToRangeTextInput.setText("");
        addFromRangeTextInput.setText("");
    }

    @FXML
    void addRangeButtonListener(ActionEvent event) {
        String alertTitle = "Add range";
        try {
            String rangeName = addRangeNameTextInput.getText();

            if (!rangeName.isEmpty()) {
                CellPositionInSheet fromPosition = PositionFactory.createPosition(addFromRangeTextInput.getText());
                CellPositionInSheet toPosition = PositionFactory.createPosition(addToRangeTextInput.getText());
                engine.createRange(rangeName, fromPosition, toPosition);
                modelUi.addRange(rangeName, engine.getRangesByName(rangeName));
                resetAddRangeTextInputs();
                AlertsHandler.HandleOkAlert("Range " + rangeName + " added successfully!");
            } else {
                AlertsHandler.HandleErrorAlert(alertTitle, "Range name cannot be empty");
            }
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert(alertTitle, e.getMessage());
        }
    }

    @FXML
    void deleteRangeButtonListener(ActionEvent event) {
        try {
            String rangeName = deleteRangeNameChoiceBox.getValue();
            engine.deleteRange(rangeName);
            modelUi.removeRange(rangeName);
            deleteRangeNameChoiceBox.setValue(null); // clean current choice
            AlertsHandler.HandleOkAlert("Range " + rangeName + " deleted successfully!");
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Delete range", e.getMessage());
        }
    }

    @FXML
    void tableViewOnMouseClickedListener(MouseEvent event) {
        RangeModelUI.TableEntry selectedRow = showRangesTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            // Logic when a row is clicked
            System.out.println("Selected Row: " + selectedRow.nameProperty());
            // Add your logic here
        }
    }
}