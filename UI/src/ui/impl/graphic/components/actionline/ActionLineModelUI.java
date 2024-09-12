package ui.impl.graphic.components.actionline;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class ActionLineModelUI {
    private final SimpleBooleanProperty isFileLoading;
    private final SimpleBooleanProperty isAnyCellClicked;
    private final SimpleStringProperty selectedCellId;
    private final SimpleStringProperty selectedCellOriginalValue;
    private final SimpleIntegerProperty selectedCellLastVersion;
    private final SimpleIntegerProperty currentSheetVersion;

    public ActionLineModelUI(Button updateValueButton, Label selectedCellIdLabel, Label originalCellValueLabel,
                             Label lastCellVersionLabel, ChoiceBox<Integer> showSheetVersionSelector,
                             Button showSheetVersionButton) {
        isFileLoading = new SimpleBooleanProperty(false);
        isAnyCellClicked = new SimpleBooleanProperty(false);
        selectedCellId = new SimpleStringProperty("");
        selectedCellOriginalValue = new SimpleStringProperty("");
        selectedCellLastVersion = new SimpleIntegerProperty();
        currentSheetVersion = new SimpleIntegerProperty(0);

        updateValueButton.disableProperty().bind(Bindings.or(isAnyCellClicked.not(), isFileLoading));
        selectedCellIdLabel.textProperty().bind(Bindings.concat("Cell ID: ", selectedCellId));
        originalCellValueLabel.textProperty().bind(Bindings.concat("Original Value: ", selectedCellOriginalValue));
        lastCellVersionLabel.textProperty().bind(Bindings.concat("Last Cell Version: ", selectedCellLastVersion));
        showSheetVersionButton.disableProperty().bind(Bindings.or(currentSheetVersion.isEqualTo(0), isFileLoading));
        showSheetVersionSelector.disableProperty().bind(Bindings.or(currentSheetVersion.isEqualTo(0), isFileLoading));

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
