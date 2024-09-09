package engine.testing;

import engine.api.Engine;
import engine.entity.range.Range;
import engine.impl.EngineImpl;

import java.util.Objects;

public class Testing {

    private static void verifyOriginalValueAssertion(String originalValue, String actualValue, String expectedValue) {
        if (!Objects.equals(actualValue, expectedValue)) {
            throw new AssertionError(originalValue + " equals " + actualValue + " and not " + expectedValue);
        }
    }

    private static void checkPlus(Engine engine) {
        String originalValue = "{PLUS,1,2}";
        engine.updateSheetCell(1, 1, originalValue);
        String actualValue = engine.findCellInSheet(1, 1, engine.getCurrentSheetVersion()).getEffectiveValue().getValue().toString();
        String expectedValue = Double.toString(3);

        verifyOriginalValueAssertion(originalValue, actualValue, expectedValue);
    }

    private static void checkRange(Engine engine) {
        String originalValue = "{SUM,effective-grades}";
        engine.updateSheetCell(1, 1, originalValue);
    }

    public static void main(String[] args) throws Exception {
        Engine engine = new EngineImpl();
        String filename = "C:\\Users\\asafl\\Downloads\\grades.xml";
        engine.loadFile(filename);
        checkRange(engine);

    }
}
