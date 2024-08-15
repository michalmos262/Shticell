package engine.expression.impl;

import engine.entity.cell.Cell;
import engine.entity.cell.CellPositionInSheet;
import engine.entity.sheet.Sheet;
import engine.expression.api.Expression;
import engine.operation.Operation;
import engine.operation.function.*;

import java.util.ArrayList;
import java.util.List;

public class ExpressionEvaluator {
//    public static void main(String[] args) {
//        // Test examples
//        System.out.println(evaluateExpression("{PLUS,2,3}")); // Output: 5
//        System.out.println(evaluateExpression("{MINUS,{PLUS,4,5},{POW,2,3}}")); // Output: 1
//        System.out.println(evaluateExpression("{CONCAT,Hello,World}")); // Output: HelloWorld
//        System.out.println(evaluateExpression("{ABS,{MINUS,4,5}}")); // Output: 1
//        System.out.println(evaluateExpression("{POW,2,3}")); // Output: 8
//        System.out.println(evaluateExpression("{SUB,hello,2,3}")); // Output: 8
//        System.out.println(evaluateExpression("{MOD,4, 2}")); // Output: 0
//    }

    public static Object evaluateExpression(String expression, Sheet sheet) {
        // Remove the outer curly braces
        expression = expression.substring(1, expression.length() - 1);

        // Split the expression into function name and arguments
        String[] parts = splitExpression(expression);

        String functionName = parts[0];
        List<String> args = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            args.add(parts[i]);
        }

        // Evaluate the function
        return evaluateFunction(functionName, args, sheet);
    }

    public static Object evaluateFunction(String operationName, List<String> args, Sheet sheet) {
        Operation operation = Operation.valueOf(operationName);
        switch (operation) {
            case PLUS, MINUS, TIMES, DIVIDE, MOD, POW:
                if (args.size() != 2) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                // Evaluate arguments, allowing for nested expressions
                double doubleArg1 = Double.parseDouble(evaluateArgument(args.getFirst(), sheet).toString());
                double doubleArg2 = Double.parseDouble(evaluateArgument(args.get(1), sheet).toString());
                Expression<Double> numberExp1 = new NumberExpression<>(doubleArg1);
                Expression<Double> numberExp2 = new NumberExpression<>(doubleArg2);

                if (operation == Operation.PLUS) {
                    return new Plus(numberExp1, numberExp2).invoke();
                }
                if (operation == Operation.MINUS) {
                    return new Minus(numberExp1, numberExp2).invoke();
                }
                if (operation == Operation.TIMES) {
                    return new Times(numberExp1, numberExp2).invoke();
                }
                if (operation == Operation.DIVIDE) {
                    return new Divide(numberExp1, numberExp2).invoke();
                }
                if (operation == Operation.MOD) {
                    return new Mod(numberExp1, numberExp2).invoke();
                }
                return new Pow(numberExp1, numberExp2).invoke();
            case ABS:
                if (args.size() != 1) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                double argForAbs = Double.parseDouble(evaluateArgument(args.getFirst(), sheet).toString());
                Expression<Double> numberExpForAbs = new NumberExpression<>(argForAbs);
                return new Abs(numberExpForAbs).invoke();
            case CONCAT:
                if (args.size() != 2) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                String argForConcat1 = evaluateArgument(args.getFirst(), sheet).toString();
                String argForConcat2 = evaluateArgument(args.get(1), sheet).toString();
                Expression<String> strExp1 = new StringExpression(argForConcat1);
                Expression<String> strExp2 = new StringExpression(argForConcat2);
                return new Concat(strExp1, strExp2).invoke();
            case SUB:
                if (args.size() != 3) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                Expression<String> strExp = new StringExpression(args.getFirst());
                Expression<Integer> startIndex = new NumberExpression<>(Integer.parseInt(args.get(1)));
                Expression<Integer> endIndex = new NumberExpression<>(Integer.parseInt(args.get(2)));
                return new Sub(strExp, startIndex, endIndex).invoke();
            case REF:
//                if (args.size() != 1) {
//                    throw new IllegalArgumentException("Wrong number of arguments to function " + operationName);
//                }
//                CellPositionInSheet cellPositionInSheet = new CellPositionInSheet(args.getFirst());
//                Cell cell = sheet.getVersion2cellTable()[cellPositionInSheet.getRow()][cellPositionInSheet.getColumn()].get(sheet.getCurrVersion());
//                Expression<Cell> cellExp = new CellExpression(cell);
//                return new Ref(cellExp);
            default:
                throw new IllegalArgumentException("Unknown function: " + operationName);
        }
    }

    private static Object evaluateArgument(String arg, Sheet sheet) {
        if (arg.startsWith("{")) {
            // If the argument is an expression, recursively evaluate it
            return evaluateExpression(arg, sheet);
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