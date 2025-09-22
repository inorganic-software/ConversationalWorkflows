package com.inorganic.tools.exceptions;

public class ToolException extends RuntimeException {
    private final String errorType;

    public ToolException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ToolException(String message, String errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }
}