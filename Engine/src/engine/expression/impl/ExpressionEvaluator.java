package engine.expression.impl;

import engine.entity.cell.*;
import engine.entity.dto.SheetDto;
import engine.exception.operation.OperationIllegalNumberOfArgumentsException;
import engine.operation.Operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionEvaluator {
    public static EffectiveValue evaluateArgument(SheetDto sheetDto, String argument, List<CellPositionInSheet> influencingCellPositions) throws Exception {
        EffectiveValue effectiveValue;

         if (argument.matches("-?\\d+(\\.\\d+)?")) {
                effectiveValue = new EffectiveValue(CellType.NUMERIC, Double.parseDouble(argument));
        }
        else if (argument.trim().equalsIgnoreCase("true") || argument.trim().equalsIgnoreCase("false")) {
            effectiveValue = new EffectiveValue(CellType.BOOLEAN, Boolean.parseBoolean(argument));
        }
        else if (argument.charAt(0) == '{' && argument.charAt(argument.length() - 1) == '}') {
            // ignore all spaces if exist
            argument = argument.replace(" ", "");
            // Remove the outer curly braces
            argument = argument.substring(1, argument.length() - 1);

            // Split the expression into function name and arguments
            String[] parts = splitExpression(argument);

            String functionName = parts[0];

            List<String> args = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));

            // Evaluate the function
            effectiveValue = evaluateFunction(sheetDto, functionName, args, influencingCellPositions);
        }
        else {
            effectiveValue = new EffectiveValue(CellType.STRING, argument);
         }

        return effectiveValue;
    }

    public static EffectiveValue evaluateFunction(SheetDto sheetDto, String operationName, List<String> args, List<CellPositionInSheet> influencingCellPositions) {
        try {
            Operation operation = Operation.getOperation(operationName);
            ArrayList<EffectiveValue> effectiveValues = new ArrayList<>();
            ArrayList<EffectiveValueExpression> effectiveValueExpressions = new ArrayList<>();
            int expectedExpressionsAmount = operation.getExpressionsAmount();

            if (args.size() != expectedExpressionsAmount) {
                throw new OperationIllegalNumberOfArgumentsException(operationName, expectedExpressionsAmount, args.size());
            }
            for (String arg : args) {
                EffectiveValue effectiveValue = evaluateArgument(sheetDto, arg, influencingCellPositions);
                effectiveValues.add(effectiveValue);
            }
            for (EffectiveValue ev : effectiveValues) {
                EffectiveValueExpression effectiveValueExpression = new EffectiveValueExpression(ev);
                effectiveValueExpressions.add(effectiveValueExpression);
            }

            return operation.eval(sheetDto, influencingCellPositions, effectiveValueExpressions);

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
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