package engine.entity.dto;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;

import java.util.Set;

public class CellDto {
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final EffectiveValue effectiveValueForDisplay;
    private final Set<CellPositionInSheet> influencedBy;
    private final Set<CellPositionInSheet> influences;

    public CellDto(String originalValue, EffectiveValue effectiveValue, EffectiveValue effectiveValueForDisplay,
                   Set<CellPositionInSheet> influencedBy, Set<CellPositionInSheet> influences) {
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

    public Set<CellPositionInSheet> getInfluencedBy() {
        return influencedBy;
    }

    public Set<CellPositionInSheet> getInfluences() {
        return influences;
    }
}