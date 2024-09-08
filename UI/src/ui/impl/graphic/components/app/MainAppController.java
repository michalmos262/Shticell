package ui.impl.graphic.components.app;

import engine.entity.dto.CellDto;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetDimension;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ui.impl.graphic.components.actionline.ActionLineController;
import ui.impl.graphic.components.file.LoadFileController;
import ui.impl.graphic.components.grid.GridController;
import ui.impl.graphic.model.BusinessLogic;

public class MainAppController {
    @FXML private GridPane loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;
    @FXML private GridPane actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;
    @FXML private BorderPane commandsComponent;
    @FXML private BorderPane rangesComponent;
    @FXML private ScrollPane sheetComponent;
    @FXML private GridController sheetComponentController;

    private Stage primaryStage;
    private BusinessLogic businessLogic;

    @FXML
    public void initialize() {
        if (loadFileComponentController != null && sheetComponentController != null && actionLineComponent != null) {
            loadFileComponentController.setMainController(this);
            sheetComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setBusinessLogic(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
        actionLineComponentController.bindToModel(this.businessLogic);
        loadFileComponentController.bindToModel(this.businessLogic);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void loadFile() {
        try {
            businessLogic.loadFile(loadFileComponentController.getAbsolutePath());

            SheetDimension sheetDimension = businessLogic.getSheetDimension();
            SheetDto sheetDto = businessLogic.getSheet(businessLogic.getCurrentSheetVersion());

            sheetComponentController.initMainGrid(businessLogic, sheetDimension, sheetDto);

        } catch (Exception e) {
            loadFileComponentController.loadFileFailed(e.getMessage());
        }
    }

    public CellDto cellClicked(String cellPositionId) {
        return businessLogic.cellClicked(cellPositionId);
    }

    public void updateCell(String cellNewOriginalValue) {
        try {
            CellDto cellDto = businessLogic.updateCell(cellNewOriginalValue);
            sheetComponentController.updateCell(cellDto);
            actionLineComponentController.updateCellSucceeded();
        } catch (Exception e) {
            actionLineComponentController.updateCellFailed(e.getMessage());
        }
    }

    public void selectSheetVersion(int version) {
        SheetDto sheetDto = businessLogic.getSheet(version);
        SheetDimension sheetDimension = businessLogic.getSheetDimension();

        sheetComponentController.showSheetInVersion(sheetDimension, sheetDto, version);
    }
}