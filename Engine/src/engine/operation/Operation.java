package engine.operation;

import engine.entity.cell.CellPositionInSheet;
import engine.entity.cell.EffectiveValue;
import engine.entity.sheet.api.ReadOnlySheet;
import engine.exception.operation.InvalidOperationNameException;
import engine.expression.impl.EffectiveValueExpression;
import engine.operation.function.arithmetical.*;
import engine.operation.function.booleanic.*;
import engine.operation.function.systemic.*;
import engine.operation.function.textual.*;

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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
            return new Sub(expressions.getFirst(), expressions.get(1), expressions.getLast()).invoke();
        }
    },
    PERCENT(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Retrieves the percent part from the whole number, which means part*whole/100. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",10,50} will retrieve 5.";
        }

        @Override
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions, ArrayList<EffectiveValueExpression> expressions) {
            return new Percent(expressions.getFirst(), expressions.getLast()).invoke();
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
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
            return new Ref(expressions.getFirst()).invoke(roSheet, influencingCellPositions);
        }
    },
    EQUAL(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Making a comparison on 2 values and returns TRUE if equal. Syntax: {" +
                    this + ",[" + valueArg + "],[" + valueArg + "]}. For example: {" + this + ",hi,hi} will retrieve TRUE.";
        }

        @Override
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
            return new Equal(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    NOT(1) {
        @Override
        public String getDocumentation() {
            return this + " -> Retrieves the opposite of a given boolean value. Syntax: {" + this + ",["
                    + booleanArg + "]}. For example: {" + this
                    + ",TRUE} will retrieve FALSE.";
        }

        @Override
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
            return new Not(expressions.getFirst()).invoke();
        }
    },
    OR(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Making a '||' on 2 boolean values. Syntax: {" +
                    this + ",[" + booleanArg + "],[" + booleanArg + "]}. For example: {" + this + ",TRUE,FALSE} will retrieve TRUE.";
        }

        @Override
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
            return new Or(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    AND(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Making a '&&' on 2 boolean values. Syntax: {" +
                    this + ",[" + booleanArg + "],[" + booleanArg + "]}. For example: {" + this + ",TRUE,FALSE} will retrieve FALSE.";
        }

        @Override
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
            return new And(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    BIGGER(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Retrieves TRUE if first value is bigger or equal to second value. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",5,2} will retrieve TRUE.";
        }

        @Override
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions, ArrayList<EffectiveValueExpression> expressions) {
            return new Bigger(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    LESS(2) {
        @Override
        public String getDocumentation() {
            return this + " -> Retrieves TRUE if first value is smaller or equal to second value. Syntax: {" +
                    this + ",[" + numberArg + "],[" + numberArg + "]}. For example: {" + this + ",5,2} will retrieve FALSE.";
        }

        @Override
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions, ArrayList<EffectiveValueExpression> expressions) {
            return new Less(expressions.getFirst(), expressions.getLast()).invoke();
        }
    },
    IF(3) {
        @Override
        public String getDocumentation() {
            return this + " -> Evaluates a boolean condition and retrieves the second value if TRUE, else the third value. Notice that both values should be the same type. Syntax: {" +
                    this + ",[" + booleanArg + "],[" + valueArg + "],[" + valueArg
                    + "]}. For example: {" + this + ",{BIGGER,5,2},TRUE,FALSE} will retrieve TRUE.";
        }

        @Override
        public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                            ArrayList<EffectiveValueExpression> expressions) {
            return new If(expressions.getFirst(), expressions.get(1), expressions.getLast()).invoke();
        }
    };
    
    private final int expressionsAmount;
    private static final String numberArg = "NUMBER";
    private static final String stringArg = "TEXT";
    private static final String booleanArg = "BOOLEAN";
    private static final String valueArg = "VALUE";
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
            throw new InvalidOperationNameException(operationName);
        }
    }

    abstract public String getDocumentation();

    abstract public EffectiveValue eval(ReadOnlySheet roSheet, List<CellPositionInSheet> influencingCellPositions,
                                        ArrayList<EffectiveValueExpression> expressions);
}