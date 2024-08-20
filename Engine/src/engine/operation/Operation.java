package engine.operation;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.dto.SheetDto;
import engine.expression.impl.EffectiveValueExpression;
import engine.operation.function.numeric.*;
import engine.operation.function.systemic.Ref;
import engine.operation.function.textual.Concat;
import engine.operation.function.textual.Sub;

import java.util.ArrayList;
import java.util.List;

public enum Operation {
    PLUS(2) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Plus(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    MINUS(2) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Minus(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    TIMES(2) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Times(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    DIVIDE(2) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Divide(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    MOD(2) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Mod(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    POW(2) {
       @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Pow(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    ABS(1) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Abs(expressions.getFirst()).invoke();
        }
    },
    CONCAT(2) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Concat(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    SUB(3) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Sub(expressions.getFirst(), expressions.get(1), expressions.getLast()).invoke();
        }
    },
    REF(1) {
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Ref(expressions.getFirst()).invoke(sheetDto, influencingCellPositions);
        }
    };
    
    private final int expressionsAmount;

    Operation(int expressionsAmount) {
        this.expressionsAmount = expressionsAmount;
    }
    
    public int getExpressionsAmount() {
        return expressionsAmount;
    }

    @Override
    public String toString() {
        return this.name();
    }

    abstract public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                                 ArrayList<EffectiveValueExpression> expressions) throws Exception;
}