package engine.entity.cell;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import static engine.expression.impl.ExpressionEvaluator.evaluateExpression;

public class Cell implements Cloneable {
    private String originalValue;
    private EffectiveValue effectiveValue;
    private final List<Cell> dependsOn;
    private final List<Cell> influencingOn;
    private int lastUpdatedInVersion;

    public Cell(String originalValue, int lastUpdatedInVersion) {
        this.originalValue = originalValue;
        setEffectiveValueByOriginalValue();
        dependsOn = new ArrayList<>();
        influencingOn = new ArrayList<>();
        this.lastUpdatedInVersion = lastUpdatedInVersion;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public List<Cell> getDependsOn() {
        return dependsOn;
    }

    public List<Cell> getInfluencingOn() {
        return influencingOn;
    }

    public int getLastUpdatedInVersion() {
        return lastUpdatedInVersion;
    }

    public void setEffectiveValueByOriginalValue() {
        if (originalValue.matches("-?\\d+(\\.\\d+)?")) {
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            effectiveValue = new EffectiveValue(CellType.NUMERIC, formatter.format(new BigDecimal(originalValue)));
        }
        else if (originalValue.equalsIgnoreCase("true") || originalValue.equalsIgnoreCase("false")) {
            effectiveValue = new EffectiveValue(CellType.BOOLEAN, originalValue.toUpperCase());
        }
        else if (originalValue.charAt(0) == '{' && originalValue.charAt(originalValue.length() - 1) == '}') {
            effectiveValue = evaluateExpression(originalValue);
        }
        else {
            effectiveValue = new EffectiveValue(CellType.STRING, originalValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return Objects.equals(getOriginalValue(), cell.getOriginalValue()) && Objects.equals(getEffectiveValue(), cell.getEffectiveValue()) && Objects.equals(getDependsOn(), cell.getDependsOn()) && Objects.equals(getInfluencingOn(), cell.getInfluencingOn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOriginalValue(), getEffectiveValue(), getDependsOn(), getInfluencingOn());
    }

    @Override
    public Cell clone() {
        try {
            Cell cloned = (Cell) super.clone();
            cloned.originalValue = originalValue;
            cloned.effectiveValue = effectiveValue;
            dependsOn.forEach((cell) -> cloned.dependsOn.add(cell.clone()));
            influencingOn.forEach((cell) -> cloned.influencingOn.add(cell.clone()));
            cloned.lastUpdatedInVersion = lastUpdatedInVersion;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}