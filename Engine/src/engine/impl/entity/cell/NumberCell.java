package engine.impl.entity.cell;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberCell extends Cell {
    public NumberCell(String originalValue) {
        super(originalValue);
    }

    @Override
    public void setEffectiveValueByOriginalValue() {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        this.effectiveValue = formatter.format(parseOriginalValue());
    }

    @Override
    public Number parseOriginalValue() {
        return new BigDecimal(originalValue);
    }
}