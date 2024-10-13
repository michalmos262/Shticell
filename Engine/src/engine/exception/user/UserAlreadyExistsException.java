package engine.exception.user;

public class UserAlreadyExistsException extends RuntimeException {
    private final String username;

    public UserAlreadyExistsException(String username) {
        this.username = username;
    }

    @Override
    public String getMessage() {
        return "Username " + username + " already exists";
    }
}
