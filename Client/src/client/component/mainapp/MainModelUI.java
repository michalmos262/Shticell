package client.component.mainapp;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;

public class MainModelUI {
    private final StringProperty username;
    private final StringProperty pageHeading;

    public MainModelUI(SplitPane mainSplitPane, Label headingLabel, Label loggedInAsLabel) {
        pageHeading = new SimpleStringProperty("");
        username = new SimpleStringProperty("guest");

        headingLabel.textProperty().bind(pageHeading);
        loggedInAsLabel.textProperty().bind(Bindings.concat("Hello, ", username));

        // disable the split pane divider from moving
        SplitPane.Divider divider = mainSplitPane.getDividers().getFirst();
        divider.positionProperty().addListener((observable, oldValue, newValue) -> divider.setPosition(0.0456));
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty pageHeadingProperty() {
        return pageHeading;
    }
}