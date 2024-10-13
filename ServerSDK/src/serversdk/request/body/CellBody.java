package serversdk.request.body;

public class CellBody {
    private final String originalValue;

    public CellBody(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getOriginalValue() {
        return originalValue;
    }
}
