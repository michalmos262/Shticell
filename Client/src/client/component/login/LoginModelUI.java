package client.component.login;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

public class LoginModelUI {
    private final StringProperty errorMessage;

    public LoginModelUI(Label errorMessageLabel) {
        errorMessage = new SimpleStringProperty();
        errorMessageLabel.textProperty().bind(errorMessage);
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }
}
