package com.axorean.reconciliation_service.model;

import java.util.List;

public record DetailMismatchedTransaction (List<SystemTransaction> systemTransactions,
                                           List<BankStatements> bankStatementsList) {
}
