package client.component.dashboard;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class DashboardModelUI {
    private final ObservableMap<SimpleStringProperty, SheetNameData> sheetNameProperty2itsData;
    private final ObservableList<SheetsTableEntry> sheetsTableData;

    private final ObservableMap<SimpleStringProperty, PermissionUsernameData> permissionUsernameProperty2itsData;
    private final ObservableList<PermissionsTableEntry> permissionsTableData;

    private final BooleanProperty isSheetClicked;
    private final BooleanProperty isPermissionClicked;
    private final StringProperty selectedSheetName;
    private final BooleanProperty isOwner;
    private final BooleanProperty isAllowedToViewSheet;

    public DashboardModelUI(Button viewSheetButton, Button requestPermissionButton, List<Button> ownerOnlyButtons,
                            TableView<SheetsTableEntry> showSheetsTable,
                            TableColumn<SheetsTableEntry, String> sheetNameColumn,
                            TableColumn<SheetsTableEntry, String> ownerUsernameColumn,
                            TableColumn<SheetsTableEntry, String> sheetSizeColumn,
                            TableColumn<SheetsTableEntry, String> yourPermissionTypeColumn,

                            TableView<PermissionsTableEntry> showPermissionsTable,
                            TableColumn<PermissionsTableEntry, String> usernameColumn,
                            TableColumn<PermissionsTableEntry, String> permissionTypeColumn,
                            TableColumn<PermissionsTableEntry, String> approvalStateColumn) {

        isSheetClicked = new SimpleBooleanProperty(false);
        isPermissionClicked = new SimpleBooleanProperty(false);
        selectedSheetName = new SimpleStringProperty();
        isOwner = new SimpleBooleanProperty(false);
        isAllowedToViewSheet = new SimpleBooleanProperty(false);

        // view sheet when click on sheet entry and has a view sheet permission (write/reader)
        viewSheetButton.disableProperty().bind(isSheetClicked.not().or(isAllowedToViewSheet.not()));

        // can request a permission only when a sheet entry is selected
        requestPermissionButton.disableProperty().bind(isSheetClicked.not().or(isOwner));

        // can accept/reject permissions only if you are the owner of the sheet you selected
        for (Button button : ownerOnlyButtons) {
            button.disableProperty().bind(isOwner.not().or(isPermissionClicked.not()));
        }

        sheetNameProperty2itsData = FXCollections.observableHashMap();
        sheetsTableData = FXCollections.observableArrayList();

        permissionUsernameProperty2itsData = FXCollections.observableHashMap();
        permissionsTableData = FXCollections.observableArrayList();

        bindSheetsTableView(showSheetsTable, ownerUsernameColumn, sheetNameColumn, sheetSizeColumn, yourPermissionTypeColumn);
        bindPermissionsTableView(showPermissionsTable, usernameColumn, permissionTypeColumn, approvalStateColumn);
    }

    private void bindSheetsTableView(TableView<SheetsTableEntry> showSheetsTable,
                                     TableColumn<SheetsTableEntry, String> ownerUsernameColumn,
                                     TableColumn<SheetsTableEntry, String> sheetNameColumn,
                                     TableColumn<SheetsTableEntry, String> sheetSizeColumn,
                                     TableColumn<SheetsTableEntry, String> yourPermissionTypeColumn) {

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
                                nameProperty.get(),
                                sheetNameData.ownerName.get(),
                                sheetNameData.sheetSize.get(),
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

    private void bindPermissionsTableView(TableView<PermissionsTableEntry> showPermissionsTable,
                            TableColumn<PermissionsTableEntry, String> usernameColumn,
                            TableColumn<PermissionsTableEntry, String> permissionTypeColumn,
                            TableColumn<PermissionsTableEntry, String> approvalStateColumn) {

        // Initialize the columns in the TableView
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        permissionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().permissionTypeProperty());
        approvalStateColumn.setCellValueFactory(cellData -> cellData.getValue().approvalStateProperty());

        // bind the sheets data to the table
        showPermissionsTable.setItems(permissionsTableData);

        // Add a MapChangeListener to the map to listen for new entries
        permissionUsernameProperty2itsData.addListener((MapChangeListener<SimpleStringProperty, PermissionUsernameData>) change -> {
            SimpleStringProperty usernameProperty = change.getKey();
            PermissionUsernameData permissionUsernameData = change.getValueAdded();

            if (change.wasAdded()) {
                permissionsTableData.add(
                        new PermissionsTableEntry(
                                usernameProperty.get(),
                                permissionUsernameData.permissionType.get(),
                                permissionUsernameData.approvalState.get(),
                                permissionUsernameData.requestUid.get()
                        )
                );
            }
        });
    }

    // permission username column and other columns
    public static class PermissionUsernameData {
        private final StringProperty permissionType;
        private final StringProperty approvalState;
        private final StringProperty requestUid;

        public PermissionUsernameData(String permissionType, String approvalState, String requestUid) {
            this.permissionType = new SimpleStringProperty(permissionType);
            this.approvalState = new SimpleStringProperty(approvalState);
            this.requestUid = new SimpleStringProperty(requestUid);
        }
    }

    public static class PermissionsTableEntry {
        private final StringProperty username;
        private final StringProperty permissionType;
        private final StringProperty approvalState;
        private final StringProperty requestUid;

        public PermissionsTableEntry(String username, String permissionType, String approvalState, String requestUid) {
            this.username = new SimpleStringProperty(username);
            this.permissionType = new SimpleStringProperty(permissionType);
            this.approvalState = new SimpleStringProperty(approvalState);
            this.requestUid = new SimpleStringProperty(requestUid);
        }

        public StringProperty usernameProperty() {
            return username;
        }

        public StringProperty permissionTypeProperty() {
            return permissionType;
        }

        public StringProperty approvalStateProperty() {
            return approvalState;
        }

        public StringProperty requestUidProperty() {
            return requestUid;
        }
    }

    public BooleanProperty isSheetClickedProperty() {
        return isSheetClicked;
    }

    public BooleanProperty isPermissionClicked() {
        return isPermissionClicked;
    }

    public StringProperty selectedSheetNameProperty() {
        return selectedSheetName;
    }

    public BooleanProperty isOwnerProperty() {
        return isOwner;
    }

    public BooleanProperty isAllowedToViewSheetProperty() {
        return isAllowedToViewSheet;
    }

    public void addSheetUserPermission(String username, String permissionType, String approvalState, String requestUid) {
        permissionUsernameProperty2itsData.put(new SimpleStringProperty(username),
                new PermissionUsernameData(permissionType, approvalState, requestUid));
    }
}