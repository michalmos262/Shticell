package dto.cell;

import java.util.Collections;
import java.util.Set;

public class CellDto {
    private final String originalValue;
    private final EffectiveValueDto effectiveValue;
    private final EffectiveValueDto effectiveValueForDisplay;
    private final Set<CellPositionDto> influencedBy;
    private final Set<CellPositionDto> influences;
    private final int lastUpdatedInVersion;

    public CellDto(String originalValue, EffectiveValueDto effectiveValue, EffectiveValueDto effectiveValueForDisplay,
                   Set<CellPositionDto> influencedBy, Set<CellPositionDto> influences, int lastUpdatedInVersion) {
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

    public EffectiveValueDto getEffectiveValue() {
        return effectiveValue;
    }

    public EffectiveValueDto getEffectiveValueForDisplay() {
        return effectiveValueForDisplay;
    }

    public Set<CellPositionDto> getInfluencedBy() {
        return Collections.unmodifiableSet(influencedBy);
    }

    public Set<CellPositionDto> getInfluences() {
        return Collections.unmodifiableSet(influences);
    }

    public int getLastUpdatedInVersion() {
        return lastUpdatedInVersion;
    }
}