package client.component.login;

import client.component.mainapp.MainAppController;
import client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import serversdk.exception.ServerException;
import serversdk.request.body.LoginBody;

import java.io.IOException;

import static client.resources.CommonResourcesPaths.*;

public class LoginController {

    @FXML private Button loginButton;
    @FXML private TextField usernameTextField;
    @FXML private Label errorMessageLabel;

    private LoginModelUI modelUi;
    private MainAppController mainAppController;

    @FXML
    public void initialize() {
        modelUi = new LoginModelUI(errorMessageLabel);
    }

    public void setMainAppController(MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    @FXML
    void loginButtonListener() {
        String username = usernameTextField.getText();

        if (username.isEmpty()) {
            modelUi.errorMessageProperty().set("User name is empty. You can't login with empty user name");
            return;
        }

        // create the request body
        String loginBodyJson = GSON_INSTANCE.toJson(new LoginBody(username));
        MediaType mediaType = MediaType.get(JSON_MEDIA_TYPE);
        RequestBody requestBody = RequestBody.create(loginBodyJson, mediaType);

        HttpClientUtil.runAsyncPost(LOGIN_PAGE, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> modelUi.errorMessageProperty().set(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        mainAppController.loggedIn(username);
                        mainAppController.switchToDashboardPage();
                    });
                } else {
                    String responseBody = response.body() != null ? response.body().string() : GENERAL_ERROR_JSON;
                    Platform.runLater(() -> {
                        ServerException.ErrorResponse errorResponse = GSON_INSTANCE.fromJson(responseBody, ServerException.ErrorResponse.class);
                        modelUi.errorMessageProperty().set(errorResponse.getMessage());
                    });
                }
            }
        });
    }
}