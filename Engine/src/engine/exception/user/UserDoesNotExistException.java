package engine.exception.user;

public class UserDoesNotExistException extends RuntimeException {
    private final String username;

    public UserDoesNotExistException(String username) {
        this.username = username;
    }

    @Override
    public String getMessage() {
        return "Username " + username + " does not exist";
    }
}