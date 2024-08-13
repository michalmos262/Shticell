package engine.entity.cell;

import engine.entity.sheet.Sheet;

import static engine.expression.impl.ExpressionEvaluator.evaluateExpression;

public class ExpCell extends Cell {
    public ExpCell(String originalValue) {
        super(originalValue);
    }

    @Override
    public void setEffectiveValueByOriginalValue(Sheet sheet) {
        this.effectiveValue = evaluateExpression(originalValue, sheet).toString();
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