package ui.impl.graphic.components.command;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.SheetDto;
import engine.entity.range.Range;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandsController {

    @FXML private Button showSortedSheetButton;
    @FXML private Button showFilteredSheetButton;
    @FXML private Button chooseFilterValuesButton;
    @FXML private ListView<CommandsModelUI.ListViewEntry> sortByColumnsListView;
    @FXML private ListView<CommandsModelUI.ListViewEntry> filterByColumnsListView;
    @FXML private TextField fromPositionFilterTextField;
    @FXML private TextField fromPositionSortTextField;
    @FXML private TextField toPositionFilterTextField;
    @FXML private TextField toPositionSortTextField;
    @FXML private TitledPane sortSheetTitledPane;
    @FXML private TitledPane filterSheetTitledPane;

    private MainAppController mainAppController;
    private Engine engine;
    private CommandsModelUI modelUi;
    private Set<String> sheetColumns;

    @FXML
    private void initialize() {
        List<TitledPane> titledPanes = Arrays.asList(sortSheetTitledPane, filterSheetTitledPane);
        modelUi = new CommandsModelUI(titledPanes, sortByColumnsListView, showSortedSheetButton);
        sheetColumns = new LinkedHashSet<>();
    }

    public void setMainController(MainAppController mainAppController, Engine engine) {
        this.mainAppController = mainAppController;
        this.engine = engine;
    }

    public void fileLoadedSuccessfully() {
        modelUi.isSheetLoadedProperty().set(true);
        for (int i = 0; i < engine.getNumOfSheetColumns(); i++) {
            sheetColumns.add(CellPositionInSheet.parseColumn(i + 1));
        }
        setColumnsSelectBoxes();
        fileIsLoading(false);
    }

    public void fileIsLoading(boolean isStarted) {
        modelUi.isFileLoadingProperty().set(isStarted);
    }

    private void setColumnsSelectBoxes() {
        for (String column : sheetColumns) {
            sortByColumnsListView.getItems().add(new CommandsModelUI.ListViewEntry(column));
            filterByColumnsListView.getItems().add(new CommandsModelUI.ListViewEntry(column));
        }

        // Set the cell factory to include a CheckBox in each row
        sortByColumnsListView.setCellFactory(lv -> new ListCell<>() {
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

        filterByColumnsListView.setCellFactory(lv -> new ListCell<>() {
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

    @FXML
    void showSortedSheetButtonListener(ActionEvent event) {
        try {
            CellPositionInSheet fromPosition = PositionFactory.createPosition(fromPositionSortTextField.getText());
            CellPositionInSheet toPosition = PositionFactory.createPosition(toPositionSortTextField.getText());
            Range range = new Range(fromPosition, toPosition);

            LinkedHashSet<String> chosenColumns = sortByColumnsListView.getItems().stream()
                    .filter(CommandsModelUI.ListViewEntry::isSelected)
                    .map(CommandsModelUI.ListViewEntry::getName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            SheetDto sheetDto = engine.getSortedRowsSheet(range, chosenColumns);
            mainAppController.sortedSheet(sheetDto);
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Show sorted sheet", e.getMessage());
        }
    }

    @FXML
    void showFilteredSheetButtonListener(ActionEvent event) {

    }

    @FXML
    void chooseFilterValuesButtonListener(ActionEvent event) {

    }
}