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
    private final String updatedByName;

    public CellDto(String originalValue, EffectiveValueDto effectiveValue, EffectiveValueDto effectiveValueForDisplay,
                   Set<CellPositionDto> influencedBy, Set<CellPositionDto> influences, int lastUpdatedInVersion,
                   String updatedByName) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.effectiveValueForDisplay = effectiveValueForDisplay;
        this.influencedBy = influencedBy;
        this.influences = influences;
        this.lastUpdatedInVersion = lastUpdatedInVersion;
        this.updatedByName = updatedByName;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getUpdatedByName() {
        return updatedByName;
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