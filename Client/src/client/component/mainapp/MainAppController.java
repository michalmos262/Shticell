package client.component.mainapp;

import client.component.dashboard.DashboardController;
import client.component.login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import static client.resources.CommonResourcesPaths.DASHBOARD_PAGE_FXML_RESOURCE_LOCATION;
import static client.resources.CommonResourcesPaths.LOGIN_PAGE_FXML_RESOURCE_LOCATION;

public class MainAppController implements Closeable {
    @FXML private AnchorPane mainPanel;
    @FXML private AnchorPane loginComponent;
    @FXML private LoginController loginComponentController;
    @FXML private BorderPane dashboardComponent;
    @FXML private DashboardController dashboardComponentController;

    @FXML
    public void initialize() {
        // prepare components
        loadLoginPage();
        loadDashboardPage();
    }

    private void setMainPanelTo(Parent pane) {
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(pane);
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

    public void switchToDashboardPage() {
        setMainPanelTo(dashboardComponent);
    }
}
