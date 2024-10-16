package client.component.mainapp;

import client.component.dashboard.DashboardController;
import client.component.login.LoginController;
import client.component.sheet.mainsheet.MainSheetController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static client.resources.CommonResourcesPaths.*;

public class MainAppController implements Closeable {
    @FXML private Label headingLabel;
    @FXML private Label loggedInAsLabel;
    @FXML private SplitPane mainSplitPane;
    @FXML private AnchorPane contentAnchorPane;
    @FXML private AnchorPane loginComponent;
    @FXML private LoginController loginComponentController;
    @FXML private BorderPane dashboardComponent;
    @FXML private DashboardController dashboardComponentController;

    private MainModelUI modelUi;
    private Map<String, BorderPane> sheetName2Component;
    private Map<String, MainSheetController> sheetName2Controller;

    @FXML
    public void initialize() {
        modelUi = new MainModelUI(mainSplitPane, headingLabel, loggedInAsLabel);
        sheetName2Component = new HashMap<>();
        sheetName2Controller = new HashMap<>();

        // prepare components
        loadLoginPage();
        loadDashboardPage();

        setMainPanelTo(loginComponent);
    }

    private void setMainPanelTo(Parent pane) {
        contentAnchorPane.getChildren().clear();
        contentAnchorPane.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
    }

    private void loadLoginPage() {
        URL loginPageUrl = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load();
            loginComponentController = fxmlLoader.getController();
            loginComponentController.setMainAppController(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadDashboardPage() {
        URL dashboardPageUrl = getClass().getResource(DASHBOARD_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(dashboardPageUrl);
            dashboardComponent = fxmlLoader.load();
            dashboardComponentController = fxmlLoader.getController();
            dashboardComponentController.setMainAppController(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadSheetPage(String sheetName) {
        // if the sheet has not loaded yet
        if (!sheetName2Component.containsKey(sheetName)) {
            URL sheetPageUrl = getClass().getResource(MAIN_SHEET_PAGE_FXML_RESOURCE_LOCATION);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(sheetPageUrl);
                sheetName2Component.put(sheetName, fxmlLoader.load());

                MainSheetController sheetController = fxmlLoader.getController();
                sheetName2Controller.put(sheetName, sheetController);
                sheetController.setMainAppController(this);
                sheetController.initComponents(sheetName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        dashboardComponentController.close();
        for (MainSheetController sheetController : sheetName2Controller.values()) {
            sheetController.close();
        }
    }

    public void loggedIn(String username) {
        modelUi.usernameProperty().set(username);
    }

    public String getLoggedInUsername() {
        return modelUi.usernameProperty().getValue();
    }

    public void switchToDashboardPage() {
        modelUi.pageHeadingProperty().set("Management Dashboard");
        setMainPanelTo(dashboardComponent);
        dashboardComponentController.setActive();
    }

    public void switchToSheet(String sheetName) {
        loadSheetPage(sheetName);
        modelUi.pageHeadingProperty().set("In sheet: " + sheetName);
        setMainPanelTo(sheetName2Component.get(sheetName));
        sheetName2Controller.get(sheetName).setActive();
    }
}