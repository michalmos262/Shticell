package client.component.sheet.range;

import engine.entity.range.Range;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.collections.ObservableMap;

import java.util.List;

public class RangeModelUI {
    private final SimpleBooleanProperty isFileLoading;
    private final ObservableMap<SimpleStringProperty, Range> nameProperty2range;
    private final ObservableList<TableEntry> rangesTableData;
    private final SimpleBooleanProperty isRangeAdded;

    public RangeModelUI(TableView<TableEntry> showRangesTable, TableColumn<TableEntry, String> nameColumn,
                        TableColumn<RangeModelUI.TableEntry, String> rangeColumn, ChoiceBox<String> deleteRangeNameChoiceBox,
                        List<TitledPane> titledPanes, List<TextField> textFields) {

        isFileLoading = new SimpleBooleanProperty(false);
        isRangeAdded = new SimpleBooleanProperty(false);
        nameProperty2range = FXCollections.observableHashMap();
        rangesTableData = FXCollections.observableArrayList();

        bindTableView(showRangesTable, nameColumn, rangeColumn, deleteRangeNameChoiceBox);
        bindTitledPanes(titledPanes);
        bindTextFields(textFields);
    }

    private void bindTableView(TableView<TableEntry> showRangesTable, TableColumn<TableEntry, String> nameColumn,
                        TableColumn<TableEntry, String> rangeColumn, ChoiceBox<String> deleteRangeNameChoiceBox) {
        // Initialize the columns in the TableView
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        rangeColumn.setCellValueFactory(cellData -> cellData.getValue().rangeProperty());

        // bind the ranges data to the table
        showRangesTable.setItems(rangesTableData);
        // bind the ranges names to the delete ranges select box
        ObservableList<String> deleteRangeNameChoiceBoxData = deleteRangeNameChoiceBox.getItems();

        // Add a MapChangeListener to the map to listen for new entries
        nameProperty2range.addListener((MapChangeListener<SimpleStringProperty, Range>) change -> {
            SimpleStringProperty nameProperty = change.getKey();

            if (change.wasAdded()) {
                Range addedRange = change.getValueAdded();
                rangesTableData.add(new TableEntry(nameProperty.get(), addedRange));
                deleteRangeNameChoiceBoxData.add(nameProperty.get());
            }
            if (change.wasRemoved()) {
                // Find the corresponding TableEntry by name and remove it
                rangesTableData.removeIf(entry -> entry.nameProperty().get().equals(nameProperty.get()));
                // Remove the entry from the ChoiceBox
                deleteRangeNameChoiceBoxData.remove(nameProperty.get());
            }
        });
    }

    private void bindTextFields(List<TextField> textFields) {
        // when range is added, the add range fields will clear
        for (TextField textField : textFields) {
            isRangeAdded.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                    textField.clear();
                }
            });
        }
    }

    public SimpleBooleanProperty isFileLoadingProperty() {
        return isFileLoading;
    }

    private void bindTitledPanes(List<TitledPane> titledPanes) {
        // bind disable property of the titled panes
        for (TitledPane titledPane : titledPanes) {
            titledPane.disableProperty().bind(Bindings.or(Bindings.isEmpty(nameProperty2range), isFileLoading));
        }
    }

    public SimpleBooleanProperty isRangeAddedProperty() {
        return isRangeAdded;
    }

    public void addRange(String name, Range range) {
        nameProperty2range.put(new SimpleStringProperty(name), range);
    }

    public void removeRange(String name) {
        // Find the key with the matching name
        SimpleStringProperty keyToRemove = null;
        for (SimpleStringProperty key : nameProperty2range.keySet()) {
            if (key.get().equals(name)) {
                keyToRemove = key;
                break;
            }
        }
        if (keyToRemove != null) {
            nameProperty2range.remove(keyToRemove);
        }
    }

    public void resetRanges() {
        nameProperty2range.clear();
    }

    public static class TableEntry {
        private final SimpleStringProperty name;
        private final SimpleStringProperty range;

        public TableEntry(String name, Range range) {
            this.name = new SimpleStringProperty(name);
            this.range = new SimpleStringProperty(range.toString());
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public SimpleStringProperty rangeProperty() {
            return range;
        }
    }
}