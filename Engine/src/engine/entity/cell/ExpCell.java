package engine.entity.cell;

import static engine.expression.impl.ExpressionEvaluator.evaluateExpression;

public class ExpCell extends Cell {
    public ExpCell(String originalValue) {
        super(originalValue);
    }

    @Override
    public void setEffectiveValueByOriginalValue() {
        this.effectiveValue = evaluateExpression(originalValue).toString();
    }

    @Override
    protected Object parseOriginalValue() {
        try {
        return Double.parseDouble(effectiveValue);
        } catch (NumberFormatException e) {
            return effectiveValue;
        }
    }
}