package com.axorean.reconciliation_service.model;

import java.util.Date;

public record SystemTransaction(String trxId,
                                Double amount,
                                TransactionType type,
                                Date transactionTime) {
}
