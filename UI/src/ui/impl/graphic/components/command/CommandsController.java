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

    @FXML private ColorPicker cellBackgroundColorPicker;
    @FXML private ColorPicker cellTextColorPicker;
    @FXML private ChoiceBox<CellPositionInSheet> cellPositionChoiceBox;
    @FXML private ChoiceBox<String> columnTextAlignChoiceBox;
    @FXML private ChoiceBox<Integer> rowChoiceBox;
    @FXML private ChoiceBox<String> filterByColumnChoiceBox;
    @FXML private Spinner<Integer> heightSpinner;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Button defaultCellPropsButton;
    @FXML private Button defaultColumnPropsButton;
    @FXML private Button defaultRowPropsButton;
    @FXML private Button setCellPropsButton;
    @FXML private Button setColumnPropsButton;
    @FXML private Button setRowPropsButton;
    @FXML private Button showSortedSheetButton;
    @FXML private Button showFilteredSheetButton;
    @FXML private ListView<CommandsModelUI.ListViewEntry> columnsListView;
    @FXML private TextField fromPositionFilterTextField;
    @FXML private TextField fromPositionSortTextField;
    @FXML private TextField toPositionFilterTextField;
    @FXML private TextField toPositionSortTextField;
    @FXML private TitledPane columnPropsTitledPane;
    @FXML private TitledPane rowPropsTitledPane;
    @FXML private TitledPane cellPropsTitledPane;
    @FXML private TitledPane sortSheetTitledPane;
    @FXML private TitledPane filterSheetTitledPane;

    private MainAppController mainAppController;
    private Engine engine;
    private CommandsModelUI modelUi;
    private Set<String> sheetColumns;

    @FXML
    private void initialize() {
        List<TitledPane> titledPanes = Arrays.asList(columnPropsTitledPane, rowPropsTitledPane, cellPropsTitledPane,
                sortSheetTitledPane, filterSheetTitledPane);
        modelUi = new CommandsModelUI(titledPanes, columnsListView, showSortedSheetButton);
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
            columnsListView.getItems().add(new CommandsModelUI.ListViewEntry(column));
            filterByColumnChoiceBox.getItems().add(column);
        }
        // Set the cell factory to include a CheckBox in each row
        columnsListView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(CommandsModelUI.ListViewEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setText(item.getName());
                    setGraphic(checkBox);
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

            LinkedHashSet<String> chosenColumns = columnsListView.getItems().stream()
                    .filter(entry -> entry.selectedProperty().get())
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
    void defaultCellPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void defaultColumnPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void defaultRowPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void setCellPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void setColumnPropsButtonListener(ActionEvent event) {

    }

    @FXML
    void setRowPropsButtonListener(ActionEvent event) {

    }


}