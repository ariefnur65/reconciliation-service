package com.axorean.reconciliation_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationRequest implements Serializable {
    private String systemTransactionPath;
    private String[] bankStatements;
    private Date startDate;
    private Date endDate;
}
