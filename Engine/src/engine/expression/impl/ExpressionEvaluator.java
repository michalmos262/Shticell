package engine.expression.impl;

import engine.entity.cell.*;
import engine.entity.dto.SheetDto;
import engine.entity.sheet.Sheet;
import engine.expression.api.Expression;
import engine.operation.Operation;
import engine.operation.function.numeric.*;
import engine.operation.function.systemic.Ref;
import engine.operation.function.textual.Concat;
import engine.operation.function.textual.Sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ExpressionEvaluator {
    public static void main(String[] args) throws Exception {
        Sheet sheet = new Sheet();
        List<CellPositionInSheet> influencingCellPositions = new LinkedList<>();

        // Test examples
        System.out.println(evaluateArgument(sheet, "{PLUS,2,3}", influencingCellPositions)); // Output: 5
        System.out.println(evaluateArgument(sheet, "{MINUS,{PLUS,4,5},{POW,2,3}}", influencingCellPositions)); // Output: 1
        System.out.println(evaluateArgument(sheet, "{CONCAT,Hello,World}", influencingCellPositions)); // Output: HelloWorld
        System.out.println(evaluateArgument(sheet, "{ABS,{MINUS,4,5}}", influencingCellPositions)); // Output: 1
        System.out.println(evaluateArgument(sheet, "{POW,2,3}", influencingCellPositions)); // Output: 8
        System.out.println(evaluateArgument(sheet, "{SUB,hello,2,3}", influencingCellPositions)); // Output: l
        System.out.println(evaluateArgument(sheet, "{MOD,4,2}", influencingCellPositions)); // Output: 0
    }

    public static EffectiveValue evaluateArgument(Sheet sheet, String argument, List<CellPositionInSheet> influencingCellPositions) throws Exception {
        EffectiveValue effectiveValue;

         if (argument.matches("-?\\d+(\\.\\d+)?")) {
                effectiveValue = new EffectiveValue(CellType.NUMERIC, Double.parseDouble(argument));
        }
        else if (argument.equalsIgnoreCase("true") || argument.equalsIgnoreCase("false")) {
            effectiveValue = new EffectiveValue(CellType.BOOLEAN, Boolean.parseBoolean(argument));
        }
        else if (argument.charAt(0) == '{' && argument.charAt(argument.length() - 1) == '}') {
            // Remove the outer curly braces
            argument = argument.substring(1, argument.length() - 1);

            // Split the expression into function name and arguments
            String[] parts = splitExpression(argument);

            String functionName = parts[0];

            List<String> args = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));

            // Evaluate the function
            effectiveValue = evaluateFunction(sheet, functionName, args, influencingCellPositions);
        }
        else {
            effectiveValue = new EffectiveValue(CellType.STRING, argument);
         }

        return effectiveValue;
    }

    public static EffectiveValue evaluateFunction(Sheet sheet, String operationName, List<String> args, List<CellPositionInSheet> influencingCellPositions) throws Exception {
        Operation operation = Operation.valueOf(operationName);
        EffectiveValue effectiveValue1, effectiveValue2, effectiveValue3;
        Expression exp1, exp2, exp3;
        EffectiveValue returnedEffectiveValue = null;
        switch (operation) {
            case PLUS, MINUS, TIMES, DIVIDE, MOD, POW:
                if (args.size() != 2) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = evaluateArgument(sheet, args.getFirst(), influencingCellPositions);
                effectiveValue2 = evaluateArgument(sheet, args.get(1), influencingCellPositions);
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
                effectiveValue1 = evaluateArgument(sheet, args.getFirst(), influencingCellPositions);
                exp1 = new EffectiveValueExpression(effectiveValue1);
                returnedEffectiveValue = new Abs(exp1).invoke();
                break;
            case CONCAT:
                if (args.size() != 2) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = evaluateArgument(sheet, args.getFirst(), influencingCellPositions);
                effectiveValue2 = evaluateArgument(sheet, args.getLast(), influencingCellPositions);
                exp1 = new EffectiveValueExpression(effectiveValue1);
                exp2 = new EffectiveValueExpression(effectiveValue2);
                returnedEffectiveValue = new Concat(exp1, exp2).invoke();
                break;
            case SUB:
                if (args.size() != 3) {
                    throw new IllegalArgumentException("Wrong number of arguments to function: " + operationName);
                }
                effectiveValue1 = evaluateArgument(sheet, args.getFirst(), influencingCellPositions);
                effectiveValue2 = evaluateArgument(sheet, args.get(1), influencingCellPositions);
                effectiveValue3 = evaluateArgument(sheet, args.get(2), influencingCellPositions);
                exp1 = new EffectiveValueExpression(effectiveValue1);
                exp2 = new EffectiveValueExpression(effectiveValue2);
                exp3 = new EffectiveValueExpression(effectiveValue3);
                returnedEffectiveValue = new Sub(exp1, exp2, exp3).invoke();
                break;
            case REF:
                if (args.size() != 1) {
                    throw new IllegalArgumentException("Wrong number of arguments to function " + operationName);
                }
                effectiveValue1 = evaluateArgument(sheet, args.getFirst(), influencingCellPositions);
                exp1 = new EffectiveValueExpression(effectiveValue1);
                returnedEffectiveValue = new Ref(exp1).invoke(new SheetDto(sheet), influencingCellPositions);
                break;
            default:
                throw new IllegalArgumentException("Unknown function: " + operationName);
        }
        return returnedEffectiveValue;
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