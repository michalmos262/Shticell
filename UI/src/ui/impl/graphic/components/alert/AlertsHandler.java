package ui.impl.graphic.components.alert;

public class AlertsHandler {
    public static void HandleErrorAlert(String errorTitle, String errorMessage) {
        javafx.scene.control.Alert dialog = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        dialog.setTitle(errorTitle);
        dialog.setHeaderText(errorMessage);
        dialog.setContentText("Please try again!");
        dialog.showAndWait();
    }
}
