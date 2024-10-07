package client.component.sheet.actionline;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.util.List;

public class ActionLineModelUI {
    private final SimpleBooleanProperty isAnyCellClicked;
    private final SimpleStringProperty selectedCellId;
    private final SimpleStringProperty selectedCellOriginalValue;
    private final SimpleIntegerProperty selectedCellLastVersion;
    private final SimpleIntegerProperty currentSheetVersion;

    public ActionLineModelUI(List<Button> cellButtons, Label selectedCellIdLabel, TextField originalCellValueTextField,
                             Label lastCellVersionLabel, ChoiceBox<Integer> showSheetVersionSelector,
                             ChoiceBox<Pos> columnTextAlignmentChoiceBox,
                             Spinner<Integer> columnWidthSpinner, Spinner<Integer> rowHeightSpinner,
                             ColorPicker cellBackgroundColorPicker, ColorPicker cellTextColorPicker) {

        isAnyCellClicked = new SimpleBooleanProperty(false);
        selectedCellId = new SimpleStringProperty("");
        selectedCellOriginalValue = new SimpleStringProperty("");
        selectedCellLastVersion = new SimpleIntegerProperty();
        currentSheetVersion = new SimpleIntegerProperty(0);

        for (Button button : cellButtons) {
            button.disableProperty().bind(isAnyCellClicked.not());
        }

        originalCellValueTextField.disableProperty().bind(isAnyCellClicked.not());
        columnTextAlignmentChoiceBox.disableProperty().bind(isAnyCellClicked.not());
        columnWidthSpinner.disableProperty().bind(isAnyCellClicked.not());
        rowHeightSpinner.disableProperty().bind(isAnyCellClicked.not());
        cellBackgroundColorPicker.disableProperty().bind(isAnyCellClicked.not());
        cellTextColorPicker.disableProperty().bind(isAnyCellClicked.not());

        selectedCellIdLabel.textProperty().bind(selectedCellId);
        originalCellValueTextField.textProperty().bindBidirectional(selectedCellOriginalValue);
        lastCellVersionLabel.textProperty().bind(Bindings.concat("Last Cell Version: ", selectedCellLastVersion));

        currentSheetVersion.addListener((obs, oldValue, newValue) ->
                showSheetVersionSelector.getItems().add(newValue.intValue()));
    }

    public BooleanProperty isAnyCellClickedProperty() {
        return isAnyCellClicked;
    }

    public SimpleStringProperty selectedCellIdProperty() {
        return selectedCellId;
    }

    public SimpleStringProperty selectedCellOriginalValueProperty() {
        return selectedCellOriginalValue;
    }

    public SimpleIntegerProperty selectedCellLastVersionProperty() {
        return selectedCellLastVersion;
    }

    public SimpleIntegerProperty currentSheetVersionProperty() {
        return currentSheetVersion;
    }
}
