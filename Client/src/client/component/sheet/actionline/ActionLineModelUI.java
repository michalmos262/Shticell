package client.component.sheet.actionline;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.util.List;

public class ActionLineModelUI {
    private final BooleanProperty isAnyCellClicked;
    private final StringProperty selectedCellId;
    private final StringProperty selectedCellOriginalValue;
    private final IntegerProperty selectedCellLastVersion;
    private final StringProperty selectedUpdatedByName;
    private final IntegerProperty currentSheetVersion;
    private final BooleanProperty isUserWriter;

    public ActionLineModelUI(Button dynamicAnalysisButton, List<Button> writerOnlyButtons,
                             TextField originalCellValueTextField,
                             Label selectedCellIdLabel, Label lastCellVersionLabel, Label updatedByLabel,
                             ChoiceBox<Integer> showSheetVersionSelector, ChoiceBox<Pos> columnTextAlignmentChoiceBox,
                             Spinner<Integer> columnWidthSpinner, Spinner<Integer> rowHeightSpinner,
                             ColorPicker cellBackgroundColorPicker, ColorPicker cellTextColorPicker) {

        isAnyCellClicked = new SimpleBooleanProperty(false);
        selectedCellId = new SimpleStringProperty("");
        selectedCellOriginalValue = new SimpleStringProperty("");
        selectedCellLastVersion = new SimpleIntegerProperty();
        selectedUpdatedByName = new SimpleStringProperty("");
        currentSheetVersion = new SimpleIntegerProperty(0);
        isUserWriter = new SimpleBooleanProperty(false);

        dynamicAnalysisButton.disableProperty().bind(isAnyCellClicked.not());

        for (Button button : writerOnlyButtons) {
            button.disableProperty().bind(isUserWriter.not().or(isAnyCellClicked.not()));
        }

        originalCellValueTextField.disableProperty().bind(isAnyCellClicked.not().or(isUserWriter.not()));
        columnTextAlignmentChoiceBox.disableProperty().bind(isAnyCellClicked.not().or(isUserWriter.not()));
        columnWidthSpinner.disableProperty().bind(isAnyCellClicked.not().or(isUserWriter.not()));
        rowHeightSpinner.disableProperty().bind(isAnyCellClicked.not().or(isUserWriter.not()));
        cellBackgroundColorPicker.disableProperty().bind(isAnyCellClicked.not().or(isUserWriter.not()));
        cellTextColorPicker.disableProperty().bind(isAnyCellClicked.not().or(isUserWriter.not()));

        selectedCellIdLabel.textProperty().bind(selectedCellId);
        originalCellValueTextField.textProperty().bindBidirectional(selectedCellOriginalValue);
        originalCellValueTextField.disableProperty().bind(isUserWriter.not());
        lastCellVersionLabel.textProperty().bind(Bindings.concat("Last Cell Version: ", selectedCellLastVersion));
        updatedByLabel.textProperty().bind(Bindings.concat("Updated by: ", selectedUpdatedByName));

        currentSheetVersion.addListener((obs, oldValue, newValue) -> {
            int oldVersion = oldValue.intValue();
            int newVersion = newValue.intValue();

            // maybe there is a big gap between last version to the newest version
            while (oldVersion != newVersion) {
                oldVersion++;
                showSheetVersionSelector.getItems().add(oldVersion);
            }

            currentSheetVersion.set(newVersion);
        });
    }

    public BooleanProperty isAnyCellClickedProperty() {
        return isAnyCellClicked;
    }

    public StringProperty selectedCellIdProperty() {
        return selectedCellId;
    }

    public StringProperty selectedCellOriginalValueProperty() {
        return selectedCellOriginalValue;
    }

    public IntegerProperty selectedCellLastVersionProperty() {
        return selectedCellLastVersion;
    }

    public StringProperty selectedUpdatedByNameProperty() {
        return selectedUpdatedByName;
    }

    public IntegerProperty currentSheetVersionProperty() {
        return currentSheetVersion;
    }

    public BooleanProperty isUserWriterProperty() {
        return isUserWriter;
    }
}
