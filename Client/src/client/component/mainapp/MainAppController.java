package client.component.mainapp;

import client.component.dashboard.DashboardController;
import client.component.login.LoginController;
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

import static client.resources.CommonResourcesPaths.DASHBOARD_PAGE_FXML_RESOURCE_LOCATION;
import static client.resources.CommonResourcesPaths.LOGIN_PAGE_FXML_RESOURCE_LOCATION;

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

    @FXML
    public void initialize() {
        // prepare components
        loadLoginPage();
        loadDashboardPage();

        modelUi = new MainModelUI(loggedInAsLabel);
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
            setMainPanelTo(loginComponent);
            headingLabel.setText("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboardPage() {
        URL loginPageUrl = getClass().getResource(DASHBOARD_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            dashboardComponent = fxmlLoader.load();
            dashboardComponentController = fxmlLoader.getController();
            dashboardComponentController.setMainAppController(this);
            setMainPanelTo(loginComponent);
            headingLabel.setText("Management Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSheetPage() {
        //todo
    }

    @Override
    public void close() throws IOException {
        //todo
    }

    public void updateUserName(String userName) {

    }

    public void loggedIn(String username) {
        modelUi.usernameProperty().set(username);
    }

    public void switchToDashboardPage() {
        setMainPanelTo(dashboardComponent);
    }
}
