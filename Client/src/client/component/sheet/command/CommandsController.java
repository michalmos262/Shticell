package client.component.sheet.command;

import client.component.alert.AlertsHandler;
import client.component.sheet.mainsheet.MainSheetController;
import client.util.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
import dto.cell.CellPositionDto;
import dto.cell.EffectiveValueDto;
import dto.sheet.RowDto;
import dto.sheet.SheetDimensionDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import serversdk.exception.ServerException;

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

    public void initComponent(String sheetName) throws IOException {
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
    void showSortedSheetButtonListener(ActionEvent event) throws IOException {
        String fromPositionStr = fromPositionSortTextField.getText();
        String toPositionStr = toPositionSortTextField.getText();
        int sheetVersion = mainSheetController.getCurrentSheetVersion();

        Set<String> chosenColumns = sortByColumnsListView.getItems().stream()
                    .filter(CommandsModelUI.ListViewEntry::isSelected)
                    .map(CommandsModelUI.ListViewEntry::getName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

        String url = HttpUrl
                .parse(SORTED_SHEET_ROWS_ENDPOINT)
                .newBuilder()
                .addQueryParameter(SHEET_VERSION, String.valueOf(sheetVersion))
                .addQueryParameter(FROM_CELL_POSITION, fromPositionStr)
                .addQueryParameter(TO_CELL_POSITION, toPositionStr)
                .addQueryParameter(SORT_OR_FILTER_BY_COLUMNS, String.join(",", chosenColumns))
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        if (response.isSuccessful()) {
            Type listType = new TypeToken<LinkedList<RowDto>>(){}.getType();
            LinkedList<RowDto> sortedRows = GSON_INSTANCE.fromJson(responseBody, listType);
            mainSheetController.sheetIsSorted(sortedRows, fromPositionStr, toPositionStr);
        } else {
            ServerException.ErrorResponse errorResponse = GSON_INSTANCE.fromJson(responseBody, ServerException.ErrorResponse.class);
            AlertsHandler.HandleErrorAlert("Show sorted sheet", errorResponse.getMessage());
        }
    }

    @FXML
    void chooseFilterValuesButtonListener(ActionEvent event) {
        try {
            String fromPositionStr = fromPositionFilterTextField.getText();
            String toPositionStr = toPositionFilterTextField.getText();
            int sheetVersion = mainSheetController.getCurrentSheetVersion();

            Set<String> columns = filterByColumnsListView.getItems().stream()
                    .filter(CommandsModelUI.ListViewEntry::isSelected)
                    .map(CommandsModelUI.ListViewEntry::getName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            String url = HttpUrl
                    .parse(UNIQUE_COLUMN_VALUES_ENDPOINT)
                    .newBuilder()
                    .addQueryParameter(SHEET_VERSION, String.valueOf(sheetVersion))
                    .addQueryParameter(FROM_CELL_POSITION, fromPositionStr)
                    .addQueryParameter(TO_CELL_POSITION, toPositionStr)
                    .addQueryParameter(SORT_OR_FILTER_BY_COLUMNS, String.join(",", columns))
                    .build()
                    .toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
            String responseBody = response.body().string();
            if (response.isSuccessful()) {
                Type type = new TypeToken<Map<String, Set<EffectiveValueDto>>>() {}.getType();
                Map<String, Set<EffectiveValueDto>> uniqueValuesInColumns = GSON_INSTANCE.fromJson(responseBody, type);
                modelUi.setupFilterValuesTableView(filterValuesTableView, uniqueValuesInColumns);
            } else {
                ServerException.ErrorResponse errorResponse = GSON_INSTANCE.fromJson(responseBody, ServerException.ErrorResponse.class);
                AlertsHandler.HandleErrorAlert("Show filtered sheet", errorResponse.getMessage());
            }
        } catch (Exception e) {
            AlertsHandler.HandleErrorAlert("Show filtered sheet", e.getMessage());
        }
    }

    @FXML
    void showFilteredSheetButtonListener(ActionEvent event) throws IOException {
        String fromPositionStr = fromPositionFilterTextField.getText();
        String toPositionStr = toPositionFilterTextField.getText();
        int sheetVersion = mainSheetController.getCurrentSheetVersion();

        Map<String, Set<EffectiveValueDto>> column2effectiveValuesFilteredBy = new HashMap<>();

        // Iterate over each column in the filter TableView
        for (TableColumn<Map<String, CommandsModelUI.EffectiveValueWrapper>, ?> column : filterValuesTableView.getColumns()) {
            String columnName = column.getText(); // Get the column name
            Set<EffectiveValueDto> selectedValues = new HashSet<>(); // Set to store selected values for the column

            // Iterate over the rows in the TableView
            for (Map<String, CommandsModelUI.EffectiveValueWrapper> row : filterValuesTableView.getItems()) {
                CommandsModelUI.EffectiveValueWrapper wrapper = row.get(columnName);

                if (wrapper != null && wrapper.isSelected()) {
                    EffectiveValueDto value = wrapper.getEffectiveValue();
                    selectedValues.add(value);
                }
            }

            // Only add the column if there are selected values
            if (!selectedValues.isEmpty()) {
                column2effectiveValuesFilteredBy.put(columnName, selectedValues);
            }
        }

        HttpUrl.Builder urlBuilder = HttpUrl
                .parse(FILTERED_SHEET_ROWS_ENDPOINT)
                .newBuilder()
                .addQueryParameter(SHEET_VERSION, String.valueOf(sheetVersion))
                .addQueryParameter(FROM_CELL_POSITION, fromPositionStr)
                .addQueryParameter(TO_CELL_POSITION, toPositionStr);

        // Iterate through the map and add query parameters
        for (Map.Entry<String, Set<EffectiveValueDto>> entry : column2effectiveValuesFilteredBy.entrySet()) {
            String columnName = entry.getKey();
            Set<EffectiveValueDto> values = entry.getValue();

            // Join the EffectiveValueDto values into a comma-separated string
            String joinedValues = values.stream()
                    .map(EffectiveValueDto::toString) // Convert each EffectiveValueDto to a String
                    .collect(Collectors.joining(",")); // Join with commas

            // Add the column name and joined values as a query parameter
            urlBuilder.addQueryParameter(columnName, joinedValues);
        }

        String url = urlBuilder
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        if (response.isSuccessful()) {
            Type listType = new TypeToken<LinkedList<RowDto>>(){}.getType();
            LinkedList<RowDto> filteredRows = GSON_INSTANCE.fromJson(responseBody, listType);
            mainSheetController.sheetIsFiltered(filteredRows, fromPositionStr, toPositionStr);

        } else {
            ServerException.ErrorResponse errorResponse = GSON_INSTANCE.fromJson(responseBody, ServerException.ErrorResponse.class);
            AlertsHandler.HandleErrorAlert("Show filtered sheet", errorResponse.getMessage());
        }
    }
}