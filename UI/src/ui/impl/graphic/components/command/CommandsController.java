package ui.impl.graphic.components.command;

import engine.api.Engine;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.RowDto;
import engine.entity.range.Range;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ui.impl.graphic.components.alert.AlertsHandler;
import ui.impl.graphic.components.app.MainAppController;

import java.util.*;
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
    @FXML private TableView<Map<String, CommandsModelUI.EffectiveValueWrapper>> filterValuesTableView;

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
        List<ListView<CommandsModelUI.ListViewEntry>> listViews = new LinkedList<>();
        listViews.add(filterByColumnsListView);
        listViews.add(sortByColumnsListView);
        modelUi.setColumnsSelectBoxes(sheetColumns, listViews);

        filterSheetTitledPane.setExpanded(false);
        sortSheetTitledPane.setExpanded(false);

        fileIsLoading(false);
    }

    public void fileIsLoading(boolean isStarted) {
        modelUi.isFileLoadingProperty().set(isStarted);
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

            LinkedList<RowDto> sortedRows = engine.getSortedRowsSheet(range, chosenColumns);

            mainAppController.sheetIsSorted(sortedRows, range);
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Show sorted sheet", e.getMessage());
        }
    }

    @FXML
    void chooseFilterValuesButtonListener(ActionEvent event) {
        try {
            CellPositionInSheet fromPosition = PositionFactory.createPosition(fromPositionFilterTextField.getText());
            CellPositionInSheet toPosition = PositionFactory.createPosition(toPositionFilterTextField.getText());
            Range range = new Range(fromPosition, toPosition);

            Set<String> columns = filterByColumnsListView.getItems().stream()
                    .filter(CommandsModelUI.ListViewEntry::isSelected)
                    .map(CommandsModelUI.ListViewEntry::getName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            Map<String, Set<EffectiveValue>> uniqueValuesInColumns = engine.getUniqueColumnValuesByRange(range, columns);
            modelUi.setupFilterValuesTableView(filterValuesTableView, uniqueValuesInColumns);
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Show filtered sheet", e.getMessage());
        }
    }

    @FXML
    void showFilteredSheetButtonListener(ActionEvent event) {
        try {
            CellPositionInSheet fromPosition = PositionFactory.createPosition(fromPositionFilterTextField.getText());
            CellPositionInSheet toPosition = PositionFactory.createPosition(toPositionFilterTextField.getText());
            Range range = new Range(fromPosition, toPosition);

            Map<String, Set<EffectiveValue>> column2effectiveValuesFilteredBy = new HashMap<>();

            // Iterate over each column in the filter TableView
            for (TableColumn<Map<String, CommandsModelUI.EffectiveValueWrapper>, ?> column : filterValuesTableView.getColumns()) {
                String columnName = column.getText(); // Get the column name
                Set<EffectiveValue> selectedValues = new HashSet<>(); // Set to store selected values for the column

                // Iterate over the rows in the TableView
                for (Map<String, CommandsModelUI.EffectiveValueWrapper> row : filterValuesTableView.getItems()) {
                    CommandsModelUI.EffectiveValueWrapper wrapper = row.get(columnName);

                    if (wrapper != null && wrapper.isSelected()) {
                        EffectiveValue value = wrapper.getEffectiveValue();
                        selectedValues.add(value);
                    }
                }

                // Only add the column if there are selected values
                if (!selectedValues.isEmpty()) {
                    column2effectiveValuesFilteredBy.put(columnName, selectedValues);
                }
            }

            LinkedList<RowDto> filteredRows = engine.getFilteredRowsSheet(range, column2effectiveValuesFilteredBy);
            mainAppController.sheetIsFiltered(filteredRows, range);
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Show filtered sheet", e.getMessage());
        }
    }
}