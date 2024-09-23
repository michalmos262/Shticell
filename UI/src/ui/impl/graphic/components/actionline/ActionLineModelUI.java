package ui.impl.graphic.components.actionline;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.util.List;

public class ActionLineModelUI {
    private final SimpleBooleanProperty isFileLoading;
    private final SimpleBooleanProperty isAnyCellClicked;
    private final SimpleStringProperty selectedCellId;
    private final SimpleStringProperty selectedCellOriginalValue;
    private final SimpleIntegerProperty selectedCellLastVersion;
    private final SimpleIntegerProperty currentSheetVersion;

    public ActionLineModelUI(List<Button> cellButtons, Label selectedCellIdLabel, TextField originalCellValueTextField,
                             Label lastCellVersionLabel, ChoiceBox<Integer> showSheetVersionSelector,
                             ChoiceBox<Pos> columnTextAlignmentChoiceBox, Button showSheetVersionButton,
                             Spinner<Integer> columnWidthSpinner, Spinner<Integer> rowHeightSpinner,
                             ColorPicker cellBackgroundColorPicker, ColorPicker cellTextColorPicker,
                             ComboBox<String> systemSkinComboBox) {

        isFileLoading = new SimpleBooleanProperty(false);
        isAnyCellClicked = new SimpleBooleanProperty(false);
        selectedCellId = new SimpleStringProperty("");
        selectedCellOriginalValue = new SimpleStringProperty("");
        selectedCellLastVersion = new SimpleIntegerProperty();
        currentSheetVersion = new SimpleIntegerProperty(0);

        for (Button button : cellButtons) {
            button.disableProperty().bind(Bindings.or(isAnyCellClicked.not(), isFileLoading));
        }

        showSheetVersionButton.disableProperty().bind(Bindings.or(currentSheetVersion.isEqualTo(0), isFileLoading));
        showSheetVersionSelector.disableProperty().bind(Bindings.or(currentSheetVersion.isEqualTo(0), isFileLoading));
        systemSkinComboBox.disableProperty().bind(isFileLoading);

        originalCellValueTextField.disableProperty().bind(Bindings.or(isAnyCellClicked.not(), isFileLoading));
        columnTextAlignmentChoiceBox.disableProperty().bind(Bindings.or(isAnyCellClicked.not(), isFileLoading));
        columnWidthSpinner.disableProperty().bind(Bindings.or(isAnyCellClicked.not(), isFileLoading));
        rowHeightSpinner.disableProperty().bind(Bindings.or(isAnyCellClicked.not(), isFileLoading));
        cellBackgroundColorPicker.disableProperty().bind(Bindings.or(isAnyCellClicked.not(), isFileLoading));
        cellTextColorPicker.disableProperty().bind(Bindings.or(isAnyCellClicked.not(), isFileLoading));

        selectedCellIdLabel.textProperty().bind(selectedCellId);
        originalCellValueTextField.textProperty().bindBidirectional(selectedCellOriginalValue);
        lastCellVersionLabel.textProperty().bind(Bindings.concat("Last Cell Version: ", selectedCellLastVersion));

        currentSheetVersion.addListener((obs, oldValue, newValue) -> {
            if (newValue.equals(1)) {
                showSheetVersionSelector.getItems().clear();
            }
            showSheetVersionSelector.getItems().add(newValue.intValue());
        });
    }

    public SimpleBooleanProperty isFileLoadingProperty() {
        return isFileLoading;
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
