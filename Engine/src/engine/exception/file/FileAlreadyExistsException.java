package engine.exception.file;

public class FileAlreadyExistsException extends java.nio.file.FileAlreadyExistsException {

    public FileAlreadyExistsException(String filePath) {
        super(filePath);
    }

    @Override
    public String getMessage() {
        return "File path " + getFile() + " already exists.";
    }
}