package engine.exception.sheet;

public class SheetNameAlreadyExistsException extends RuntimeException {
    private final String name;

    public SheetNameAlreadyExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Sheet with name " + name + " already exists";
    }


}
