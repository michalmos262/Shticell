package client.component.dashboard;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

public class DashboardModelUI {
    StringProperty username;

    public DashboardModelUI(Label usernameLabel) {
        username = new SimpleStringProperty();
        usernameLabel.textProperty().bind(Bindings.concat("Logged in as: ", username));
    }
}
