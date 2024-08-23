package engine.exception.file;

public class InvalidFileTypeException extends IllegalArgumentException {
    private final String filePath;
    private final String expectedFileType;

    public InvalidFileTypeException(String filePath, String fileType) {
        this.filePath = filePath;
        this.expectedFileType = fileType;
    }

    @Override
    public String getMessage() {
        return "File " + filePath + " is not a valid " + expectedFileType + ".";
    }
}