package client.component.sheet.mainsheet;

import client.component.mainapp.MainAppController;
import client.component.sheet.actionline.ActionLineController;
import client.component.sheet.range.RangesController;
import client.util.http.HttpClientUtil;
import dto.cell.CellDto;
import dto.sheet.RowDto;
import dto.sheet.SheetDto;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import client.component.sheet.command.CommandsController;
import client.component.sheet.grid.GridController;
import dto.sheet.RangeDto;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.SHEET_VERSION;

public class MainSheetController implements Closeable {
    @FXML public BorderPane singleSheetComponent;
    @FXML public Button backToDashboardButton;
    @FXML private GridPane actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;
    @FXML private BorderPane commandsComponent;
    @FXML private CommandsController commandsComponentController;
    @FXML private BorderPane rangesComponent;
    @FXML private RangesController rangesComponentController;
    @FXML private ScrollPane gridComponent;
    @FXML private GridController gridComponentController;

    private MainAppController mainAppController;

    @FXML
    void initialize() {
        if (gridComponentController != null && actionLineComponent != null &&
                rangesComponentController != null && commandsComponentController != null) {
            gridComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
            rangesComponentController.setMainController(this);
            commandsComponentController.setMainController(this);
        }
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public void initComponents(String sheetName) throws IOException {
        gridComponentController.initMainGrid(sheetName);
        actionLineComponentController.initComponent(sheetName);
        commandsComponentController.initComponent(sheetName);
    }

    @FXML
    private void backToDashboardButtonListener() {
        mainAppController.switchToDashboardPage();
    }

    public CellDto cellClicked(Label clickedCell) throws IOException {
        return actionLineComponentController.cellClicked(clickedCell);
    }

    public void cellIsUpdated(String cellPositionId, CellDto cellDto) {
        gridComponentController.cellUpdated(cellPositionId, cellDto);
        actionLineComponentController.updateCellSucceeded();
    }

    public void changeCellBackground(String cellId, Color cellBackgroundColor) {
        gridComponentController.changeCellBackground(cellId, cellBackgroundColor);
    }

    public void changeCellTextColor(String cellId, Color cellTextColor) {
        gridComponentController.changeCellTextColor(cellId, cellTextColor);
    }

    public void changeColumnTextAlignment(String cellId, Pos columnTextAlignment) {
        gridComponentController.changeColumnTextAlignment(cellId, columnTextAlignment);
    }

    public void changeRowHeight(String cellId, int rowHeight) {
        gridComponentController.changeRowHeight(cellId, rowHeight);
    }

    public void changeColumnWidth(String cellId, int columnWidth) {
        gridComponentController.changeColumnWidth(cellId, columnWidth);
    }

    public void updateCellColors(String cellId, Color cellBackgroundColor, Color cellTextColor) {
        gridComponentController.updateCellColors(cellId, cellBackgroundColor, cellTextColor);
    }

    public void selectSheetVersion(int version) {
        String url = HttpUrl
                .parse(SHEET_ENDPOINT)
                .newBuilder()
                .addQueryParameter(SHEET_VERSION, String.valueOf(version))
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                SheetDto sheetDto = GSON_INSTANCE.fromJson(response.body().string(), SheetDto.class);
                gridComponentController.showSheetInVersion(sheetDto, version);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showCellsInRange(String name) throws IOException {
        gridComponentController.showCellsInRange(name);
        actionLineComponentController.removeCellClickFocus();
    }

    public void sheetIsSorted(LinkedList<RowDto> sortedRows, String fromPositionStr, String toPositionStr) throws IOException {
        gridComponentController.showSortedSheet(sortedRows, fromPositionStr, toPositionStr);
    }

    public void sheetIsFiltered(LinkedList<RowDto> filteredRows, String fromPositionStr, String toPositionStr) throws IOException {
        gridComponentController.showFilteredSheet(filteredRows, fromPositionStr, toPositionStr);
    }

    public void removeCellsPaints() {
        gridComponentController.removeCellsPaints();
    }

    public void showDynamicAnalysis(String cellId) throws IOException {
        gridComponentController.showDynamicAnalysis(cellId);
    }

    public void moveToNewestSheetVersion() throws IOException {
        removeCellsPaints();
        gridComponentController.moveToNewestSheetVersion();
    }

    public void setActive() {
        actionLineComponentController.setActive();
        rangesComponentController.setActive();
    }

    public int getLastSheetVersion() throws IOException {
        Request request = HttpClientUtil.getCurrentSheet();
        Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();

        if (response.isSuccessful()) {
            SheetDto sheetDto = GSON_INSTANCE.fromJson(responseBody, SheetDto.class);
            return sheetDto.getVersion();
        } else {
            System.out.println("Error: " + responseBody);
        }

        return 0;
    }

    public int getCurrentSheetVersion() {
        return actionLineComponentController.getCurrentSheetVersion();
    }


    @Override
    public void close() {
        actionLineComponentController.close();
        rangesComponentController.close();
    }
}