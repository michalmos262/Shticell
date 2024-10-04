package client.component.sheet.command;

import engine.entity.cell.EffectiveValue;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.*;

public class CommandsModelUI {
    private final SimpleBooleanProperty isFileLoading;
    private final SimpleBooleanProperty isSheetLoaded;

    public CommandsModelUI(List<TitledPane> titledPaneList, ListView<ListViewEntry> columnsListView,
                           Button showSortedSheetButton) {
        isFileLoading = new SimpleBooleanProperty(false);
        isSheetLoaded = new SimpleBooleanProperty(false);

        bindTitledPanes(titledPaneList);
    }

    public SimpleBooleanProperty isFileLoadingProperty() {
        return isFileLoading;
    }

    public SimpleBooleanProperty isSheetLoadedProperty() {
        return isSheetLoaded;
    }

    private void bindTitledPanes(List<TitledPane> titledPanes) {
        // bind disable property of the titled panes
        for (TitledPane titledPane : titledPanes) {
            titledPane.disableProperty().bind(Bindings.or(isSheetLoaded.not(), isFileLoading));
        }
    }

    public void setColumnsSelectBoxes(Set<String> sheetColumns, List<ListView<ListViewEntry>> listViews) {
        for (ListView<ListViewEntry> listView : listViews) {
            for (String column : sheetColumns) {
                listView.getItems().add(new ListViewEntry(column));
            }

            // Set the cell factory to include a CheckBox in each row
            listView.setCellFactory(lv -> new ListCell<>() {
                private final CheckBox checkBox = new CheckBox();

                @Override
                protected void updateItem(ListViewEntry item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        checkBox.setText(item.getName()); // Set the CheckBox label
                        checkBox.setSelected(item.isSelected()); // Bind the CheckBox selection to the item state

                        // Update the item's selected state when the CheckBox is toggled
                        checkBox.setOnAction(event -> item.setSelected(checkBox.isSelected()));

                        setGraphic(checkBox); // Set the CheckBox as the graphic for the row
                    }
                }
            });
        }
    }

    public void setupFilterValuesTableView(TableView<Map<String, EffectiveValueWrapper>> filterValuesTableView, Map<String, Set<EffectiveValue>> uniqueValuesInColumns) {
        filterValuesTableView.getColumns().clear(); // Clear existing columns

        // List to hold the rows
        ObservableList<Map<String, EffectiveValueWrapper>> tableData = FXCollections.observableArrayList();

        // Build a map of column names to column data (for each row)
        List<Map<String, EffectiveValueWrapper>> rows = new ArrayList<>();
        int maxRowCount = uniqueValuesInColumns.values().stream().mapToInt(Set::size).max().orElse(0);

        // Create empty rows to populate later
        for (int i = 0; i < maxRowCount; i++) {
            rows.add(new HashMap<>());
        }

        // Iterate over each column in the map and populate the rows
        for (Map.Entry<String, Set<EffectiveValue>> entry : uniqueValuesInColumns.entrySet()) {
            String columnName = entry.getKey();
            Set<EffectiveValue> values = entry.getValue();

            TableColumn<Map<String, EffectiveValueWrapper>, Boolean> column = getFilterTableColumn(columnName);

            // Add the column to the TableView
            filterValuesTableView.getColumns().add(column);

            // Populate the rows for this column with its respective values
            int rowIndex = 0;
            for (EffectiveValue value : values) {
                if (rowIndex < rows.size()) {
                    rows.get(rowIndex).put(columnName, new EffectiveValueWrapper(value));
                }
                rowIndex++;
            }
        }

        // Populate the TableView with data
        tableData.addAll(rows);
        filterValuesTableView.setItems(tableData);
    }

    private TableColumn<Map<String, EffectiveValueWrapper>, Boolean> getFilterTableColumn(String columnName) {
        TableColumn<Map<String, EffectiveValueWrapper>, Boolean> column = new TableColumn<>(columnName);

        // Create a cell factory for the checkbox and value display
        column.setCellFactory(tc -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean selected, boolean empty) {
                super.updateItem(selected, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Map<String, EffectiveValueWrapper> rowData = getTableRow().getItem();
                    EffectiveValueWrapper wrapper = rowData.get(columnName);

                    if (wrapper != null) {
                        if (wrapper.getEffectiveValue() == null || wrapper.getEffectiveValue().getValue() == null) {
                            // empty cell
                            checkBox.setText(null);
                        } else {
                            checkBox.setText(wrapper.getEffectiveValue().toString());
                        }
                        checkBox.setSelected(wrapper.isSelected());
                        checkBox.setOnAction(event -> wrapper.setSelected(checkBox.isSelected()));
                        setGraphic(checkBox);
                    } else {
                        setGraphic(null); // No data for this cell
                    }
                }
            }
        });

        // Set the cell value factory to bind the checkboxes
        column.setCellValueFactory(cellData -> {
            EffectiveValueWrapper wrapper = cellData.getValue().get(columnName);
            return wrapper != null ? new SimpleBooleanProperty(wrapper.isSelected()).asObject() : null;
        });
        return column;
    }


    public static class ListViewEntry {
        private final String name;
        private boolean selected;

        public ListViewEntry(String name) {
            this.name = name;
            this.selected = false;
        }

        public String getName() {
            return name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            if (this.selected != selected) {
                this.selected = selected;
            }
        }
    }

    public static class EffectiveValueWrapper {
        private final EffectiveValue value;
        private boolean selected;

        public EffectiveValueWrapper(EffectiveValue value) {
            this.value = value;
            this.selected = false;
        }

        public EffectiveValue getEffectiveValue() {
            return value;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            if (this.selected != selected) {
                this.selected = selected;
            }
        }
    }
}