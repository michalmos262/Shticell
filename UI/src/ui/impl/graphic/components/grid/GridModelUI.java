package ui.impl.graphic.components.grid;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.dto.SheetDto;
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
    private final SimpleBooleanProperty isFileLoading;
    private final GridPane gridPane;
    private final Map<CellPositionInSheet, CellProperties> cellPosition2displayedValue;

    public GridModelUI(GridPane gridPane) {
        this.gridPane = gridPane;
        isFileLoading = new SimpleBooleanProperty(false);
        cellPosition2displayedValue = new HashMap<>();
    }

    public Map<CellPositionInSheet, CellProperties> getCellPosition2displayedValue() {
        return cellPosition2displayedValue;
    }

    public void setCellLabelBinding(Label label, SheetDto sheetDto, CellPositionInSheet primaryCellPosition) {
        SimpleStringProperty displayedValue = sheetDto.getCell(primaryCellPosition) == null
                ? new SimpleStringProperty("")
                : new SimpleStringProperty(sheetDto.getCell(primaryCellPosition)
                    .getEffectiveValueForDisplay().toString());

        CellProperties cellProperties = new CellProperties(displayedValue);

        cellPosition2displayedValue.put(primaryCellPosition, cellProperties);
        label.textProperty().bind(cellPosition2displayedValue.get(primaryCellPosition).displayedValue);
        label.disableProperty().bind(isFileLoading);

        cellPosition2displayedValue.get(primaryCellPosition).backgroundColorProperty()
                .addListener((observable, oldValue, newValue) -> label.setStyle(label.getStyle() + "-fx-background-color: " +
                        colorToHex(newValue) + ";"));

        cellPosition2displayedValue.get(primaryCellPosition).textColorProperty()
                .addListener((observable, oldValue, newValue) -> label.setStyle(label.getStyle() + ";-fx-text-fill: " +
                        colorToHex(newValue) + ";"));

    }

    public void setRowsAndColumnsBindings(CellPositionInSheet primaryCellPosition) {
        cellPosition2displayedValue.get(primaryCellPosition).columnWidthProperty()
                .addListener((observable, oldValue, newValue) ->
                        cellPosition2displayedValue.forEach((cellPosition, properties) -> {
                            if (cellPosition.getColumn() == primaryCellPosition.getColumn()) {
                                Label labelInColumn = (Label) gridPane.lookup("#" + cellPosition);
                                labelInColumn.setStyle(labelInColumn.getStyle() + ";-fx-min-width: " + newValue +
                                        ";-fx-pref-width: " + newValue + ";-fx-max-width: " + newValue + ";");
                            }
                        })
                );

        cellPosition2displayedValue.get(primaryCellPosition).rowHeightProperty()
                .addListener((observable, oldValue, newValue) ->
                        cellPosition2displayedValue.forEach((cellPosition, properties) -> {
                            if (cellPosition.getRow() == primaryCellPosition.getRow()) {
                                Label labelInRow = (Label) gridPane.lookup("#" + cellPosition);
                                labelInRow.setStyle(labelInRow.getStyle() + ";-fx-min-height: " + newValue +
                                        ";-fx-pref-height: " + newValue + ";-fx-max-height: " + newValue + ";");
                            }
                        })
                );

        cellPosition2displayedValue.get(primaryCellPosition).textAlignmentProperty()
                .addListener((observable, oldValue, newValue) ->
                        cellPosition2displayedValue.forEach((cellPosition, properties) -> {
                            if (cellPosition.getColumn() == primaryCellPosition.getColumn()) {
                                Label labelInColumn = (Label) gridPane.lookup("#" + cellPosition);
                                labelInColumn.setStyle(labelInColumn.getStyle() + ";-fx-alignment: " + newValue + ";");
                            }
                        })
                );
    }

    public SimpleBooleanProperty isFileLoadingProperty() {
        return isFileLoading;
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
