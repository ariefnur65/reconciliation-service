package com.axorean.reconciliation_service.exception;

public class BankStatementFileInvalidException extends Exception {
    public BankStatementFileInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
