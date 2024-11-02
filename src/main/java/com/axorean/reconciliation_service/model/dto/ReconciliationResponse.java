package com.axorean.reconciliation_service.model.dto;

import com.axorean.reconciliation_service.model.DetailMismatchedTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationResponse {
    private long totalTransactions;
    private long totalMatchedTransaction;
    private long totalMisMatchedTransactions;
    private Double totalDiscrepancy;
    private DetailMismatchedTransaction detailMismatchedTransaction;
}
