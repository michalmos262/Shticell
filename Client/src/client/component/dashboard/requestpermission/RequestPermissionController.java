package client.component.dashboard.requestpermission;

import client.component.alert.AlertsHandler;
import client.component.dashboard.DashboardController;
import client.util.http.HttpClientUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import serversdk.request.body.CreatePermissionRequestBody;

import java.io.IOException;

import static client.resources.CommonResourcesPaths.*;
import static client.resources.CommonResourcesPaths.GSON_INSTANCE;

public class RequestPermissionController {

    @FXML private Button sendPermissionRequestButton;
    @FXML private RadioButton readerRadioButton;
    @FXML private RadioButton writerRadioButton;

    private DashboardController dashboardController;
    private String sheetName;
    private String permission;

    @FXML
    private void initialize() {

    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void readerRadioButtonListener(ActionEvent event) {
        if (writerRadioButton.isSelected()) {
            writerRadioButton.setSelected(false);
        }
        permission = readerRadioButton.getText();
    }

    @FXML
    void writerRadioButtonListener(ActionEvent event) {
        if (readerRadioButton.isSelected()) {
            readerRadioButton.setSelected(false);
        }
        permission = writerRadioButton.getText();
    }

    @FXML
    void sendPermissionRequestButtonListener(ActionEvent event) throws IOException {
        sheetName = dashboardController.getClickedSheetName();
        if (sheetName != null) {
            String requestPermissionBody = GSON_INSTANCE.toJson(new CreatePermissionRequestBody(sheetName, permission));
            MediaType mediaType = MediaType.get(JSON_MEDIA_TYPE);
            RequestBody requestBody = RequestBody.create(requestPermissionBody, mediaType);

            Request request = new Request.Builder()
                    .url(PERMISSION_REQUEST_ENDPOINT)
                    .post(requestBody)
                    .build();

            Response response = HttpClientUtil.HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                Stage stage = (Stage) sendPermissionRequestButton.getScene().getWindow();
                stage.close();
                AlertsHandler.HandleOkAlert("Permission request " + permission + " sent to the sheet owner!");
            } else {
                if (!(writerRadioButton.isSelected() || readerRadioButton.isSelected())) {
                    AlertsHandler.HandleErrorAlert("Request sheet permission",
                            "Please choose a permission first.");
                } else {
                    System.out.println(response.body().string());
                }
            }
        }
    }
}