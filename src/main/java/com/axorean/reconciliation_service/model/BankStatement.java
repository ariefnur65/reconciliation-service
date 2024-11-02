package com.axorean.reconciliation_service.model;

import java.util.Date;

public record BankStatement(String unique_identifier,
                            Double amount,
                            Date date) {
}
