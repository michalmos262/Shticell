package ui.impl.graphic.components.range;

import engine.entity.range.Range;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

public class RangeModelUI {
    private final ObservableMap<SimpleStringProperty, Range> nameProperty2range;

    public RangeModelUI(TableView<TableEntry> showRangesTable, TableColumn<TableEntry, String> nameColumn,
                        TableColumn<RangeModelUI.TableEntry, String> rangeColumn) {

         // Create an ObservableMap for SimpleStringProperty and Range
        nameProperty2range = FXCollections.observableHashMap();

        // Initialize the columns in the TableView
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        rangeColumn.setCellValueFactory(cellData -> cellData.getValue().rangeProperty());

        // Create an ObservableList to hold the data for the TableView
        ObservableList<TableEntry> tableData = FXCollections.observableArrayList();

        // Set the items for the TableView
        showRangesTable.setItems(tableData);

        // Add a MapChangeListener to the map to listen for new entries
        nameProperty2range.addListener((MapChangeListener<SimpleStringProperty, Range>) change -> {
            if (change.wasAdded()) {
                // Add the new entry to the TableView
                SimpleStringProperty nameProperty = change.getKey();
                Range range = change.getValueAdded();
                tableData.add(new TableEntry(nameProperty.get(), range));
            }
        });
    }

    public void addRange(String name, Range range) {
        nameProperty2range.put(new SimpleStringProperty(name), range);
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