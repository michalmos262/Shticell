package client.component.sheet.grid;

import client.component.alert.AlertsHandler;
import client.component.sheet.mainsheet.MainSheetController;
import client.util.http.HttpClientUtil;
import dto.cell.CellDto;
import dto.cell.CellPositionDto;
import dto.sheet.RangeDto;
import dto.sheet.RowDto;
import dto.sheet.SheetDimensionDto;
import dto.sheet.SheetDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.*;

public class GridController {
    @FXML private ScrollPane scrollPane;
    @FXML private GridPane mainGridPane;

    private MainSheetController mainSheetController;
    private GridModelUI modelUi;
    private final List<Label> currentlyPaintedCells = new ArrayList<>(); // List to store painted cells
    private Label clickedLabel;
    private int numOfRows, numOfColumns, defaultRowHeight, defaultColumnWidth;

    private final String CELL_CSS_CLASS = "cell";
    private final String INFLUENCED_CELL_CSS_CLASS = "influenced-cell";
    private final String INFLUENCING_CELL_CSS_CLASS = "influencing-cell";
    private final String CLICKED_CELL_CSS_CLASS = "clicked";
    private final String OF_RANGE_CSS_CLASS = "of-range";
    private final String COLUMN_HEADER_CSS_CLASS = "column-header";
    private final String ROW_HEADER_CSS_CLASS = "row-header";
    private final String COPIED_CELL_PREFIX_CSS_CLASS = "-copied";
    private double currentStepSize = 1;
    private final double defaultFromRange = 0;
    private final double defaultToRange = 1000;


    @FXML
    private void initialize() {
        modelUi = new GridModelUI(mainGridPane);
    }

    public void setMainController(MainSheetController mainSheetController) {
        this.mainSheetController = mainSheetController;
    }

