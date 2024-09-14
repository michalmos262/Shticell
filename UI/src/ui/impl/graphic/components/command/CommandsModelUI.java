package ui.impl.graphic.components.command;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

import java.util.List;

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

    public static class ListViewEntry {
        private final String name;
        private final SimpleBooleanProperty selected;

        public ListViewEntry(String name) {
            this.name = name;
            this.selected = new SimpleBooleanProperty(false);
        }

        public String getName() {
            return name;
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }
    }
}