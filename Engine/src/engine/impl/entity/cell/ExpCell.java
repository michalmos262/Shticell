package engine.impl.entity.cell;

public class ExpCell extends Cell {
    protected ExpCell(String originalValue) {
        super(originalValue);
    }

    @Override
    public void setEffectiveValueByOriginalValue() {

    }

    @Override
    protected <T> T parseOriginalValue() {
        return null;
    }
}
