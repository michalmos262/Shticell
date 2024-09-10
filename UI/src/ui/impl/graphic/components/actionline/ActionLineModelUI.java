package ui.impl.graphic.components.actionline;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class ActionLineModelUI {
    private final SimpleBooleanProperty isDataLoaded;
    private final SimpleBooleanProperty isAnyCellClicked;
    private final SimpleStringProperty selectedCellId;
    private final SimpleStringProperty selectedCellOriginalValue;
    private final SimpleIntegerProperty selectedCellLastVersion;
    private final SimpleIntegerProperty currentSheetVersion;

    public ActionLineModelUI(Button updateValueButton, Label selectedCellIdLabel, Label originalCellValueLabel,
                             Label lastCellVersionLabel, ComboBox<Integer> selectSheetVersionSelector) {
        isDataLoaded = new SimpleBooleanProperty(false);
        isAnyCellClicked = new SimpleBooleanProperty(false);
        selectedCellId = new SimpleStringProperty("");
        selectedCellOriginalValue = new SimpleStringProperty("");
        selectedCellLastVersion = new SimpleIntegerProperty();
        currentSheetVersion = new SimpleIntegerProperty();

        updateValueButton.disableProperty().bind(isAnyCellClicked.not());
        selectSheetVersionSelector.disableProperty().bind(isDataLoaded.not());
        selectedCellIdLabel.textProperty().bind(Bindings.concat("Cell ID: ", selectedCellId));
        originalCellValueLabel.textProperty().bind(Bindings.concat("Original Value: ", selectedCellOriginalValue));
        lastCellVersionLabel.textProperty().bind(Bindings.concat("Last Cell Version: ", selectedCellLastVersion));

        currentSheetVersion.addListener((obs, oldValue, newValue) -> {
            if (newValue.equals(1)) {
                selectSheetVersionSelector.getItems().clear();
            }
            selectSheetVersionSelector.getItems().add(newValue.intValue());
        });
    }

    public BooleanProperty isAnyCellClickedProperty() {
        return isAnyCellClicked;
    }

    public SimpleBooleanProperty isDataLoadedProperty() {
        return isDataLoaded;
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
