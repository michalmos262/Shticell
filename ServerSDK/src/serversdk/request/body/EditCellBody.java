package serversdk.request.body;

public class EditCellBody {
    private final String originalValue;

    public EditCellBody(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getOriginalValue() {
        return originalValue;
    }
}
