package engine.exception.operation;

public class OperationIllegalNumberOfArgumentsException extends IllegalArgumentException {
    private final String operationName;
    private final int expectedNumberOfArguments;
    private final int actualNumberOfArguments;

    public OperationIllegalNumberOfArgumentsException(String operationName, int expectedNumberOfArguments, int actualNumberOfArguments) {
        this.operationName = operationName;
        this.expectedNumberOfArguments = expectedNumberOfArguments;
        this.actualNumberOfArguments = actualNumberOfArguments;
    }

    @Override
    public String getMessage() {
        return "Operation " + operationName + " requires " + expectedNumberOfArguments + " arguments, but got " + actualNumberOfArguments + ".";
    }
}
