package engine.operation;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.dto.SheetDto;
import engine.exception.operation.InvalidOperationName;
import engine.expression.impl.EffectiveValueExpression;
import engine.operation.function.arithmetical.*;
import engine.operation.function.systemic.Ref;
import engine.operation.function.textual.Concat;
import engine.operation.function.textual.Sub;

import java.util.ArrayList;
import java.util.List;

public enum Operation {
    PLUS(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Makes an arithmetic '+' between 2 arguments. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",1,2} will retrieve 3.";
        }

        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Plus(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    MINUS(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Makes an arithmetic '-' between 2 arguments. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",3,2} will retrieve 1.";
        }

        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Minus(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    TIMES(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Makes an arithmetic '*' between 2 arguments. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",3,2} will retrieve 6.";
        }
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Times(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    DIVIDE(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Makes an arithmetic '/' between 2 arguments. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",6,4} will retrieve 1.";
        }

        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Divide(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    MOD(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Makes an arithmetic '%' between 2 arguments. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",6,4} will retrieve 2.";
        }

        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Mod(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    POW(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Makes an arithmetic '^' between 2 arguments. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",3,2} will retrieve 9.";
        }

       @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Pow(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    ABS(1) {
        @Override
        public String getDocumentation() {
            return this + " -> Retrieves the absolute number of an argument. Syntax: {" +
                    this + ",[" + numberArg + "]}. For example: {" + this + ",-3} will retrieve 3.";
        }

        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Abs(expressions.getFirst()).invoke();
        }
    },
    CONCAT(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Retrieves a concat of 2 texts. Syntax: {" +
                    this + ",[" + stringArg + "],[" + stringArg + "]}. For example: {" + this + ",hello,World} will retrieve 'helloWorld'.";
        }
        
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Concat(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    SUB(3) {
        @Override
        public String getDocumentation() {
            return this + " -> Retrieves a cut of a given text from a given start index to a given end index. Syntax: {" +
                    this + ",[" + stringArg + "],[FROM-" + numberArg + "],[TO-" + numberArg
                    + "]}. For example: {" + this + ",hello,1,4} will retrieve 'ello'.";
        }

        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Sub(expressions.getFirst(), expressions.get(1), expressions.getLast()).invoke();
        }
    },
    REF(1) {
        @Override
        public String getDocumentation() {
            return this + " -> Retrieves the value of another cell in the sheet. Syntax: {" + this + ",["
                    + positionArg + "]}. For example: {" + this
                    + ",A3} will retrieve the effective value of position A3.";
        }
        @Override
        public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) throws Exception {
            return new Ref(expressions.getFirst()).invoke(sheetDto, influencingCellPositions);
        }
    };
    
    private final int expressionsAmount;
    private static final String numberArg = "NUMBER";
    private static final String stringArg = "TEXT";
    private static final String booleanArg = "BOOLEAN";
    private static final String positionArg = "CELL-POSITION";

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

    public static Operation getOperation(String operationName) {
        try {
            return valueOf(operationName);
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationName(operationName);
        }
    }

    abstract public EffectiveValue eval(SheetDto sheetDto, List<CellPositionInSheet> influencingCellPositions,
                                 ArrayList<EffectiveValueExpression> expressions) throws Exception;

    abstract public String getDocumentation();
}