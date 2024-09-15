package ui.impl.graphic.components.command;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;

import java.util.List;
import java.util.Set;

public class CommandsModelUI {
    private final SimpleBooleanProperty isFileLoading;
    private final SimpleBooleanProperty isSheetLoaded;

    public CommandsModelUI(List<TitledPane> titledPaneList, ListView<CommandsModelUI.ListViewEntry> columnsListView,
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

    public void setColumnsSelectBoxes(Set<String> sheetColumns, List<ListView<CommandsModelUI.ListViewEntry>> listViews) {
        for (ListView<CommandsModelUI.ListViewEntry> listView : listViews) {
            for (String column : sheetColumns) {
                listView.getItems().add(new CommandsModelUI.ListViewEntry(column));
            }

            // Set the cell factory to include a CheckBox in each row
            listView.setCellFactory(lv -> new ListCell<>() {
                private final CheckBox checkBox = new CheckBox();

                @Override
                protected void updateItem(CommandsModelUI.ListViewEntry item, boolean empty) {
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
}