package client.component.dashboard.loadfile.task;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class LoadFileTask extends Task<Boolean> {
    private final long SLEEP_TIME = 1000;
    private final String fileName;
    private final Consumer<String> loadFileFunction;
    private final Consumer<String> onFinishFunction;

    public LoadFileTask(String fileName, Consumer<String> loadFileFunction, Consumer<String> onFinishFunction) {
        this.fileName = fileName;
        this.loadFileFunction = loadFileFunction;
        this.onFinishFunction = onFinishFunction;
    }

    @Override
    protected Boolean call() {
        try {
            updateMessage("Fetching file...");
            sleepForAWhile(SLEEP_TIME);
            updateProgress(0.1, 1);

            updateMessage("Checking file schema...");
            sleepForAWhile(SLEEP_TIME);
            updateProgress(0.3, 1);

            updateMessage("Loading file...");
            sleepForAWhile(SLEEP_TIME);
            loadFileFunction.accept(fileName);
            updateProgress(0.5, 1);

            updateMessage("Processing ranges...");
            sleepForAWhile(SLEEP_TIME);
            updateProgress(0.7, 1);

            updateMessage("Finalizing sheet upload...");
            sleepForAWhile(SLEEP_TIME);
            updateProgress(0.9, 1);

            updateMessage("Done!");
            updateProgress(1, 1);
            sleepForAWhile(SLEEP_TIME);

            Platform.runLater(() -> onFinishFunction.accept(fileName));

            return true;

        } finally {
            updateMessage("");
            updateProgress(0, 1);
        }
    }

    public static void sleepForAWhile(long sleepTime) {
        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {

            }
        }
    }
}