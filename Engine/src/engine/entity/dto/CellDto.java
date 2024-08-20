package engine.entity.dto;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;

import java.util.List;

public class CellDto {
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final EffectiveValue effectiveValueForDisplay;
    private final List<CellPositionInSheet> influencedBy;
    private final List<CellPositionInSheet> influences;

    public CellDto(String originalValue, EffectiveValue effectiveValue, EffectiveValue effectiveValueForDisplay,
                   List<CellPositionInSheet> influencedBy, List<CellPositionInSheet> influences) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.effectiveValueForDisplay = effectiveValueForDisplay;
        this.influencedBy = influencedBy;
        this.influences = influences;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public EffectiveValue getEffectiveValueForDisplay() {
        return effectiveValueForDisplay;
    }

    public List<CellPositionInSheet> getInfluencedBy() {
        return influencedBy;
    }

    public List<CellPositionInSheet> getInfluences() {
        return influences;
    }
}