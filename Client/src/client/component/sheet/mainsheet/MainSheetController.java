package client.component.sheet.mainsheet;

import client.component.mainapp.MainAppController;
import client.component.sheet.actionline.ActionLineController;
import client.component.sheet.range.RangesController;
import client.util.http.HttpClientUtil;
import dto.cell.CellDto;
import dto.sheet.RowDto;
import dto.sheet.SheetDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import client.component.sheet.command.CommandsController;
import client.component.sheet.grid.GridController;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.function.Consumer;

import static client.resources.CommonResourcesPaths.*;
import static serversdk.request.parameter.RequestParameters.SHEET_NAME;
import static serversdk.request.parameter.RequestParameters.SHEET_VERSION;

public class MainSheetController implements Closeable {
    @FXML public BorderPane singleSheetComponent;
    @FXML public Button backToDashboardButton;
    @FXML private GridPane actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;
    @FXML private CommandsController commandsComponentController;
    @FXML private RangesController rangesComponentController;
    @FXML private GridController gridComponentController;

    private MainAppController mainAppController;
    private String sheetName;
    private boolean isComponentActive = false;
    private MainSheetRefresher mainSheetRefresher;
    private Timer timer;

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

    public void initComponents(String sheetName) {
        this.sheetName = sheetName;
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
        rangesComponentController.updateCellSucceeded(cellDto.getLastUpdatedInVersion());
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

        HttpClientUtil.runAsyncGet(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error on selecting sheet version: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    SheetDto sheetDto = GSON_INSTANCE.fromJson(response.body().string(), SheetDto.class);
                    Platform.runLater(() -> gridComponentController.showSheetInVersion(sheetDto, version));
                } else {
                    Platform.runLater(() -> {
                        try {
                            System.out.println("Error on selecting sheet version: " + response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
    }

    public void showCellsInRange(String name) {
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

    public void showDynamicAnalysis(String cellId) {
        gridComponentController.showDynamicAnalysis(cellId);
    }

    public void moveToNewestSheetVersion(int newestVersion) throws IOException {
        removeCellsPaints();
        gridComponentController.moveToNewestSheetVersion();
        rangesComponentController.moveToNewestSheetVersion(newestVersion);
    }

    public void clickOnMoveToNewestVersionButton() {
        actionLineComponentController.clickOnMoveToNewestVersionButtonSync();
    }

    public void setActive() {
        if (!isComponentActive) {
            actionLineComponentController.setActive();
            rangesComponentController.setActive();
            startMainSheetRefresher();
            isComponentActive = true;
        }
    }

    public void setIsUserWriter(boolean isWriter) {
        actionLineComponentController.setIsUserWriter(isWriter);
        rangesComponentController.setIsUserWriter(isWriter);
    }

    public int getLastSheetVersionSync() throws IOException {
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
            return sheetDto.getVersion();
        } else {
            System.out.println("Error get last sheet version sync: " + responseBody + " status code: " +
                            response.code());
        }
        return 0;
    }

    public void getLastSheetVersionAsync(Consumer<Integer> callBack) {
        HttpClientUtil.runAsyncGet(SHEET_ENDPOINT, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error get last sheet version async: " + e.getMessage());
                Platform.runLater(() -> callBack.accept(0));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    SheetDto sheetDto = GSON_INSTANCE.fromJson(responseBody, SheetDto.class);
                    Platform.runLater(() -> callBack.accept(sheetDto.getVersion()));
                } else {
                    System.out.println("Error get last sheet version async: " + responseBody + " status code: " +
                            response.code());
                    Platform.runLater(() -> callBack.accept(0));
                }
            }
        });
    }

    public int getCurrentSheetVersion() {
        return actionLineComponentController.getCurrentSheetVersion();
    }

    private void startMainSheetRefresher() {
        if (isComponentActive) return;
        mainSheetRefresher = new MainSheetRefresher(
                sheetName,
                this::setIsUserWriter
        );
        timer = new Timer();
        timer.schedule(mainSheetRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    @Override
    public void close() {
        isComponentActive = false;
        actionLineComponentController.close();
        rangesComponentController.close();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (mainSheetRefresher != null) {
            mainSheetRefresher.cancel();
        }
    }
}