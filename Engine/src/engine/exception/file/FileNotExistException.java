package engine.exception.file;

import java.io.FileNotFoundException;

public class FileNotExistException extends FileNotFoundException {
    private final String filePath;

    public FileNotExistException(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getMessage() {
        return "File does not exist in path " + filePath + ", make sure you put the right file path.";
    }
}