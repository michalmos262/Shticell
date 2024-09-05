package engine.exception.cell;

public class EffectiveValueCastingException extends ClassCastException {
    private final Class<?> actualType;
    private final Class<?> expectedType;

    public EffectiveValueCastingException(Class<?> actualType, Class<?> expectedType) {
        this.actualType = actualType;
        this.expectedType = expectedType;
    }

    @Override
    public String getMessage() {
        return "Could not cast value type " + actualType + " to " + expectedType;
    }
}