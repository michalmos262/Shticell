package dto;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;

import java.util.Collections;
import java.util.Set;

public class CellDto {
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final EffectiveValue effectiveValueForDisplay;
    private final Set<CellPositionInSheet> influencedBy;
    private final Set<CellPositionInSheet> influences;
    private final int lastUpdatedInVersion;

    public CellDto(String originalValue, EffectiveValue effectiveValue, EffectiveValue effectiveValueForDisplay,
                   Set<CellPositionInSheet> influencedBy, Set<CellPositionInSheet> influences, int lastUpdatedInVersion) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.effectiveValueForDisplay = effectiveValueForDisplay;
        this.influencedBy = influencedBy;
        this.influences = influences;
        this.lastUpdatedInVersion = lastUpdatedInVersion;
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
        return Collections.unmodifiableSet(influencedBy);
    }

    public Set<CellPositionInSheet> getInfluences() {
        return Collections.unmodifiableSet(influences);
    }

    public int getLastUpdatedInVersion() {
        return lastUpdatedInVersion;
    }
}