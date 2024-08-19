package engine.impl;

import engine.api.Engine;
import engine.entity.cell.*;
import engine.entity.dto.CellDto;
import engine.entity.sheet.Sheet;
import engine.entity.sheet.SheetDimension;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.SheetManager;
import engine.jaxb.schema.generated.STLCell;
import engine.jaxb.schema.generated.STLSheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import static engine.expression.impl.ExpressionEvaluator.evaluateExpression;

public class ShticellEngine implements Engine {
    private final SheetManager sheetManager;

    public ShticellEngine(String fileName) throws JAXBException {
        sheetManager = buildSheetManagerFromXml(fileName);
    }

    @Override
    public String getSheetName() {
        return sheetManager.getName();
    }

    @Override
    public int getCurrentSheetVersion() {
        return sheetManager.getCurrentVersion();
    }

    @Override
    public SheetDimension getSheetDimension() {
        return sheetManager.getDimension();
    }

    @Override
    public CellDto findCellInSheet(int row, int column, int sheetVersion) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetVersion);
        SheetDto sheetDto = new SheetDto(sheet);
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return sheetDto.getCell(cellPosition);
    }

    @Override
    public int getLastCellVersion(int row, int column) {
        Sheet sheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion());
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        return sheet.getCell(cellPosition).getLastUpdatedInVersion();
    }

    @Override
    public List<CellPositionInSheet> getInfluencedByList(int row, int column, int sheetVersion) {
        return findCellInSheet(row, column, sheetVersion).getInfluencedBy();
    }

    @Override
    public List<CellPositionInSheet> getInfluencesList(int row, int column, int sheetVersion) {
        return findCellInSheet(row, column, sheetVersion).getInfluences();
    }

    public static EffectiveValue handleEffectiveValue(Sheet sheet, CellPositionInSheet cellPosition, String originalValue) {
        EffectiveValue effectiveValue;

        if (originalValue.matches("-?\\d+(\\.\\d+)?")) {
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            effectiveValue = new EffectiveValue(CellType.NUMERIC, formatter.format(new BigDecimal(originalValue)));
        }
        else if (originalValue.equalsIgnoreCase("true") || originalValue.equalsIgnoreCase("false")) {
            effectiveValue = new EffectiveValue(CellType.BOOLEAN, originalValue.toUpperCase());
        }
        else if (originalValue.charAt(0) == '{' && originalValue.charAt(originalValue.length() - 1) == '}') {
            List<CellPositionInSheet> influencingCellPositions = new LinkedList<>();
            effectiveValue = evaluateExpression(sheet, originalValue, influencingCellPositions).getEffectiveValue();
            //NEED TO CHECK IF WORKS!
//            effectiveValue = handleEffectiveValue(sheet, cellPosition, effectiveValue.toString());
            for (CellPositionInSheet influencingPosition : influencingCellPositions) {
                sheet.addCellConnection(influencingPosition, cellPosition);
            }
        }
        else {
            effectiveValue = new EffectiveValue(CellType.STRING, originalValue);
        }

        return effectiveValue;
    }

    //RECURSIVE UPDATE
    private void updateInfluencedByCell(Sheet sheet, CellPositionInSheet InfluencerCellPosition, Set<CellPositionInSheet> visited) {
        Cell cell = sheet.getCell(InfluencerCellPosition);
        List<CellPositionInSheet> influencedCellPositions = new LinkedList<>(cell.getInfluences());
        for (CellPositionInSheet influencedByCellPosition : influencedCellPositions) {
            Cell influencedByCell = sheet.getCell(influencedByCellPosition);
            String originalValue = influencedByCell.getOriginalValue();
            EffectiveValue effectiveValue = handleEffectiveValue(sheet, influencedByCellPosition, originalValue);
            sheet.updateCell(influencedByCellPosition, originalValue, effectiveValue);
            visited.add(influencedByCellPosition);
            updateInfluencedByCell(sheet, influencedByCellPosition, visited);
        }
    }

    @Override
    //THE FIRST UPDATE
    public void updateSheetCell(int row, int column, String newOriginalValue) {
        int cellsUpdatedCounter = 1;
        Sheet clonedSheet = sheetManager.getSheetByVersion(sheetManager.getCurrentVersion()).clone();
        CellPositionInSheet cellPosition = PositionFactory.createPosition(row, column);
        Cell cellInUpdate = clonedSheet.getCell(cellPosition);
        EffectiveValue effectiveValue;

        if (cellInUpdate == null) { // need to create new cell
            clonedSheet.createNewCell(cellPosition, newOriginalValue);
        } else {
            List<CellPositionInSheet> influencedByCellPositions = new LinkedList<>(cellInUpdate.getInfluencedBy());
            for (CellPositionInSheet influencingCellPosition: influencedByCellPositions) {
                clonedSheet.removeCellConnection(influencingCellPosition, cellPosition);
            }
        }
        effectiveValue = handleEffectiveValue(clonedSheet, cellPosition, newOriginalValue);
        clonedSheet.updateCell(cellPosition, newOriginalValue, effectiveValue);
        Set<CellPositionInSheet> visitedCellPositions = new HashSet<>();
        updateInfluencedByCell(clonedSheet, cellPosition, visitedCellPositions);
        cellsUpdatedCounter += visitedCellPositions.size();
        clonedSheet.setUpdatedCellsCount(cellsUpdatedCounter);
        sheetManager.addNewSheet(clonedSheet);
    }

    public SheetManager buildSheetManagerFromXml(String fileName) throws JAXBException {
        File file = new File(fileName);
        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        STLSheet jaxbSheet = (STLSheet) jaxbUnmarshaller.unmarshal(file);

        int numOfRows = jaxbSheet.getSTLLayout().getRows();
        int numOfColumns = jaxbSheet.getSTLLayout().getColumns();
        int rowHeight = jaxbSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        int columnWidth = jaxbSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();

        SheetDimension dimension = new SheetDimension(numOfRows, numOfColumns, rowHeight, columnWidth);
        SheetManager sheetManager = new SheetManager(jaxbSheet.getName(), dimension);
        Sheet sheet = new Sheet();
        int cellsUpdatedCounter = 1;

        for (STLCell jaxbCell: jaxbSheet.getSTLCells().getSTLCell()) {
            CellPositionInSheet cellPosition = PositionFactory.createPosition(jaxbCell.getRow(), jaxbCell.getColumn());
            String originalValue = jaxbCell.getSTLOriginalValue();
            sheet.createNewCell(cellPosition, originalValue);

            EffectiveValue effectiveValue = handleEffectiveValue(sheet, cellPosition, originalValue);
            sheet.updateCell(cellPosition, originalValue, effectiveValue);
            Set<CellPositionInSheet> visitedCellPositions = new HashSet<>();
            updateInfluencedByCell(sheet, cellPosition, visitedCellPositions);
            cellsUpdatedCounter += visitedCellPositions.size();
            sheet.setUpdatedCellsCount(cellsUpdatedCounter);
        }
        sheetManager.addNewSheet(sheet);
        return sheetManager;
    }

    @Override
    public Map<Integer, Integer> getSheetVersions() {
        Map<Integer, Integer> version2cellsUpdatedCount = new HashMap<>();
        sheetManager.getVersion2sheet().forEach((version, sheet) ->
                version2cellsUpdatedCount.put(version, sheet.getUpdatedCellsCount()));
        return version2cellsUpdatedCount;
    }

    @Override
    public CellPositionInSheet getCellPositionInSheet(int row, int column) {
        return PositionFactory.createPosition(row, column);
    }

    @Override
    public CellPositionInSheet getCellPositionInSheet(String position) {
        return PositionFactory.createPosition(position);
    }

    @Override
    public int parseRowFromPosition(String position) {
        return getCellPositionInSheet(position).getRow();
    }

    @Override
    public int parseColumnFromPosition(String position) {
        return getCellPositionInSheet(position).getColumn();
    }
}