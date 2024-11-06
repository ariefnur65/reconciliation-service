package com.axorean.reconciliation_service.model;

import java.util.List;

public record BankStatements (String filePath,
                              List<BankStatement> statements) {
}
