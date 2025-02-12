package client.component.dashboard.loadfile;

import client.component.alert.AlertsHandler;
import client.component.dashboard.loadfile.task.LoadFileTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import okhttp3.*;
import serversdk.exception.ServerException;

import java.io.File;

import static client.resources.CommonResourcesPaths.*;
import static client.util.http.HttpClientUtil.HTTP_CLIENT;

public class LoadFileController {
    @FXML private Button loadFileButton;
    @FXML private Label loadingProcessLabel;
    @FXML private ProgressBar progressBar;

    private LoadFileModelUI modelUi;
    private LoadFileTask loadFileTask;

    @FXML
    private void initialize() {
        modelUi = new LoadFileModelUI(loadFileButton);
    }

    @FXML
    void loadFileButtonListener() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an " + SUPPORTED_FILE_TYPE.toUpperCase() + " file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(SUPPORTED_FILE_TYPE.toUpperCase() + " files", "*."
                        + SUPPORTED_FILE_TYPE)
        );
        File selectedFile = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }
        String selectedFileName = selectedFile.getAbsolutePath();
        modelUi.isFileLoadingProperty().set(true);

        loadFileTask = new LoadFileTask(
            selectedFileName,
            fileFunction -> {
                try {
                    RequestBody body = new MultipartBody.Builder()
                            .addFormDataPart("file", selectedFileName,
                                    RequestBody.create(selectedFile,
                                            MediaType.parse("text/plain")))
                            .build();

                    Request request = new Request.Builder()
                            .url(SHEET_ENDPOINT)
                            .post(body)
                            .build();

                    // Use try-with-resources to ensure response is closed
                    try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            ServerException.ErrorResponse errorResponse =
                                    GSON_INSTANCE.fromJson(response.body().string(),
                                            ServerException.ErrorResponse.class);
                            throw new RuntimeException(errorResponse.getMessage());
                        }

                        // Process response body as needed
                        // Example: String responseBody = response.body().string();
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage()); // Pass the exception
                    }
                } catch (Exception e) {
                    // Handle exceptions
                    throw new RuntimeException(e.getMessage());
                }
            },
            onFinish -> modelUi.isFileLoadingProperty().set(false));

        // Handle task failure
        loadFileTask.setOnFailed(eventFailed -> {
            Throwable exception = loadFileTask.getException(); // Get the exception that caused the failure
            if (exception != null) {
                Platform.runLater(() -> {
                    loadFileFailed(exception.getMessage()); // Call failure handler on the JavaFX thread
                    modelUi.isFileLoadingProperty().set(false);
                });
            }
        });

        modelUi.setTaskListener(loadFileTask, loadingProcessLabel, progressBar);
        new Thread(loadFileTask).start();
    }


    public void loadFileFailed(String errorMessage) {
        AlertsHandler.HandleErrorAlert("Error on loading file", errorMessage);
    }
}