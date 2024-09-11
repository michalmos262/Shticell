package ui.impl.graphic.components.range;

import engine.entity.range.Range;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableMap;

public class RangeModelUI {
    private final ObservableMap<SimpleStringProperty, Range> nameProperty2range;

    public RangeModelUI(TableView<TableEntry> showRangesTable, TableColumn<TableEntry, String> nameColumn,
                        TableColumn<RangeModelUI.TableEntry, String> rangeColumn, ChoiceBox<String> deleteRangeNameChoiceBox) {

         // Create an ObservableMap for SimpleStringProperty and Range
        nameProperty2range = FXCollections.observableHashMap();

        // Initialize the columns in the TableView
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        rangeColumn.setCellValueFactory(cellData -> cellData.getValue().rangeProperty());

        // Create an ObservableList to hold the data for the TableView
        ObservableList<TableEntry> tableData = FXCollections.observableArrayList();

        // Set the items for the TableView
        showRangesTable.setItems(tableData);

        // Update the ChoiceBox
        ObservableList<String> choiceBoxData = deleteRangeNameChoiceBox.getItems();

        // Add a MapChangeListener to the map to listen for new entries
        nameProperty2range.addListener((MapChangeListener<SimpleStringProperty, Range>) change -> {
            SimpleStringProperty nameProperty = change.getKey();

            if (change.wasAdded()) {
                Range addedRange = change.getValueAdded();
                tableData.add(new TableEntry(nameProperty.get(), addedRange));
                choiceBoxData.add(nameProperty.get());
            }
            if (change.wasRemoved()) {
                // Find the corresponding TableEntry by name and remove it
                tableData.removeIf(entry -> entry.nameProperty().get().equals(nameProperty.get()));
                // Remove the entry from the ChoiceBox
                choiceBoxData.remove(nameProperty.get());
            }
        });
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