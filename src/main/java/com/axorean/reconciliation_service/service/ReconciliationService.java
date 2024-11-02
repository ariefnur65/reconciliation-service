package com.axorean.reconciliation_service.service;

import com.axorean.reconciliation_service.model.dto.ReconciliationRequest;
import com.axorean.reconciliation_service.model.dto.ReconciliationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReconciliationService {

    public ReconciliationResponse reconcileData(ReconciliationRequest request) {

        return ReconciliationResponse.builder().build();
    }
}
