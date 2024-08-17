package engine.expression.impl;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;

import java.util.List;

public class ValueAndPositions {
    private EffectiveValue effectiveValue;
    private List<CellPositionInSheet> influencingCellPositions;

    public ValueAndPositions(EffectiveValue effectiveValue, List<CellPositionInSheet> influencingCellPositions) {
        this.effectiveValue = effectiveValue;
        this.influencingCellPositions = influencingCellPositions;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public List<CellPositionInSheet> getInfluencingCellPositions() {
        return influencingCellPositions;
    }
}
