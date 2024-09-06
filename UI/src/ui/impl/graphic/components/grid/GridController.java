package ui.impl.graphic.components.grid;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.PositionFactory;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.impl.SheetDimension;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ui.impl.graphic.components.app.MainAppController;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GridController {

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridPane;

    private MainAppController mainAppController;
    private Map<CellPositionInSheet, StringProperty> cellPosition2displayedValue;

    public GridController() {
        cellPosition2displayedValue = new HashMap<>();
    }

    public void setMainController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    @FXML
    private void handleCellClick(MouseEvent event) {
        Label clickedLabel = (Label) event.getSource();
        mainAppController.cellClicked(clickedLabel.getId());
    }

    public void initGrid(SheetDimension sheetDimension, SheetDto sheetDto) {
        int numOfRows = sheetDimension.getNumOfRows();
        int numOfColumns = sheetDimension.getNumOfColumns();
        int rowHeight = sheetDimension.getRowHeight() * 10;
        int columnWidth = sheetDimension.getColumnWidth() * 10;

         // Clear the existing content in the gridContainer
        gridPane.getChildren().clear();

        // Add the column headers (A, B, C, ...)
        for (int col = 0; col < numOfColumns; col++) {
            Label columnHeader = new Label(String.valueOf((char) ('A' + col)));
            columnHeader.setFont(Font.font("Arial", 14));
            columnHeader.setPrefHeight((double) rowHeight / 2);
            columnHeader.setPrefWidth(columnWidth);
            columnHeader.setStyle("-fx-border-color: black; -fx-background-color: lightgray; -fx-padding: 10;");
            columnHeader.setAlignment(Pos.CENTER);
            gridPane.add(columnHeader, col + 1, 0);  // Place the column header in the first row
        }

        // Add the row headers (1, 2, 3, ...)
        for (int row = 0; row < numOfRows; row++) {
            Label rowHeader = new Label(String.valueOf(row + 1));
            rowHeader.setFont(Font.font("Arial", 14));
            rowHeader.setPrefHeight(rowHeight);
            rowHeader.setPrefWidth((double) columnWidth / 2);
            rowHeader.setStyle("-fx-border-color: black; -fx-background-color: lightgray; -fx-padding: 10;");
            rowHeader.setAlignment(Pos.CENTER);
            gridPane.add(rowHeader, 0, row + 1);  // Place the row header in the first column
        }

        // Populate the GridPane with Labels in the main grid area
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(row+1, col+1);
                Label label = new Label();
                label.setId((char) ('A' + col) + String.valueOf(row + 1));
                StringProperty strProperty = sheetDto.getCell(cellPositionInSheet) == null
                        ? new SimpleStringProperty("")
                        : new SimpleStringProperty(sheetDto.getCell(cellPositionInSheet)
                            .getEffectiveValueForDisplay().toString());
                cellPosition2displayedValue.put(cellPositionInSheet, strProperty);
                label.textProperty().bind(cellPosition2displayedValue.get(cellPositionInSheet));
                label.setPrefHeight(rowHeight);
                label.setPrefWidth(columnWidth);
                label.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-padding: 10;");
                label.setAlignment(Pos.CENTER);

                // Attach the click event handler
                label.setOnMouseClicked(this::handleCellClick);

                gridPane.add(label, col + 1, row + 1);  // Offset by 1 to leave space for headers
            }
        }

        // Force a layout pass after adding new nodes
        gridPane.requestLayout();
    }

    //TODO: Delete saveGridPaneToFXML?
    public static void saveGridPaneToFXML(GridPane gridPane, String fileName) {
        try {
            // Create a new XML Document
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Root element: GridPane
            Element rootElement = doc.createElement("GridPane");
            doc.appendChild(rootElement);

            // Add each element from the gridPane
            for (Node node : gridPane.getChildren()) {
                if (node instanceof Label) {
                    Element labelElement = doc.createElement("Label");
                    Label label = (Label) node;
                    labelElement.setAttribute("text", label.getText());

                    Integer colIndex = GridPane.getColumnIndex(node);
                    Integer rowIndex = GridPane.getRowIndex(node);

                    if (colIndex != null) {
                        labelElement.setAttribute("GridPane.columnIndex", colIndex.toString());
                    }

                    if (rowIndex != null) {
                        labelElement.setAttribute("GridPane.rowIndex", rowIndex.toString());
                    }

                    rootElement.appendChild(labelElement);
                }
            }

            // Write the content into an XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));

            transformer.transform(source, result);

            System.out.println("GridPane saved to " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}