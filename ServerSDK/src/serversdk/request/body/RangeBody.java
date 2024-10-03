package serversdk.request.body;

public class RangeBody {
    private final String name;
    private final String fromPosition;
    private final String toPosition;

    public RangeBody(String name, String fromPosition, String toPosition) {
        this.name = name;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

    public String getName() {
            return name;
        }

    public String getFromPosition() {
        return fromPosition;
    }

    public String getToPosition() {
        return toPosition;
    }
}
