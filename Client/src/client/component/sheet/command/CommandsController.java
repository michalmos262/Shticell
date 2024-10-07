package client.component.sheet.command;

import client.component.alert.AlertsHandler;
import client.component.sheet.mainsheet.MainSheetController;
import client.util.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
import dto.cell.CellPositionDto;
import dto.sheet.RowDto;
import dto.sheet.SheetDimensionDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.*;

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

    private MainSheetController mainSheetController;
    private CommandsModelUI modelUi;
    private Set<String> sheetColumns;

    @FXML
    private void initialize() {
        modelUi = new CommandsModelUI();
        sheetColumns = new LinkedHashSet<>();
    }

    public void setMainController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
    }

    public void init(String sheetName) throws IOException {
        String url = HttpUrl
                .parse(SHEET_DIMENSION_ENDPOINT)
                .newBuilder()
                .addQueryParameter(SHEET_NAME, sheetName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        if (response.isSuccessful()) {
            SheetDimensionDto sheetDimensionDto = GSON_INSTANCE.fromJson(responseBody, SheetDimensionDto.class);
            for (int i = 0; i < sheetDimensionDto.getNumOfColumns(); i++) {
                sheetColumns.add(CellPositionDto.parseColumn(i + 1));
            }
            List<ListView<CommandsModelUI.ListViewEntry>> listViews = new LinkedList<>();
            listViews.add(filterByColumnsListView);
            listViews.add(sortByColumnsListView);
            modelUi.setColumnsSelectBoxes(sheetColumns, listViews);
        }
    }

    @FXML
    void showSortedSheetButtonListener(ActionEvent event) {
        String fromPositionStr = fromPositionSortTextField.getText();
        String toPositionStr = toPositionSortTextField.getText();

        LinkedHashSet<String> chosenColumns = sortByColumnsListView.getItems().stream()
                    .filter(CommandsModelUI.ListViewEntry::isSelected)
                    .map(CommandsModelUI.ListViewEntry::getName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

        String url = HttpUrl
                .parse(SORTED_SHEET_ROWS_ENDPOINT)
                .newBuilder()
                .addQueryParameter(FROM_CELL_POSITION, fromPositionStr)
                .addQueryParameter(TO_CELL_POSITION, toPositionStr)
                .addQueryParameter(SORT_FILTER_BY_COLUMNS, String.join(",", chosenColumns))
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
            Type listType = new TypeToken<LinkedList<RowDto>>(){}.getType();
            LinkedList<RowDto> sortedRows = GSON_INSTANCE.fromJson(response.body().string(), listType);
            mainSheetController.sheetIsSorted(sortedRows, fromPositionStr, toPositionStr);
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Show sorted sheet", e.getMessage());
        }
    }

    @FXML
    void chooseFilterValuesButtonListener(ActionEvent event) {
        //todo: get unique columns for filter
//        try {
//            CellPositionInSheet fromPosition = PositionFactory.createPosition(fromPositionFilterTextField.getText());
//            CellPositionInSheet toPosition = PositionFactory.createPosition(toPositionFilterTextField.getText());
//            Range range = new Range(fromPosition, toPosition);
//
//            Set<String> columns = filterByColumnsListView.getItems().stream()
//                    .filter(CommandsModelUI.ListViewEntry::isSelected)
//                    .map(CommandsModelUI.ListViewEntry::getName)
//                    .collect(Collectors.toCollection(LinkedHashSet::new));
//
//            Map<String, Set<EffectiveValue>> uniqueValuesInColumns = engine.getUniqueColumnValuesByRange(range, columns);
//            modelUi.setupFilterValuesTableView(filterValuesTableView, uniqueValuesInColumns);
//        } catch (Exception e) {
//            AlertsHandler.HandleErrorAlert("Show filtered sheet", e.getMessage());
//        }
    }

    @FXML
    void showFilteredSheetButtonListener(ActionEvent event) {
        //todo: filter
//        try {
//            CellPositionInSheet fromPosition = PositionFactory.createPosition(fromPositionFilterTextField.getText());
//            CellPositionInSheet toPosition = PositionFactory.createPosition(toPositionFilterTextField.getText());
//            Range range = new Range(fromPosition, toPosition);
//
//            Map<String, Set<EffectiveValue>> column2effectiveValuesFilteredBy = new HashMap<>();
//
//            // Iterate over each column in the filter TableView
//            for (TableColumn<Map<String, CommandsModelUI.EffectiveValueWrapper>, ?> column : filterValuesTableView.getColumns()) {
//                String columnName = column.getText(); // Get the column name
//                Set<EffectiveValue> selectedValues = new HashSet<>(); // Set to store selected values for the column
//
//                // Iterate over the rows in the TableView
//                for (Map<String, CommandsModelUI.EffectiveValueWrapper> row : filterValuesTableView.getItems()) {
//                    CommandsModelUI.EffectiveValueWrapper wrapper = row.get(columnName);
//
//                    if (wrapper != null && wrapper.isSelected()) {
//                        EffectiveValue value = wrapper.getEffectiveValue();
//                        selectedValues.add(value);
//                    }
//                }
//
//                // Only add the column if there are selected values
//                if (!selectedValues.isEmpty()) {
//                    column2effectiveValuesFilteredBy.put(columnName, selectedValues);
//                }
//            }
//
//            LinkedList<RowDto> filteredRows = engine.getFilteredRowsSheet(range, column2effectiveValuesFilteredBy);
//            mainSheetController.sheetIsFiltered(filteredRows, range);
//        } catch (Exception e) {
//            AlertsHandler.HandleErrorAlert("Show filtered sheet", e.getMessage());
//        }
    }
}