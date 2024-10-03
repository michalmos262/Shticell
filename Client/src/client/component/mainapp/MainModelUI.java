package client.component.mainapp;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

public class MainModelUI {
    private final StringProperty username;

    public MainModelUI(Label loggedInAsLabel) {
        username = new SimpleStringProperty();
        loggedInAsLabel.textProperty().bind(Bindings.concat("Logged in as: ", username));
    }

    public StringProperty usernameProperty() {
        return username;
    }
}