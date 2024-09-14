package ui.impl.graphic.components.command;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TitledPane;

import java.util.List;

public class CommandsModelUI {
    private final SimpleBooleanProperty isFileLoading;
    private final SimpleBooleanProperty isSheetLoaded;

    public CommandsModelUI(List<TitledPane> titledPaneList) {
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
}