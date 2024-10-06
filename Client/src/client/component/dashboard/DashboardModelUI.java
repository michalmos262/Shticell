package client.component.dashboard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardModelUI {
    ObservableMap<SimpleStringProperty, SheetNameData> sheetNameProperty2itsData;
    private final ObservableList<SheetsTableEntry> sheetsTableData;

    public DashboardModelUI(TableView<SheetsTableEntry> showSheetsTable,
                            TableColumn<SheetsTableEntry, String> sheetNameColumn,
                            TableColumn<SheetsTableEntry, String> ownerUsernameColumn,
                            TableColumn<SheetsTableEntry, String> sheetSizeColumn,
                            TableColumn<SheetsTableEntry, String> yourPermissionTypeColumn) {
        sheetNameProperty2itsData = FXCollections.observableHashMap();
        sheetsTableData = FXCollections.observableArrayList();
        bindSheetsTableView(showSheetsTable, ownerUsernameColumn, sheetNameColumn, sheetSizeColumn,
                yourPermissionTypeColumn);
    }

    private void bindSheetsTableView(TableView<SheetsTableEntry> showSheetsTable, TableColumn<SheetsTableEntry, String> ownerUsernameColumn,
                                     TableColumn<SheetsTableEntry, String> sheetNameColumn, TableColumn<SheetsTableEntry,
            String> sheetSizeColumn, TableColumn<SheetsTableEntry, String> yourPermissionTypeColumn) {

        // Initialize the columns in the TableView
        sheetNameColumn.setCellValueFactory(cellData -> cellData.getValue().sheetNameProperty());
        ownerUsernameColumn.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
        sheetSizeColumn.setCellValueFactory(cellData -> cellData.getValue().sheetSizeProperty());
        yourPermissionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().yourPermissionTypeProperty());

        // bind the sheets data to the table
        showSheetsTable.setItems(sheetsTableData);

        // Add a MapChangeListener to the map to listen for new entries
        sheetNameProperty2itsData.addListener((MapChangeListener<SimpleStringProperty, SheetNameData>) change -> {
            SimpleStringProperty nameProperty = change.getKey();
            SheetNameData sheetNameData = change.getValueAdded();

            if (change.wasAdded()) {
                sheetsTableData.add(
                        new SheetsTableEntry(
                                nameProperty.get(), sheetNameData.ownerName.get(), sheetNameData.sheetSize.get(),
                                sheetNameData.yourPermissionType.get()
                        )
                );
            }
        });
    }

    public void addSheet(String sheetName, String ownerName, String sheetSize, String yourPermissionType) {
        sheetNameProperty2itsData.put(new SimpleStringProperty(sheetName),
                new SheetNameData(ownerName, sheetSize, yourPermissionType));
    }

    // sheet name column and other columns
    private static class SheetNameData {
        private final StringProperty ownerName;
        private final StringProperty sheetSize;
        private final StringProperty yourPermissionType;

        public SheetNameData(String ownerName, String sheetSize, String yourPermissionType) {
            this.ownerName = new SimpleStringProperty(ownerName);
            this.sheetSize = new SimpleStringProperty(sheetSize);
            this.yourPermissionType = new SimpleStringProperty(yourPermissionType);
        }
    }

    public static class SheetsTableEntry {
        private final StringProperty sheetName;
        private final StringProperty ownerName;
        private final StringProperty sheetSize;
        private final StringProperty yourPermissionType;

        public SheetsTableEntry(String sheetName, String ownerName, String sheetSize, String yourPermissionType) {
            this.sheetName = new SimpleStringProperty(sheetName);
            this.ownerName = new SimpleStringProperty(ownerName);
            this.sheetSize = new SimpleStringProperty(sheetSize);
            this.yourPermissionType = new SimpleStringProperty(yourPermissionType);
        }

        public StringProperty sheetNameProperty() {
            return sheetName;
        }

        public StringProperty ownerNameProperty() {
            return ownerName;
        }

        public StringProperty sheetSizeProperty() {
            return sheetSize;
        }

        public StringProperty yourPermissionTypeProperty() {
            return yourPermissionType;
        }
    }

    public static class PermissionsTableEntry {

    }
}