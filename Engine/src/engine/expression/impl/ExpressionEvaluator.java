package engine.expression.impl;

import engine.entity.cell.*;
import engine.expression.api.Expression;
import engine.operation.Operation;
import engine.operation.function.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionEvaluator {
    public static void main(String[] args) {
        // Test examples
        System.out.println(evaluateExpression("{PLUS,2,3}")); // Output: 5
        System.out.println(evaluateExpression("{MINUS,{PLUS,4,5},{POW,2,3}}")); // Output: 1
        System.out.println(evaluateExpression("{CONCAT,Hello,World}")); // Output: HelloWorld
        System.out.println(evaluateExpression("{ABS,{MINUS,4,5}}")); // Output: 1
        System.out.println(evaluateExpression("{POW,2,3}")); // Output: 8
        System.out.println(evaluateExpression("{SUB,hello,2,3}")); // Output: 8
        System.out.println(evaluateExpression("{MOD,4, 2}")); // Output: 0
    }

    public static EffectiveValue evaluateExpression(String expression) {
        // Remove the outer curly braces
        expression = expression.substring(1, expression.length() - 1);

        // Split the expression into function name and arguments
        String[] parts = splitExpression(expression);

        String functionName = parts[0];

        List<String> args = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));

        // Evaluate the function
        return evaluateFunction(functionName, args);
    }

    public static EffectiveValue evaluateFunction(String operationName, List<String> args) {
        Operation operation = Operation.valueOf(operationName);
        EffectiveValue effectiveValue1, effectiveValue2, effectiveValue3;
        Expression exp1, exp2, exp3;
        switch (operation) {
            case PLUS, MINUS, TIMES, DIVIDE, MOD, POW:
                if (args.size() != 2) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.NUMERIC, evaluateArgument(args.getFirst()));
                effectiveValue2 = new EffectiveValue(CellType.NUMERIC, evaluateArgument(args.get(1)));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                exp2 = new EffectiveValueExpression(effectiveValue2);

                if (operation == Operation.PLUS) {
                    return new Plus(exp1, exp2).invoke();
                }
                if (operation == Operation.MINUS) {
                    return new Minus(exp1, exp2).invoke();
                }
                if (operation == Operation.TIMES) {
                    return new Times(exp1, exp2).invoke();
                }
                if (operation == Operation.DIVIDE) {
                    return new Divide(exp1, exp2).invoke();
                }
                if (operation == Operation.MOD) {
                    return new Mod(exp1, exp2).invoke();
                }
                return new Pow(exp1, exp2).invoke();
            case ABS:
                if (args.size() != 1) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.NUMERIC, evaluateArgument(args.getFirst()));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                return new Abs(exp1).invoke();
            case CONCAT:
                if (args.size() != 2) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.STRING, evaluateArgument(args.getFirst()));
                effectiveValue2 = new EffectiveValue(CellType.STRING, evaluateArgument(args.getLast()));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                exp2 = new EffectiveValueExpression(effectiveValue2);
                return new Concat(exp1, exp2).invoke();
            case SUB:
                if (args.size() != 3) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = new EffectiveValue(CellType.STRING, evaluateArgument(args.getFirst()));
                effectiveValue2 = new EffectiveValue(CellType.STRING, evaluateArgument(args.get(1)));
                effectiveValue3 = new EffectiveValue(CellType.STRING, evaluateArgument(args.get(2)));
                exp1 = new EffectiveValueExpression(effectiveValue1);
                exp2 = new EffectiveValueExpression(effectiveValue2);
                exp3 = new EffectiveValueExpression(effectiveValue3);
                return new Sub(exp1, exp2, exp3).invoke();
            case REF:
//                if (args.size() != 1) {
//                    throw new IllegalArgumentException("Wrong number of arguments to function " + operationName);
//                }
//                CellPositionInSheet cellPositionInSheet = PositionFactory.createPosition(args.getFirst());
//                Cell cell = sheet.getVersion2cellTable()[cellPositionInSheet.getRow()][cellPositionInSheet.getColumn()].get(sheet.getCurrVersion());
//                Expression cellExp = new CellExpression(cell);
//                return new Ref(cellExp);
                return null; // TODO: REF
            default:
                throw new IllegalArgumentException("Unknown function: " + operationName);
        }
    }

    private static Object evaluateArgument(String arg) {
        if (arg.startsWith("{")) {
            // If the argument is an expression, recursively evaluate it
            return evaluateExpression(arg);
        } else if (arg.matches("-?\\d+(\\.\\d+)?")) {
            // If the argument is a number, return it as an Integer or Double
            return arg.contains(".") ? Double.parseDouble(arg) : Integer.parseInt(arg);
        } else {
            // Otherwise, return it as a string (e.g., for CONCAT or SUB)
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