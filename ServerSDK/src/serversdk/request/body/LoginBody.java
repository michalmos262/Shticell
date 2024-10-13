package serversdk.request.body;

public class LoginBody {
    private final String username;

    public LoginBody(String username) {
        this.username = username;
    }

    public String getUsername() {
            return username;
        }
}
