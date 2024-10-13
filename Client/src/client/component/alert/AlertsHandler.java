package client.component.alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertsHandler {
    public static void HandleErrorAlert(String alertTitle, String alertMessage) {
        Alert dialog = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
        dialog.setTitle(alertTitle);
        dialog.setHeaderText(alertMessage);
        dialog.setContentText("Please try again!");
        dialog.showAndWait();
    }

    public static void HandleOkAlert(String alertMessage) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setHeaderText(alertMessage);
        dialog.getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }
}
