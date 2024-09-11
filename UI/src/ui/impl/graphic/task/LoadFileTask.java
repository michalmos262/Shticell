package ui.impl.graphic.task;

import javafx.concurrent.Task;

public class LoadFileTask extends Task<Boolean> {
    private final String fileName;

    public LoadFileTask(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected Boolean call() throws Exception {

        return true;
    }
}
