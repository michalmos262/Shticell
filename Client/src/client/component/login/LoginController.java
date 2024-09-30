package client.component.login;

import client.component.mainapp.MainAppController;
import client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static client.resources.CommonResourcesPaths.LOGIN_PAGE;

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
    void loginButtonListener(ActionEvent event) {
        String userName = usernameTextField.getText();
        if (userName.isEmpty()) {
            modelUi.errorMessageProperty().set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                        .parse(LOGIN_PAGE)
                        .newBuilder()
                        .addQueryParameter("username", userName)
                        .build()
                        .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        modelUi.errorMessageProperty().set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            modelUi.errorMessageProperty().set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                            mainAppController.updateUserName(userName);
                            mainAppController.switchToDashboardPage();
                    });
                }
            }
        });
    }
}