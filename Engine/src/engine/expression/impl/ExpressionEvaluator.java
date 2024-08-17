package engine.expression.impl;

import engine.entity.cell.*;
import engine.entity.sheet.Sheet;
import engine.entity.sheet.SheetDto;
import engine.expression.api.Expression;
import engine.operation.Operation;
import engine.operation.function.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ExpressionEvaluator {
    public static void main(String[] args) {
        Sheet sheet = new Sheet();
        List<CellPositionInSheet> influencingCellPositions = new LinkedList<>();

        // Test examples
        System.out.println(evaluateExpression(sheet, "{PLUS,2,3}", influencingCellPositions).getEffectiveValue()); // Output: 5
        System.out.println(evaluateExpression(sheet, "{MINUS,{PLUS,4,5},{POW,2,3}}", influencingCellPositions).getEffectiveValue()); // Output: 1
        System.out.println(evaluateExpression(sheet, "{CONCAT,Hello,World}", influencingCellPositions).getEffectiveValue()); // Output: HelloWorld
        System.out.println(evaluateExpression(sheet, "{ABS,{MINUS,4,5}}", influencingCellPositions).getEffectiveValue()); // Output: 1
        System.out.println(evaluateExpression(sheet, "{POW,2,3}", influencingCellPositions).getEffectiveValue()); // Output: 8
        System.out.println(evaluateExpression(sheet, "{SUB,hello,2,3}", influencingCellPositions).getEffectiveValue()); // Output: l
        System.out.println(evaluateExpression(sheet, "{MOD,4,2}", influencingCellPositions).getEffectiveValue()); // Output: 0
    }

    public static ValueAndPositions evaluateExpression(Sheet sheet, String expression, List<CellPositionInSheet> influencingCellPositions) {
        // Remove the outer curly braces
        expression = expression.substring(1, expression.length() - 1);

        // Split the expression into function name and arguments
        String[] parts = splitExpression(expression);

        String functionName = parts[0];

        List<String> args = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));

        // Evaluate the function
        return evaluateFunction(sheet, functionName, args, influencingCellPositions);
    }

    public static ValueAndPositions evaluateFunction(Sheet sheet, String operationName, List<String> args, List<CellPositionInSheet> influencingCellPositions) {
        Operation operation = Operation.valueOf(operationName);
        EffectiveValue effectiveValue1, effectiveValue2, effectiveValue3;
        Expression exp1, exp2, exp3;
        SheetDto sheetDto = new SheetDto(sheet);
        EffectiveValue returnedEffectiveValue = null;
        switch (operation) {
            case PLUS, MINUS, TIMES, DIVIDE, MOD, POW:
                if (args.size() != 2) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.NUMERIC, evaluateArgument(sheet, args.getFirst(), influencingCellPositions));
                effectiveValue2 = new EffectiveValue(CellType.NUMERIC, evaluateArgument(sheet, args.get(1), influencingCellPositions));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                exp2 = new EffectiveValueExpression(effectiveValue2);

                if (operation == Operation.PLUS) {
                    returnedEffectiveValue = new Plus(exp1, exp2).invoke();
                }
                else if (operation == Operation.MINUS) {
                    returnedEffectiveValue = new Minus(exp1, exp2).invoke();
                }
                else if (operation == Operation.TIMES) {
                    returnedEffectiveValue = new Times(exp1, exp2).invoke();
                }
                else if (operation == Operation.DIVIDE) {
                    returnedEffectiveValue = new Divide(exp1, exp2).invoke();
                }
                else if (operation == Operation.MOD) {
                    returnedEffectiveValue = new Mod(exp1, exp2).invoke();
                }
                else if (operation == Operation.POW) {
                    returnedEffectiveValue = new Pow(exp1, exp2).invoke();
                }
                break;
            case ABS:
                if (args.size() != 1) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.NUMERIC, evaluateArgument(sheet, args.getFirst(), influencingCellPositions));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                returnedEffectiveValue = new Abs(exp1).invoke();
                break;
            case CONCAT:
                if (args.size() != 2) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.STRING, evaluateArgument(sheet, args.getFirst(), influencingCellPositions));
                effectiveValue2 = new EffectiveValue(CellType.STRING, evaluateArgument(sheet, args.getLast(), influencingCellPositions));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                exp2 = new EffectiveValueExpression(effectiveValue2);
                returnedEffectiveValue = new Concat(exp1, exp2).invoke();
                break;
            case SUB:
                if (args.size() != 3) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.STRING, evaluateArgument(sheet, args.getFirst(), influencingCellPositions));
                effectiveValue2 = new EffectiveValue(CellType.STRING, evaluateArgument(sheet, args.get(1), influencingCellPositions));
                effectiveValue3 = new EffectiveValue(CellType.STRING, evaluateArgument(sheet, args.get(2), influencingCellPositions));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                exp2 = new EffectiveValueExpression(effectiveValue2);
                exp3 = new EffectiveValueExpression(effectiveValue3);
                returnedEffectiveValue = new Sub(exp1, exp2, exp3).invoke();
                break;
            case REF:
                if (args.size() != 1) {
                    throw new IllegalArgumentException("Wrong number of arguments to function " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.STRING, evaluateArgument(sheet, args.getFirst(), influencingCellPositions));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                returnedEffectiveValue = new Ref(exp1).invoke(sheetDto, influencingCellPositions);
                break;
            default:
                throw new IllegalArgumentException("Unknown function: " + operationName);
        }
        return new ValueAndPositions(returnedEffectiveValue, influencingCellPositions);
    }

    private static Object evaluateArgument(Sheet sheet, String arg, List<CellPositionInSheet> influencingCellPositions) {
        if (arg.startsWith("{")) {
            // If the argument is an expression, recursively evaluate it
            return evaluateExpression(sheet, arg, influencingCellPositions);
        } else if (arg.matches("-?\\d+(\\.\\d+)?")) {
            // If the argument is a number, return it as a Double
            return Double.parseDouble(arg);
        } else {
            // Otherwise, return it as a string (e.g., for CONCAT or SUB or REF)
            return arg;
        }
    }

    private static String[] splitExpression(String expression) {
        List<String> parts = new ArrayList<>();
        int bracketCount = 0;
        StringBuilder currentPart = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (c == ',' && bracketCount == 0) {
                parts.add(currentPart.toString());
                currentPart.setLength(0);
            } else {
                if (c == '{') bracketCount++;
                if (c == '}') bracketCount--;
                currentPart.append(c);
            }
        }
        parts.add(currentPart.toString());
        return parts.toArray(new String[0]);
    }
}