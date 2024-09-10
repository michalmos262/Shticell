package ui.impl.graphic.components.actionline;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.CellDto;
import engine.operation.Operation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;

public class ActionLineController {

    @FXML private Label lastCellVersionLabel;
    @FXML private Label originalCellValueLabel;
    @FXML private Label selectedCellIdLabel;
    @FXML private Button updateValueButton;
    @FXML private ComboBox<Integer> selectSheetVersionSelector;

    private MainAppController mainAppController;
    private ActionLineModelUI modelUi;
    private Engine engine;

    @FXML
    private void initialize() {
        modelUi = new ActionLineModelUI(updateValueButton, selectedCellIdLabel, originalCellValueLabel,
                lastCellVersionLabel, selectSheetVersionSelector);
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public void fileLoaded() {
        modelUi.isAnyCellClickedProperty().set(false);
        modelUi.selectedCellIdProperty().set("");
        modelUi.selectedCellOriginalValueProperty().set("");
        modelUi.selectedCellLastVersionProperty().set(0);
        modelUi.currentSheetVersionProperty().set(1);
        selectSheetVersionSelector.disableProperty().set(false);
    }

    @FXML
    void UpdateValueButtonListener(ActionEvent event) {
        // Create a new Dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Cell Value");
        dialog.setHeaderText(selectedCellIdLabel.getText());

        // Create the input field
        TextField inputField = new TextField();
        inputField.setPromptText("Enter new value");

        // Create a button to show/hide details
        Button showDetailsButton = new Button("Functions Documentation");

        StringBuilder content = new StringBuilder();
        for(Operation operation : Operation.values()) {
            content.append(operation.getDocumentation())
                    .append("\n");
        }

        // Create a VBox for additional details
        VBox detailsBox = new VBox();
        detailsBox.setStyle("-fx-padding: 10; -fx-background-color: lightgrey;");
        detailsBox.setVisible(false); // Initially hidden

        // Set content to show in the functions documentation box
        Label detailsLabel = new Label(String.valueOf(content));
        detailsBox.getChildren().add(detailsLabel);

        // Set action for the Show Details button
        showDetailsButton.setOnAction(ev -> {
            boolean currentlyVisible = detailsBox.isVisible();
            detailsBox.setVisible(!currentlyVisible);
            showDetailsButton.setText(currentlyVisible ? "Functions Documentation" : "Hide");
        });

        // Create a VBox to contain the input field and the button
        VBox contentBox = new VBox(10, inputField, showDetailsButton, detailsBox);

        // Set the custom content for the dialog
        dialog.getDialogPane().setContent(contentBox);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButtonType, cancelButtonType);

         // Set the result converter to handle button clicks
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                // Return the text from the input field when OK is clicked
                return inputField.getText();
            } else {
                // Return null if Cancel is clicked
                return null;
            }
        });

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(this::updateCell);
    }

    @FXML
    void SelectSheetVersionSelectorListener(ActionEvent event) {
        mainAppController.selectSheetVersion(selectSheetVersionSelector.getSelectionModel().getSelectedItem());
    }

    public void updateCellFailed(String errorMessage) {
        AlertsHandler.HandleErrorAlert("Error on updating cell", errorMessage);
    }

    public void updateCellSucceeded() {
        AlertsHandler.HandleOkAlert("Update succeeded!");
    }

    public void updateCell(String cellNewOriginalValue) {
        try {
            CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(modelUi.selectedCellIdProperty().getValue());
            CellDto cellDto = engine.updateSheetCell(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn(), cellNewOriginalValue);
            modelUi.selectedCellOriginalValueProperty().set(cellNewOriginalValue);
            modelUi.selectedCellLastVersionProperty().set(engine.getLastCellVersion(cellPositionInSheet.getRow(), cellPositionInSheet.getColumn()));
            modelUi.currentSheetVersionProperty().set(engine.getCurrentSheetVersion());
            mainAppController.cellIsUpdated(cellPositionInSheet, cellDto);
        } catch (Exception e) {
            updateCellFailed(e.getMessage());
        }
    }

    public void cellClicked(String cellPositionId, String originalValue, int lastCellVersion) {
        modelUi.selectedCellIdProperty().set(cellPositionId);
        modelUi.selectedCellLastVersionProperty().set(lastCellVersion);
        modelUi.selectedCellOriginalValueProperty().set(originalValue);
        if (!modelUi.isAnyCellClickedProperty().getValue()) {
            modelUi.isAnyCellClickedProperty().set(true);
        }
    }
}