    public void initMainGrid(String sheetName) throws IOException {
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

            numOfRows = sheetDimensionDto.getNumOfRows();
            numOfColumns = sheetDimensionDto.getNumOfColumns();
            defaultRowHeight = sheetDimensionDto.getRowHeight();
            defaultColumnWidth = sheetDimensionDto.getColumnWidth();

             // Clear the existing content in the gridContainer
            mainGridPane.getChildren().clear();

            setGridColumnsHeaders(mainGridPane, numOfColumns);
            setGridRowsHeaders(mainGridPane);
            setMainGridCells(sheetName);
        } else {
            System.out.println("Error: " + responseBody);
        }
    }

    private void setGridColumnsHeaders(GridPane gridPane, int numOfColumns) {
        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            String colStr = String.valueOf((char) ('A' + col));
            Label columnHeader = new Label(colStr);
            columnHeader.getStyleClass().add(COLUMN_HEADER_CSS_CLASS);
            columnHeader.setId(colStr);
            columnHeader.setMinWidth(defaultColumnWidth);
            columnHeader.setPrefWidth(defaultColumnWidth);
            columnHeader.setMaxWidth(defaultColumnWidth);
            gridPane.add(columnHeader, col + 1, 0);  // Place the column header in the first row
        }
    }

    private void setGridRowsHeaders(GridPane gridPane) {
        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            String rowStr = String.valueOf(row + 1);
            Label rowHeader = new Label(rowStr);
            rowHeader.getStyleClass().add(ROW_HEADER_CSS_CLASS);
            rowHeader.setId(rowStr);
            rowHeader.setPrefSize(20, defaultRowHeight);
            rowHeader.setMinSize(20, defaultRowHeight);
            rowHeader.setPrefSize(20, defaultRowHeight);
            rowHeader.setMaxHeight(defaultRowHeight);
            gridPane.add(rowHeader, 0, row + 1);  // Place the row header in the first column
        }
    }

    private void setMainGridCells(String sheetName) throws IOException {
        String url = HttpUrl
                .parse(SHEET_ENDPOINT)
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
            SheetDto sheetDto = GSON_INSTANCE.fromJson(responseBody, SheetDto.class);
            // Populate the GridPane with Labels in the main grid area
            for (int row = 0; row < numOfRows; row++) {
                for (int col = 0; col < numOfColumns; col++) {
                    CellPositionDto cellPosition = new CellPositionDto(row+1, col+1);
                    Label label = new Label();
                    label.getStyleClass().add(CELL_CSS_CLASS);
                    label.setId((char) ('A' + col) + String.valueOf(row + 1));
                    modelUi.setCellLabelBinding(label, sheetDto, cellPosition);

                    label.setMinHeight(defaultRowHeight);
                    label.setPrefHeight(defaultRowHeight);
                    label.setMaxHeight(defaultRowHeight);

                    label.setMinWidth(defaultColumnWidth);
                    label.setPrefWidth(defaultColumnWidth);
                    label.setMaxWidth(defaultColumnWidth);

                    // Attach the click event handler
                    label.setOnMouseClicked(this::handleCellClick);

                    mainGridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
                }
            }

            for (int row = 0; row < numOfRows; row++) {
                for (int col = 0; col < numOfColumns; col++) {
                    CellPositionDto CellPositionDto = new CellPositionDto(row + 1, col + 1);
                    modelUi.setRowsAndColumnsBindings(CellPositionDto);
                }
            }
        } else {
            System.out.println("Error: " + responseBody);
        }
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        clickedLabel = (Label) event.getSource();
        try {
            CellDto cellDto = mainSheetController.cellClicked(clickedLabel);
            setClickedCellColors(cellDto);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void setClickedCellColors(CellDto cellDto) {
        if (clickedLabel == null) {
            return;
        }
        // Clear the previously painted cells
        clearPaintedCells();

        clickedLabel.getStyleClass().add(CLICKED_CELL_CSS_CLASS);
        currentlyPaintedCells.add(clickedLabel); // Keep track of the painted cells

        if (cellDto != null) {
            // Paint some influences and influenced by cells based on the clicked cell
            List<Label> influencesCellsLabels = getInfluencesCellsToPaint(cellDto);
            List<Label> influencedByCellsLabels = getInfluencedByCellsToPaint(cellDto);
            for (Label cellLabel : influencesCellsLabels) {
                cellLabel.getStyleClass().add(INFLUENCED_CELL_CSS_CLASS);
                currentlyPaintedCells.add(cellLabel); // Keep track of the painted cells
            }
            for (Label cellLabel : influencedByCellsLabels) {
                cellLabel.getStyleClass().add(INFLUENCING_CELL_CSS_CLASS);
                currentlyPaintedCells.add(cellLabel); // Keep track of the painted cells
            }
        }
    }

    private void setRangeCellsColors(String rangeName) throws IOException {
        // Clear the previously painted cells
        clearPaintedCells();

        // get cell positions
        List<Label> cellsToPainter = getRangeCellsToPaint(rangeName);
        if (cellsToPainter != null) {
            for (Label cellLabel : cellsToPainter) {
                cellLabel.getStyleClass().add(OF_RANGE_CSS_CLASS);
                currentlyPaintedCells.add(cellLabel);
            }
        }
    }

    private List<Label> getInfluencesCellsToPaint(CellDto cellDto) {
        List<Label> influencesCellsLabels = new ArrayList<>();
        Set<CellPositionDto> influencesCells = cellDto.getInfluences();

        influencesCells.forEach(influencesCellPosition ->
                influencesCellsLabels.add((Label) mainGridPane.lookup("#" + influencesCellPosition))
        );

        return influencesCellsLabels;
    }

    private List<Label> getInfluencedByCellsToPaint(CellDto cellDto) {
        List<Label> influencedByCellsLabels = new ArrayList<>();
        Set<CellPositionDto> influencedByCells = cellDto.getInfluencedBy();

        influencedByCells.forEach(influencedByCellPosition ->
                influencedByCellsLabels.add((Label) mainGridPane.lookup("#" + influencedByCellPosition))
        );

        return influencedByCellsLabels;
    }

    private List<Label> getRangeCellsToPaint(String rangeName) throws IOException {
        String url = HttpUrl
                .parse(RANGE_ENDPOINT)
                .newBuilder()
                .addQueryParameter(RANGE_NAME, rangeName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        if (response.isSuccessful()) {
            List<Label> rangeCellsLabels = new ArrayList<>();
             RangeDto rangeDto = GSON_INSTANCE.fromJson(responseBody, RangeDto.class);
             Set<CellPositionDto> rangeCellPositions = rangeDto.getIncludedPositions();
            rangeCellPositions.forEach(position ->
                rangeCellsLabels.add((Label) mainGridPane.lookup("#" + position))
            );
            return rangeCellsLabels;
        } else {
            System.out.println("Error: " + responseBody);
        }
        return null;
    }

    // Method to clear the previously painted cells
    private void clearPaintedCells() {
        for (Label cell : currentlyPaintedCells) {
            cell.getStyleClass().removeAll(INFLUENCED_CELL_CSS_CLASS, CLICKED_CELL_CSS_CLASS,
                    INFLUENCING_CELL_CSS_CLASS, OF_RANGE_CSS_CLASS);
        }
        currentlyPaintedCells.clear(); // Clear the list after un-painting
    }

    public void cellUpdated(String cellPositionId, CellDto cellDto) {
        CellPositionDto cellPosition = new CellPositionDto(cellPositionId);
        SimpleStringProperty displayedValue = modelUi.getCellPosition2displayedValue().get(cellPosition).
                displayedValueProperty();
        displayedValue.setValue(cellDto.getEffectiveValueForDisplay().toString());

        // Update the visible affected cells
        cellDto.getInfluences().forEach(influencedPosition -> {
            SimpleStringProperty visibleValue = modelUi.getCellPosition2displayedValue().get(influencedPosition).
                    displayedValueProperty();

            String url = HttpUrl
                .parse(CELL_ENDPOINT)
                .newBuilder()
                .addQueryParameter(CELL_POSITION, influencedPosition.toString())
                .build()
                .toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    CellDto influencedCell = GSON_INSTANCE.fromJson(responseBody, CellDto.class);
                    visibleValue.setValue(influencedCell.getEffectiveValueForDisplay().toString());
                } else {
                    System.out.println("Error: " + responseBody);
                }
            } catch (Exception e) {
                try {
                    throw e;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setClickedCellColors(cellDto);
    }

    public void setGridOnLastVersionCells(GridPane gridPane, SheetDto sheetDto) {
        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionDto CellPositionDto = new CellPositionDto(row+1, col+1);
                Label label = new Label();
                label.getStyleClass().add(CELL_CSS_CLASS);
                String cellDisplayedValue = sheetDto.getCell(CellPositionDto) == null
                        ? ""
                        : sheetDto.getCell(CellPositionDto).getEffectiveValueForDisplay().toString();
                label.setText(cellDisplayedValue);
                label.setPrefWidth(defaultColumnWidth);
                label.setMinWidth(defaultColumnWidth);
                label.setMaxWidth(defaultColumnWidth);
                label.setPrefHeight(defaultRowHeight);
                label.setMinHeight(defaultRowHeight);
                label.setMaxHeight(defaultRowHeight);

                gridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }
    }

    public void showSheetInVersion(SheetDto sheetDto, int version) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show sheet on specific version");
        dialog.setHeaderText("Sheet version: " + version);

        ScrollPane scrollPane = getUnStyledGrid(sheetDto);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private boolean isPositionInSortedRow(LinkedList<RowDto> sortedRows, CellPositionDto CellPositionDto) {
        for (RowDto row : sortedRows) {
            if (row.getRowNumber() == CellPositionDto.getRow()) {
                return true;
            }
        }
        return false;
    }

    public void showSortedSheet(LinkedList<RowDto> sortedRows, String fromPositionStr, String toPositionStr) throws IOException {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show sorted sheet");

        ScrollPane scrollPane = getCopiedMainGreed();
        GridPane sortedGrid = (GridPane) scrollPane.getContent();
        
        String url = HttpUrl
                .parse(RANGE_ENDPOINT)
                .newBuilder()
                .addQueryParameter(FROM_CELL_POSITION, fromPositionStr)
                .addQueryParameter(TO_CELL_POSITION, toPositionStr)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            RangeDto rangeToSort = GSON_INSTANCE.fromJson(responseBody, RangeDto.class);
            Iterator<CellPositionDto> positionInRangeIterator = rangeToSort.getIncludedPositions().iterator();

            for (RowDto row : sortedRows) {
                for (Map.Entry<String, CellDto> column2cell : row.getCells().entrySet()) {
                    CellPositionDto positionInRange = positionInRangeIterator.next();
                    while (positionInRangeIterator.hasNext() && !isPositionInSortedRow(sortedRows, positionInRange)) {
                        positionInRange = positionInRangeIterator.next();
                    }
                    Label cellInOriginalGrid = (Label) mainGridPane.lookup("#" + column2cell.getKey() + row.getRowNumber());
                    Label cellInSortedGrid = (Label) sortedGrid.lookup("#" + positionInRange + COPIED_CELL_PREFIX_CSS_CLASS);
                    copyCellStyle(cellInOriginalGrid, cellInSortedGrid);
                }
            }

            dialog.getDialogPane().setContent(scrollPane);
            dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    private void copyCellStyle(Label originalCell, Label copiedCell) {
        copiedCell.setText(originalCell.getText());
        copiedCell.setTextFill(originalCell.getTextFill());
        copiedCell.setBackground(originalCell.getBackground());
    }

    private ScrollPane getCopiedMainGreed() {
        ScrollPane scrollPane = getSizedScrollPaneWithGrid();
        GridPane copiedGridPane = (GridPane) scrollPane.getContent();

        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            String rowStr = String.valueOf(row + 1);
            Label rowHeaderInOriginalGrid = (Label) mainGridPane.lookup("#" + rowStr);
            Label rowHeaderInCopiedGrid = new Label(rowStr);
            rowHeaderInCopiedGrid.setPrefSize(rowHeaderInOriginalGrid.getPrefWidth(), rowHeaderInOriginalGrid.getPrefHeight());
            rowHeaderInCopiedGrid.setMinSize(rowHeaderInOriginalGrid.getMinWidth(), rowHeaderInOriginalGrid.getMinHeight());
            rowHeaderInCopiedGrid.setMaxHeight(rowHeaderInOriginalGrid.getMaxHeight());
            rowHeaderInCopiedGrid.setBorder(rowHeaderInOriginalGrid.getBorder());
            copiedGridPane.add(rowHeaderInCopiedGrid, 0, row + 1);  // Place the row header in the first column
        }

        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            String colStr = String.valueOf((char) ('A' + col));
            Label columnHeaderInOriginalGrid = (Label) mainGridPane.lookup("#" + colStr);
            Label columnHeaderInCopiedGrid = new Label(colStr);
            columnHeaderInCopiedGrid.setMinWidth(columnHeaderInOriginalGrid.getMinWidth());
            columnHeaderInCopiedGrid.setPrefWidth(columnHeaderInOriginalGrid.getPrefWidth());
            columnHeaderInCopiedGrid.setMaxWidth(columnHeaderInOriginalGrid.getMaxWidth());
            columnHeaderInCopiedGrid.setBorder(columnHeaderInOriginalGrid.getBorder());
            copiedGridPane.add(columnHeaderInCopiedGrid, col + 1, 0);  // Place the column header in the first row
        }

        // Populate the GridPane with Labels (sheet cells)
        for (int row = 1; row <= numOfRows; row++) {
            for (int col = 1; col <= numOfColumns; col++) {
                CellPositionDto cellPosition = new CellPositionDto(row, col);
                Label cellInOriginalGrid = (Label) mainGridPane.lookup("#" + CellPositionDto.parseColumn(cellPosition.getColumn()) + cellPosition.getRow());
                Label cellInCopiedGrid = new Label();
                cellInCopiedGrid.setId(CellPositionDto.parseColumn(cellPosition.getColumn()) + cellPosition.getRow() + COPIED_CELL_PREFIX_CSS_CLASS);
                cellInCopiedGrid.setText(cellInOriginalGrid.getText());
                cellInCopiedGrid.setPrefWidth(cellInOriginalGrid.getPrefWidth());
                cellInCopiedGrid.setMinWidth(cellInOriginalGrid.getMinWidth());
                cellInCopiedGrid.setMaxWidth(cellInOriginalGrid.getMaxWidth());
                cellInCopiedGrid.setPrefHeight(cellInOriginalGrid.getPrefHeight());
                cellInCopiedGrid.setMinHeight(cellInOriginalGrid.getMinHeight());
                cellInCopiedGrid.setMaxHeight(cellInOriginalGrid.getMaxHeight());
                cellInCopiedGrid.setBorder(Border.stroke(Color.GRAY));

                cellInCopiedGrid.setBackground(cellInOriginalGrid.getBackground());
                cellInCopiedGrid.setTextFill(cellInOriginalGrid.getTextFill());
                cellInCopiedGrid.setAlignment(cellInOriginalGrid.getAlignment());

                copiedGridPane.add(cellInCopiedGrid, col, row);  // Offset by 1 to leave space for headers
            }
        }

        return scrollPane;
    }

    public void showFilteredSheet(LinkedList<RowDto> filteredRows, String fromPositionStr, String toPositionStr) throws IOException {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Show filtered sheet");

        ScrollPane scrollPane = getCopiedMainGreed();
        GridPane filteredGrid = (GridPane) scrollPane.getContent();
        String url = HttpUrl
                .parse(RANGE_ENDPOINT)
                .newBuilder()
                .addQueryParameter(FROM_CELL_POSITION, fromPositionStr)
                .addQueryParameter(TO_CELL_POSITION, toPositionStr)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            RangeDto rangeToSort = GSON_INSTANCE.fromJson(responseBody, RangeDto.class);
            Iterator<CellPositionDto> positionInRangeIterator = rangeToSort.getIncludedPositions().iterator();

            for (RowDto row : filteredRows) {
                for (Map.Entry<String, CellDto> column2cell : row.getCells().entrySet()) {
                    CellPositionDto positionInRange = positionInRangeIterator.next();
                    Label cellInOriginalGrid = (Label) mainGridPane.lookup("#" + column2cell.getKey() + row.getRowNumber());
                    Label cellInSortedGrid = (Label) filteredGrid.lookup("#" + positionInRange + COPIED_CELL_PREFIX_CSS_CLASS);
                    copyCellStyle(cellInOriginalGrid, cellInSortedGrid);
                }
            }

            if (positionInRangeIterator.hasNext()) {
                while (positionInRangeIterator.hasNext()) {
                    CellPositionDto positionInRange = positionInRangeIterator.next();
                    Label cellInSortedGrid = (Label) filteredGrid.lookup("#" + positionInRange + COPIED_CELL_PREFIX_CSS_CLASS);
                    cellInSortedGrid.setText("");
                    cellInSortedGrid.setBackground(Background.fill(Color.WHITE));
                }
            }

            dialog.getDialogPane().setContent(scrollPane);
            dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    private ScrollPane getUnStyledGrid(SheetDto sheetDto) {
        ScrollPane scrollPane = getSizedScrollPaneWithGrid();
        GridPane gridPane = (GridPane) scrollPane.getContent();

        setGridColumnsHeaders(gridPane, numOfColumns);
        setGridRowsHeaders(gridPane);
        setGridOnLastVersionCells(gridPane, sheetDto);

        for (Node node : gridPane.getChildren()) {
            if (node instanceof Label label) {
                label.setStyle("-fx-border-color: black; -fx-alignment: center;");
            }
        }

        return scrollPane;
    }

    private ScrollPane getSizedScrollPaneWithGrid() {
        ScrollPane scrollPane = new ScrollPane();
        GridPane gridPane = new GridPane();

        scrollPane.setMinHeight(600);
        scrollPane.setMinWidth(900);
        scrollPane.setPrefHeight(600);
        scrollPane.setPrefWidth(900);
        scrollPane.setMaxHeight(600);
        scrollPane.setMaxWidth(900);

        scrollPane.setContent(gridPane);

        return scrollPane;
    }

    public void showCellsInRange(String name) throws IOException {
        setRangeCellsColors(name);
    }

    public void removeCellsPaints() {
        clearPaintedCells();
    }

    public void changeCellBackground(String cellId, Color cellBackgroundColor) {
        CellPositionDto CellPositionDto = new CellPositionDto(cellId);
        modelUi.getCellPosition2displayedValue().get(CellPositionDto).backgroundColorProperty().setValue(cellBackgroundColor);
    }

    public void changeCellTextColor(String cellId, Color cellTextColor) {
        CellPositionDto CellPositionDto = new CellPositionDto(cellId);
        modelUi.getCellPosition2displayedValue().get(CellPositionDto).textColorProperty().setValue(cellTextColor);
    }

    public void changeColumnTextAlignment(String cellId, Pos columnTextAlignment) {
        CellPositionDto CellPositionDto = new CellPositionDto(cellId);
        modelUi.getCellPosition2displayedValue().get(CellPositionDto).textAlignmentProperty().setValue(columnTextAlignment);
    }

    public void changeRowHeight(String cellId, int rowHeight) {
        CellPositionDto CellPositionDto = new CellPositionDto(cellId);
        modelUi.getCellPosition2displayedValue().get(CellPositionDto).rowHeightProperty().setValue(rowHeight);
    }

    public void changeColumnWidth(String cellId, int columnWidth) {
        CellPositionDto CellPositionDto = new CellPositionDto(cellId);
        modelUi.getCellPosition2displayedValue().get(CellPositionDto).columnWidthProperty().setValue(columnWidth);
    }

    public void updateCellColors(String cellId, Color cellBackgroundColor, Color cellTextColor) {
        changeCellBackground(cellId, cellBackgroundColor);
        changeCellTextColor(cellId, cellTextColor);
    }

    public void showDynamicAnalysis(String cellId) throws IOException {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Dynamic analysis");
        dialog.setHeaderText("Dynamic analysis of cell in position: " + cellId);

        ScrollPane scrollPane = getCopiedMainGreed();
        GridPane copiedGrid = (GridPane) scrollPane.getContent();

        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionDto cellPositionDto = new CellPositionDto(row + 1, col + 1);
                Label cellLabel = (Label) copiedGrid.lookup("#" + CellPositionDto.parseColumn(cellPositionDto.getColumn()) + cellPositionDto.getRow() + COPIED_CELL_PREFIX_CSS_CLASS);
                int sheetVersion = mainSheetController.getCurrentSheetVersion();

                String url = HttpUrl
                        .parse(SHEET_ENDPOINT)
                        .newBuilder()
                        .addQueryParameter(SHEET_VERSION, String.valueOf(sheetVersion))
                        .build()
                        .toString();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    SheetDto sheetDto = GSON_INSTANCE.fromJson(responseBody, SheetDto.class);
                    modelUi.setCellLabelBindingDynamicAnalysis(cellLabel, sheetDto, cellPositionDto);
                }
            }
        }

        TextField fromRangeTextField = new TextField();
        TextField toRangeTextField = new TextField();
        TextField stepSizeTextField = new TextField();

        fromRangeTextField.setPromptText("default: " + defaultFromRange);
        toRangeTextField.setPromptText("default: " + defaultToRange);
        stepSizeTextField.setPromptText("default: " + currentStepSize);

        Label fromRangeLabel = new Label("From number:");
        Label toRangeLabel = new Label("To number:");
        Label stepSizeLabel = new Label("Step size:");

        Slider slider = new Slider(defaultFromRange, defaultToRange, currentStepSize);

        // Create the content for the dialog
        GridPane dialogGridPane = new GridPane();
        dialogGridPane.setHgap(10);
        dialogGridPane.setVgap(10);

        GridPane textFieldsGridPane = new GridPane();
        textFieldsGridPane.setHgap(10);
        textFieldsGridPane.setVgap(10);

        textFieldsGridPane.add(fromRangeLabel, 0, 0);
        textFieldsGridPane.add(fromRangeTextField, 1, 0);
        textFieldsGridPane.add(toRangeLabel, 0, 1);
        textFieldsGridPane.add(toRangeTextField, 1, 1);
        textFieldsGridPane.add(stepSizeLabel, 0, 2);
        textFieldsGridPane.add(stepSizeTextField, 1, 2);
        textFieldsGridPane.add(slider, 1, 3);

        dialogGridPane.add(textFieldsGridPane, 0, 0);
        dialogGridPane.add(scrollPane, 1, 0);

        setDynamicAnalysisListeners(cellId, fromRangeTextField, toRangeTextField, stepSizeTextField, slider);

        dialog.getDialogPane().setContent(dialogGridPane);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        dialog.showAndWait();
    }

    private void setDynamicAnalysisListeners(String cellId, TextField fromRangeTextField, TextField toRangeTextField,
                                             TextField stepSizeTextField, Slider slider ) {

        fromRangeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    double min = Double.parseDouble(fromRangeTextField.getText());
                    slider.setMin(min);

                    // Adjust slider value if it's less than the new minimum
                    if (slider.getValue() < min) {
                        slider.setValue(min);
                    }
                } else {
                    slider.setMin(defaultFromRange);
                }
            } catch (Exception e) {
                AlertsHandler.HandleErrorAlert("Set from range", "Please enter a valid number");
            }
        });

        toRangeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    double max = Double.parseDouble(toRangeTextField.getText());
                    slider.setMax(max);

                    // Adjust slider value if it's greater than the new maximum
                    if (slider.getValue() > max) {
                        slider.setValue(max);
                    }
                } else {
                    slider.setMax(defaultToRange);
                }
            } catch (Exception e) {
                AlertsHandler.HandleErrorAlert("Set to range", "Please enter a valid number");
            }
        });

        stepSizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    currentStepSize = Double.parseDouble(newValue);
                } else {
                    currentStepSize = 1;
                }

                slider.setMajorTickUnit(currentStepSize);
                slider.setBlockIncrement(currentStepSize);
                slider.setSnapToTicks(true);
                slider.setShowTickMarks(true);
                slider.setMinorTickCount(0);

                // Adjust the slider value to a valid multiple of the step size, ensuring it doesn't exceed the max
                double currentValue = slider.getValue();
                double roundedValue = Math.round((currentValue - slider.getMin()) / currentStepSize) * currentStepSize + slider.getMin();

                if (roundedValue > slider.getMax()) {
                    roundedValue = slider.getMax() - (slider.getMax() - slider.getMin()) % currentStepSize;
                }

                slider.setValue(roundedValue);

            } catch (NumberFormatException e) {
                AlertsHandler.HandleErrorAlert("Set step size", "Please enter a valid number");
            }
        });

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Ensure slider value respects the step size
            double roundedValue = Math.round((newValue.doubleValue() - slider.getMin()) / currentStepSize) * currentStepSize + slider.getMin();

            // Ensure the slider value doesn't exceed the maximum
            if (roundedValue > slider.getMax()) {
                roundedValue = slider.getMax() - (slider.getMax() - slider.getMin()) % currentStepSize;
            }

            slider.setValue(roundedValue);

            CellPositionDto CellPositionDto = new CellPositionDto(cellId);
            String url;
            url = HttpUrl
                    .parse(DYNAMIC_ANALYSED_ENDPOINT)
                    .newBuilder()
                    .addQueryParameter(CELL_POSITION, cellId)
                    .addQueryParameter(CELL_ORIGINAL_VALUE, String.valueOf(roundedValue))
                    .addQueryParameter(SHEET_VERSION, String.valueOf(mainSheetController.getCurrentSheetVersion()))
                    .build()
                    .toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    SheetDto newSheetDto = GSON_INSTANCE.fromJson(responseBody, SheetDto.class);
                    SimpleStringProperty displayedValue = modelUi.getCellPosition2displayedValueDynamicAnalysis().get(CellPositionDto).
                        displayedValueProperty();

                    CellDto cellDto = newSheetDto.getCell(CellPositionDto);
                    if (cellDto != null) {
                        displayedValue.setValue(cellDto.getEffectiveValueForDisplay().toString());

                        // Update the visible affected cells
                        cellDto.getInfluences().forEach(influencedPosition -> {
                            SimpleStringProperty visibleValue = modelUi.getCellPosition2displayedValueDynamicAnalysis().get(influencedPosition).
                                    displayedValueProperty();
                            CellDto influencedCell = newSheetDto.getCell(influencedPosition);
                            visibleValue.setValue(influencedCell.getEffectiveValueForDisplay().toString());
                        });
                    }
                } else {
                    System.out.println("Error: " + responseBody);
                }
            } catch (Exception e) {
                try {
                    throw e;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void moveToNewestSheetVersion() throws IOException {
        Request request = new Request.Builder()
                .url(SHEET_ENDPOINT)
                .build();

        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            SheetDto newSheetDto = GSON_INSTANCE.fromJson(responseBody, SheetDto.class);
            for (int row = 1; row <= numOfRows; row++) {
                for (int col = 1; col <= numOfColumns; col++) {
                    CellPositionDto cellPosition = new CellPositionDto(row, col);
                    CellDto cellDto = newSheetDto.getCell(cellPosition);
                    if (cellDto != null) {
                        cellUpdated(cellPosition.toString(), cellDto);
                    }
                }
            }
        }

        removeCellsPaints();
    }
}