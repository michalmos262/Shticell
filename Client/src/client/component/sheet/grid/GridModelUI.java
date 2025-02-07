package client.component.sheet.grid;

import dto.cell.CellPositionDto;
import dto.sheet.SheetDto;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class GridModelUI {
    private final GridPane gridPane;
    private final Map<CellPositionDto, CellProperties> cellPosition2displayedValue;
    private final Map<CellPositionDto, CellProperties> cellPosition2displayedValueDynamicAnalysis;

    public GridModelUI(GridPane gridPane) {
        this.gridPane = gridPane;
        cellPosition2displayedValue = new HashMap<>();
        cellPosition2displayedValueDynamicAnalysis = new HashMap<>();
    }

    public Map<CellPositionDto, CellProperties> getCellPosition2displayedValue() {
        return cellPosition2displayedValue;
    }

    public Map<CellPositionDto, CellProperties> getCellPosition2displayedValueDynamicAnalysis() {
        return cellPosition2displayedValueDynamicAnalysis;
    }

    public void setCellLabelBinding(Label label, SheetDto sheetDto, CellPositionDto cellPosition) {
        SimpleStringProperty displayedValue = getDisplayedValue(sheetDto, cellPosition);
        CellProperties cellProperties = new CellProperties(displayedValue);

        cellPosition2displayedValue.put(cellPosition, cellProperties);
        label.textProperty().bind(cellPosition2displayedValue.get(cellPosition).displayedValue);

        cellPosition2displayedValue.get(cellPosition).backgroundColorProperty()
                .addListener((observable, oldValue, newValue) -> label.setStyle(label.getStyle() + "-fx-background-color: " +
                        colorToHex(newValue) + ";"));

        cellPosition2displayedValue.get(cellPosition).textColorProperty()
                .addListener((observable, oldValue, newValue) -> label.setStyle(label.getStyle() + ";-fx-text-fill: " +
                        colorToHex(newValue) + ";"));

    }

    public void setCellLabelBindingDynamicAnalysis(Label label, SheetDto sheetDto, CellPositionDto cellPosition) {
        SimpleStringProperty displayedValue = getDisplayedValue(sheetDto, cellPosition);
        CellProperties cellProperties = new CellProperties(displayedValue);

        cellPosition2displayedValueDynamicAnalysis.put(cellPosition, cellProperties);
        label.textProperty().bind(cellPosition2displayedValueDynamicAnalysis.get(cellPosition).displayedValue);
    }

    public SimpleStringProperty getDisplayedValue(SheetDto sheetDto, CellPositionDto cellPosition) {
        return sheetDto.getCell(cellPosition) == null
                ? new SimpleStringProperty("")
                : new SimpleStringProperty(sheetDto.getCell(cellPosition)
                    .getEffectiveValueForDisplay().toString());
    }

    public void setRowsAndColumnsBindings(CellPositionDto primaryCellPosition) {
        cellPosition2displayedValue.get(primaryCellPosition).columnWidthProperty()
                .addListener((observable, oldValue, newValue) -> {
                    cellPosition2displayedValue.forEach((cellPosition, properties) -> {
                        if (cellPosition.getColumn() == primaryCellPosition.getColumn()) {
                            Label labelInColumn = (Label) gridPane.lookup("#" + cellPosition);
                            labelInColumn.setStyle(labelInColumn.getStyle() + ";-fx-min-width: " + newValue +
                                    ";-fx-pref-width: " + newValue + ";-fx-max-width: " + newValue + ";");
                        }
                    });
                    Label columnLabel = (Label) gridPane.lookup("#" + CellPositionDto.parseColumn(primaryCellPosition.getColumn()));
                    columnLabel.setStyle(columnLabel.getStyle() + ";-fx-min-width: " + newValue +
                            ";-fx-pref-width: " + newValue + ";-fx-max-width: " + newValue + ";");
                });

        cellPosition2displayedValue.get(primaryCellPosition).rowHeightProperty()
                .addListener((observable, oldValue, newValue) -> {
                    cellPosition2displayedValue.forEach((cellPosition, properties) -> {
                        if (cellPosition.getRow() == primaryCellPosition.getRow()) {
                            Label labelInRow = (Label) gridPane.lookup("#" + cellPosition);
                            labelInRow.setStyle(labelInRow.getStyle() + ";-fx-min-height: " + newValue +
                                    ";-fx-pref-height: " + newValue + ";-fx-max-height: " + newValue + ";");
                        }
                    });
                    Label rowLabel = (Label) gridPane.lookup("#" + primaryCellPosition.getRow());
                    rowLabel.setStyle(rowLabel.getStyle() + ";-fx-min-height: " + newValue +
                            ";-fx-pref-height: " + newValue + ";-fx-max-height: " + newValue + ";");
                });

        cellPosition2displayedValue.get(primaryCellPosition).textAlignmentProperty()
                .addListener((observable, oldValue, newValue) ->
                        cellPosition2displayedValue.forEach((cellPosition, properties) -> {
                            if (cellPosition.getColumn() == primaryCellPosition.getColumn()) {
                                Label labelInColumn = (Label) gridPane.lookup("#" + cellPosition);
                                labelInColumn.setStyle(labelInColumn.getStyle() + ";-fx-alignment: " + newValue + ";");
                                labelInColumn.setAlignment(newValue);
                            }
                        })
                );
    }

    public static class CellProperties {
        private final SimpleStringProperty displayedValue;
        private final SimpleObjectProperty<Color> backgroundColor;
        private final SimpleObjectProperty<Color> textColor;
        private final SimpleObjectProperty<Pos> textAlignment;
        private final SimpleIntegerProperty rowHeight;
        private final SimpleIntegerProperty columnWidth;

        public CellProperties(SimpleStringProperty displayedValue) {
            this.displayedValue = displayedValue;
            this.backgroundColor = new SimpleObjectProperty<>(Color.WHITE);
            this.textColor = new SimpleObjectProperty<>(Color.BLACK);
            this.textAlignment = new SimpleObjectProperty<>(Pos.TOP_LEFT);
            this.rowHeight = new SimpleIntegerProperty(0);
            this.columnWidth = new SimpleIntegerProperty(0);
        }

        public SimpleStringProperty displayedValueProperty() {
            return displayedValue;
        }

        public SimpleObjectProperty<Color> backgroundColorProperty() {
            return backgroundColor;
        }

        public SimpleObjectProperty<Color> textColorProperty() {
            return textColor;
        }

        public SimpleObjectProperty<Pos> textAlignmentProperty() {
            return textAlignment;
        }

        public SimpleIntegerProperty rowHeightProperty() {
            return rowHeight;
        }

        public SimpleIntegerProperty columnWidthProperty() {
            return columnWidth;
        }
    }

    // Method to convert Color to Hex String
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255));
    }
}
